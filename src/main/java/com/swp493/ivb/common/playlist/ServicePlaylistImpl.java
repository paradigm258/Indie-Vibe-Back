package com.swp493.ivb.common.playlist;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.track.DTOTrackFull;
import com.swp493.ivb.common.track.DTOTrackPlaylist;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.common.track.RepositoryTrack;
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
    private RepositoryTrack trackRepo;

    @Autowired
    private AmazonS3 s3;

    @Override
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
            if (thumbnail != null) {
                s3.putObject(
                        new PutObjectRequest(AWSConfig.BUCKET_NAME, playlistId, thumbnail.getInputStream(), metadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));
                playlist.setThumbnail(AWSConfig.BUCKET_URL + playlistId);
            }

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

    @Override
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
    public List<DTOPlaylistSimple> getPlaylists(String userId, boolean getPrivate, int offset, int limit) {
        List<EntityPlaylist> list = getPrivate ? playlistRepo.findByUserPlaylistsUserId(userId)
                                               : playlistRepo.findByStatusAndUserPlaylistsUserId("public", userId);
        int total = list.size();
        offset = offset<total && offset>=0 ? offset : total;
        limit = limit<total && limit>=offset ? limit : total;                                       
        return list.subList(offset, limit).stream().map(l -> {
            ModelMapper mapper = new ModelMapper();
            DTOPlaylistSimple simple = mapper.map(l, DTOPlaylistSimple.class);
            simple.setOwner(mapper.map(l.getOwner().get(0).getUser(), DTOUserPublic.class));
            simple.setTracksCount(l.getPlaylistTracks().size());
            simple.setRelation(l.getUserPlaylists().stream().map(eul -> {
                if (eul.getUser().getId().equals(userId)) {
                    return eul.getAction();
                } else {
                    return "";
                }
            }).collect(Collectors.toSet()));
            return simple;
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<DTOPlaylistFull> getPlaylistFull(String playlistId, String userId, int offset, int limit) throws Exception {
        Optional<EntityPlaylist> oPlaylist = playlistRepo.findById(playlistId);

        if (!oPlaylist.isPresent())
            return Optional.empty();

        EntityPlaylist playlist = oPlaylist.get();

        // Return empty optional if playlist is not public and user have no permission
        if (!playlist.getStatus().equals("public")
                && !playlistRepo.existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(playlistId, userId, "own"))
            return Optional.empty();

        EntityUser user = userRepo.findById(userId).get();

        List<EntityPlaylistTrack> tracks = playlist.getPlaylistTracks();
        ModelMapper mapper = new ModelMapper();
        DTOPlaylistFull playlistFull = mapper.map(playlist, DTOPlaylistFull.class);
        playlistFull.setOwner(mapper.map(playlist.getOwner().get(0).getUser(), DTOUserPublic.class));
        playlistFull.setTracksCount(tracks.size());
        playlistFull.setFollowersCount(playlist.getUserPlaylists().size());
        playlistFull.setRelation(playlist.getUserPlaylists().stream().map(eul -> {
            if (eul.getUser().getId().equals(userId)) {
                return eul.getAction();
            } else {
                return "";
            }
        }).collect(Collectors.toSet()));
        
        Paging<DTOTrackPlaylist> paging = new Paging<>();
        int total = tracks.size();
        offset = offset<total && offset>=0 ? offset : total;
        limit = limit<total && limit>=offset ? limit : total;
        paging.setTotal(total);
        paging.setOffset(offset);
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

    @Override
    public Optional<DTOPlaylistSimple> getPlaylistSimple(String playlistId, String userId) throws Exception {
        Optional<EntityPlaylist> oPlaylist = playlistRepo.findById(playlistId);
        if (!oPlaylist.isPresent())
            return Optional.empty();

        EntityPlaylist playlist = oPlaylist.get();

        // Return empty optional if playlist is not public and user have no permission
        if (!playlist.getStatus().equals("public")
                && !playlistRepo.existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(playlistId, userId, "own"))
            return Optional.empty();

        ModelMapper mapper = new ModelMapper();
        DTOPlaylistSimple playlistSimple = mapper.map(playlist, DTOPlaylistFull.class);
        playlistSimple.setOwner(mapper.map(playlist.getOwner().get(0).getUser(), DTOUserPublic.class));
        playlistSimple.setTracksCount(playlist.getPlaylistTracks().size());
        playlistSimple.setRelation(playlist.getUserPlaylists().stream().map(eul -> {
            if (eul.getUser().getId().equals(userId)) {
                return eul.getAction();
            } else {
                return "";
            }
        }).collect(Collectors.toSet()));
        return Optional.of(playlistSimple);
    }

    public boolean actionPlaylistTrack(String trackId, String playlistId, String action, String userId){
        Optional<EntityPlaylist> oPlaylist = playlistRepo.findById(playlistId);
        if(!oPlaylist.isPresent()) return false;
        EntityPlaylist playlist = oPlaylist.get();
        Optional<EntityTrack> oTrack = trackRepo.findById(trackId);
        if(!oTrack.isPresent()) return false;
        EntityTrack track = oTrack.get();

        if( !(playlist.getStatus().equals("public")|| playlistRepo.existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(playlistId,userId,"own"))&&
            !(track.getStatus().equals("public") || trackRepo.existsByIdAndTrackUsersUserIdAndTrackUsersAction(trackId,userId,"own"))
            ) return false;
        boolean success = false;
        switch (action) {
            case "add":
                success = playlist.addTrack(track);
                playlist.getGenres().addAll(track.getGenres());
                break;
            case "remove":
                success = playlist.removeTrack(track);
                Set<EntityMasterData> newGenres = new HashSet<>();
                playlist.getPlaylistTracks().stream().forEach(pt ->{
                    newGenres.addAll(pt.getTrack().getGenres());
                });
                playlist.setGenres(newGenres);
                break;
            default:
                break;
        }
        if(success){
            playlistRepo.flush();
            return true;
        }
        return false;
    }

    @Override
    public boolean actionPlaylist(String playlistId, String userId, String action) {
        Optional<EntityPlaylist> oPlaylist = playlistRepo.findById(playlistId);
        if(!oPlaylist.isPresent()) return false;
        EntityPlaylist playlist = oPlaylist.get();

        Optional<EntityUser> oUser = userRepo.findById(userId);
        if(!oUser.isPresent()) return false;
        EntityUser user = oUser.get();

        if(!(playlist.getStatus().equals("public")
        || playlistRepo.existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(playlistId,userId,"own")))
        return false;
        boolean success = false;
        switch (action) {
            case "favorite":
                success = user.favoritePlaylist(playlist);
                break;
            case "unfavorite":
                success = user.unfavoritePlaylist(playlist);
                break;
            default:
                break;
        }
        if(success){
            userRepo.flush();
            return true;
        }
        return false;

    }
}
