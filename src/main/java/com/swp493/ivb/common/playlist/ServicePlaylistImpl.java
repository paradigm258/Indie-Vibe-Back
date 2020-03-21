package com.swp493.ivb.common.playlist;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.NoPermissionException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.relationship.EntityUserPlaylist;
import com.swp493.ivb.common.relationship.RepositoryPlaylistTrack;
import com.swp493.ivb.common.relationship.RepositoryUserPlaylist;
import com.swp493.ivb.common.track.DTOTrackPlaylist;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.common.track.RepositoryTrack;
import com.swp493.ivb.common.track.ServiceTrack;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.config.AWSConfig;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServicePlaylistImpl implements ServicePlaylist {

    private static Logger log = LoggerFactory.getLogger(ServicePlaylist.class);

    @Autowired
    private ServiceTrack trackService;

    @Autowired
    private RepositoryPlaylist playlistRepo;

    @Autowired
    private RepositoryUser userRepo;

    @Autowired
    private RepositoryTrack trackRepo;

    @Autowired
    private RepositoryUserPlaylist userPlaylistRepo;

    @Autowired
    private RepositoryPlaylistTrack playlistTrackRepo;

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

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            MultipartFile thumbnail = playlistInfo.getThumbnail();

            if (thumbnail != null) {
                s3.putObject(
                    new PutObjectRequest(
                        AWSConfig.BUCKET_NAME, 
                        playlistId, 
                        thumbnail.getInputStream(), 
                        metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                );
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
            throw e;
        }
    }

    @Override
    public boolean deletePlaylist(String playlistId, String userId) throws Exception {
        try {
            if (playlistRepo.existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(playlistId, userId, "own")) {
                EntityPlaylist playlist = playlistRepo.findById(playlistId).get();
                playlistRepo.deleteById(playlistId);
                if(playlist.getThumbnail() != null && !playlist.getThumbnail().isEmpty())
                s3.deleteObject(AWSConfig.BUCKET_NAME, playlist.getThumbnail());
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
    public Paging<DTOPlaylistSimple> getPlaylists(String userId, boolean getPrivate, int offset, int limit) {
                             
        Paging<DTOPlaylistSimple> paging = new Paging<>();

        paging.setPageInfo(userPlaylistRepo.countByUserIdAndPlaylistNotNull(userId), limit, offset);
        Pageable pageable = paging.getPageable();
        List<EntityUserPlaylist> list = getPrivate ?  userPlaylistRepo.findByUserIdAndPlaylistNotNull(userId, pageable)
                                                    : userPlaylistRepo.findByPlaylistStatusAndUserId("public", userId, pageable);
        paging.setItems(list.stream().map(l -> {
            ModelMapper mapper = new ModelMapper();
            EntityPlaylist playlist = l.getPlaylist();
            DTOPlaylistSimple simple = mapper.map(playlist, DTOPlaylistSimple.class);
            simple.setOwner(mapper.map(playlist.getOwner().get(0).getUser(), DTOUserPublic.class));
            simple.setTracksCount(playlistTrackRepo.countByPlaylistId(playlist.getId()));
            simple.setRelation(userPlaylistRepo.getRelation(userId,playlist.getId()));
            return simple;
        }).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public Optional<DTOPlaylistFull> getPlaylistFull(String playlistId, String userId, int offset, int limit) throws Exception {
        EntityPlaylist playlist = playlistRepo.findById(playlistId).get();

        if (!hasPlaylistAccessPermission(playlistId, userId)) throw new NoPermissionException();

        ModelMapper mapper = new ModelMapper();
        DTOPlaylistFull playlistFull = mapper.map(playlist, DTOPlaylistFull.class);
        
        playlistFull.setOwner(mapper.map(playlist.getOwner().get(0).getUser(), DTOUserPublic.class));
        
        playlistFull.setFollowersCount(userPlaylistRepo.countByPlaylistIdAndAction(playlistId,"favorite"));
        playlistFull.setRelation(userPlaylistRepo.getRelation(userId, playlistId));
        playlistFull.setTracksCount(playlistTrackRepo.countByPlaylistId(playlistId));

        Paging<DTOTrackPlaylist> paging = new Paging<>();
        paging.setPageInfo(playlistFull.getTracksCount(), limit, offset);
        Pageable pageable = paging.getPageable();
        
        paging.setItems(
            playlistTrackRepo.findByPlaylistId(playlistId, pageable).stream().map(track -> {
                DTOTrackPlaylist trackPlaylist = new DTOTrackPlaylist();
                trackPlaylist.setAddedAt(track.getInsertedDate());
                trackPlaylist.setTrack(trackService.getTrackFullFromEntity(track.getTrack(), userId).get());
                return trackPlaylist;
            }).collect(Collectors.toList())
        );

        playlistFull.setTracks(paging);
        return Optional.of(playlistFull);
    }

    @Override
    public Optional<DTOPlaylistSimple> getPlaylistSimple(String playlistId, String userId) throws Exception {
        EntityPlaylist playlist = playlistRepo.findById(playlistId).get();

        if(!hasPlaylistAccessPermission(playlistId, userId)) return Optional.empty();

        ModelMapper mapper = new ModelMapper();
        DTOPlaylistSimple playlistSimple = mapper.map(playlist, DTOPlaylistSimple.class);
        playlistSimple.setOwner(mapper.map(playlist.getOwner().get(0).getUser(), DTOUserPublic.class));
        playlistSimple.setTracksCount(playlist.getPlaylistTracks().size());
        playlistSimple.setRelation(userPlaylistRepo.getRelation(playlistId, userId));
        return Optional.of(playlistSimple);
    }

    @Override
    public boolean actionPlaylistTrack(String trackId, String playlistId, String action, String userId) throws Exception {
        EntityPlaylist playlist = playlistRepo.findById(playlistId).get();
        EntityTrack track = trackRepo.findById(trackId).get();

        if( !hasPlaylistAccessPermission(playlistId, userId) ||
            !hasTrackAccessPermission(trackId, userId)) throw new NoPermissionException();
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
            playlistRepo.save(playlist);
            return true;
        }
        return false;
    }

    @Override
    public boolean actionPlaylist(String playlistId, String userId, String action) throws Exception {
        EntityPlaylist playlist = playlistRepo.findById(playlistId).get();
        EntityUser user = userRepo.findById(userId).get();

        if(!hasPlaylistAccessPermission(playlistId, userId)) throw new NoPermissionException();
        
        boolean success = false;
        switch (action) {
            case "favorite":
                success = user.favoritePlaylist(playlist);
                break;
            case "unfavorite":
                success = user.unfavoritePlaylist(playlist);
                break;
            case "make-public":
                if(userPlaylistRepo.existsByUserIdAndPlaylistIdAndAction(userId, playlistId, "own")){
                    playlist.setStatus("public");
                    success = true;
                }
                break;
            case "make-private":
                if(userPlaylistRepo.existsByUserIdAndPlaylistIdAndAction(userId, playlistId, "own")){
                    playlist.setStatus("private");
                    success = true;
                }
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

    private boolean hasPlaylistAccessPermission(String playlistId, String userId) {
        return  playlistRepo.existsByIdAndStatus(playlistId,"public")
            ||  userPlaylistRepo.existsByUserIdAndPlaylistIdAndAction(playlistId, userId, "own");   
    }

    private boolean hasTrackAccessPermission(String trackId, String userId){
        return  trackRepo.existsByIdAndStatus(trackId,"public")
            ||  trackRepo.existsByIdAndTrackUsersUserIdAndTrackUsersAction(trackId,userId,"own");
    }

    @Override
    public List<String> playlistStream(String playlistId, String userId) throws Exception{
        if(!hasPlaylistAccessPermission(playlistId, userId)) throw new NoPermissionException();
        return playlistRepo.findAllTrackIdById(playlistId); 
    }
    
}
