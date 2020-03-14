package com.swp493.ivb.common.playlist;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.swp493.ivb.common.track.DTOTrackFull;
import com.swp493.ivb.common.track.DTOTrackPlaylist;
import com.swp493.ivb.common.track.ServiceTrackImpl;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.EntityUserPlaylist;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.config.AWSConfig;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServicePlaylistImpl implements ServicePlaylist {

    private static Logger log = LoggerFactory.getLogger(ServicePlaylist.class);

    @Autowired
    private RepositoryPlaylist playlistRepo;

    @Autowired
    private RepositoryUser userRepo;

    @Autowired
    private AmazonS3 s3;

    public String createPlaylist(DTOPlaylistCreate playlistInfo, String userId) throws Exception {
        EntityPlaylist playlist = new EntityPlaylist();
        playlist.setTitle(playlistInfo.getTitle());
        playlist.setDescription(playlistInfo.getDescription());
        playlist.setStatus("public");
        playlist = playlistRepo.save(playlist);
        String playlistId = playlist.getId();

        ObjectMetadata metadata = new ObjectMetadata();
        MultipartFile thumbnail = playlistInfo.getThumbnail();
        try {
            s3.putObject(new PutObjectRequest(AWSConfig.BUCKET_NAME, playlistId, thumbnail.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            playlist.setThumbnail(AWSConfig.BUCKET_URL + playlistId);

            EntityUserPlaylist eup = new EntityUserPlaylist();
            eup.setAction("own");
            eup.setUser(userRepo.findById(userId).get());
            eup.setPlaylist(playlist);

            playlist.getUserPlaylists().add(eup);
            playlistRepo.flush();
            return playlistId;
        } catch (Exception e) {
            if (playlistId != null && !playlistId.isEmpty()) {
                playlistRepo.delete(playlist);
            }
            log.error("Error create playlist", e);
            throw new Exception(e);
        }
    }

    public boolean deletePlaylist(String playlistId, String userId) throws Exception {
        try {
            if (playlistRepo.existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(playlistId, userId, "own")) {
                playlistRepo.deleteById(playlistId);
                s3.deleteObject(AWSConfig.BUCKET_NAME, playlistId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to delete playlist: " + playlistId, e);
            throw e;
        }
    }

    @Override
    public List<DTOPlaylistSimple> getPlaylists(String userId, boolean getPrivate, int pageIndex) {
        List<EntityPlaylist> list = getPrivate ? playlistRepo.findByUserPlaylistsUserId(userId)
                                               : playlistRepo.findByStatusAndUserPlaylistsUserId("public", userId);
        return list.subList(pageIndex * 5, pageIndex * 5 + 9).stream().map(l -> {
            ModelMapper mapper = new ModelMapper();
            DTOPlaylistSimple simple = mapper.map(l, DTOPlaylistSimple.class);
            simple.setOwner(mapper.map(l.getOwner().get(0).getUser(), DTOUserPublic.class));
            simple.setTracksCount(l.getTrackPlaylist().size());
            return simple;
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<DTOPlaylistFull> getPlaylistFull(String playlistId, String userId, int pageIndex) throws Exception {
        Optional<EntityPlaylist> oPlaylist = playlistRepo.findById(playlistId);

        if (!oPlaylist.isPresent())
            return Optional.empty();

        EntityPlaylist playlist = oPlaylist.get();

        // Return empty optional if playlist is not public and user have no permission
        if (!playlist.getStatus().equals("public")
                && !playlistRepo.existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(playlistId, userId, "own"))
            return Optional.empty();

        EntityUser user = userRepo.findById(userId).get();

        List<EntityPlaylistTrack> tracks = playlist.getTrackPlaylist();
        ModelMapper mapper = new ModelMapper();
        DTOPlaylistFull playlistFull = mapper.map(playlist, DTOPlaylistFull.class);
        playlistFull.setOwner(mapper.map(playlist.getOwner().get(0).getUser(), DTOUserPublic.class));
        playlistFull.setFollowersCount(playlist.getUserPlaylists().size());

        Paging<DTOTrackPlaylist> paging = new Paging<>();
        paging.setTotal(tracks.size());
        paging.setOffset(pageIndex * 5);
        int limit = paging.getOffset() + 4 < paging.getTotal() ? paging.getOffset() : paging.getTotal();
        paging.setLimit(limit);
        paging.setItems(tracks.subList(paging.getOffset(), paging.getLimit()).stream().map(track -> {
            DTOTrackPlaylist trackPlaylist = new DTOTrackPlaylist();
            trackPlaylist.setAddedAt(track.getInsertedDate());
            DTOTrackFull trackFull = ServiceTrackImpl.getTrackFullFromEntity(track.getTrack(), user).get();
            trackPlaylist.setTrack(trackFull);
            return trackPlaylist;
        }).collect(Collectors.toList()));

        playlistFull.setTracks(paging);
        return Optional.of(playlistFull);
    }
}
