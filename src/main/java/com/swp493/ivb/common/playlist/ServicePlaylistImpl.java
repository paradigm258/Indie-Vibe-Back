package com.swp493.ivb.common.playlist;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.swp493.ivb.common.user.IOnlyId;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.config.AWSConfig;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServicePlaylistImpl implements ServicePlaylist {

    private static Logger log = LoggerFactory.getLogger(ServicePlaylist.class);

    @Autowired
    private ServiceTrack trackService;

    @Autowired
    private ServiceUser userService;

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
    public String createPlaylist(DTOPlaylistCreate playlistInfo, String userId) throws IOException {
        EntityPlaylist playlist = new EntityPlaylist();
        playlist.setTitle(playlistInfo.getTitle());
        playlist.setDescription(playlistInfo.getDescription());
        playlist.setStatus("public");
        playlist = playlistRepo.save(playlist);
        String playlistId = playlist.getId();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            MultipartFile thumbnail = playlistInfo.getThumbnail();

            if (thumbnail != null && !thumbnail.isEmpty()) {
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
        } catch (IOException e) {
            if (playlistId != null && !playlistId.isEmpty()) {
                playlistRepo.delete(playlist);
                if(playlist.getThumbnail() != null && !playlist.getThumbnail().isEmpty())
                s3.deleteObject(AWSConfig.BUCKET_NAME, playlist.getThumbnail());
            }
            log.error("Error create playlist", e);
            throw e;
        }
    }

    @Override
    public boolean deletePlaylist(String playlistId, String userId){
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
    public Paging<DTOPlaylistSimple> getPlaylists(String userId, String viewerId, int offset, int limit, String type) {
                             
        Paging<DTOPlaylistSimple> paging = new Paging<>();
        boolean privateView = userId.equals(viewerId);
        int total = privateView ? userPlaylistRepo.countByUserIdAndPlaylistNotNullAndAction(userId,type) 
                                : userPlaylistRepo.countByUserIdAndPlaylistStatusAndAction(userId, "public", type);
        paging.setPageInfo(total, limit, offset);
        Pageable pageable = paging.asPageable();
        List<EntityUserPlaylist> list = privateView ? userPlaylistRepo.findByUserIdAndPlaylistNotNullAndAction(userId, type, pageable)
                                                    : userPlaylistRepo.findByPlaylistStatusAndUserIdAndAction("public", userId, type, pageable);
        
        paging.setItems(list.stream().map(pl -> getPlaylistSimple(pl.getPlaylist(), viewerId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public DTOPlaylistFull getPlaylistFull(EntityPlaylist playlist, String userId, int offset, int limit) {
        
        if (!hasPlaylistAccessPermission(playlist.getId(), userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        ModelMapper mapper = new ModelMapper();
        DTOPlaylistFull playlistFull = mapper.map(playlist, DTOPlaylistFull.class);
        
        playlistFull.setOwner(userService.getUserPublic(playlist.getOwner().get(0).getUser().getId(), userId));
        
        playlistFull.setFollowersCount(userPlaylistRepo.countByPlaylistIdAndAction(playlist.getId(),"favorite"));
        playlistFull.setRelation(userPlaylistRepo.getRelation(userId, playlist.getId()));
        playlistFull.setTracksCount(playlistTrackRepo.countByPlaylistId(playlist.getId()));

        Paging<DTOTrackPlaylist> paging = new Paging<>();
        paging.setPageInfo(playlistFull.getTracksCount(), limit, offset);
        Pageable pageable = paging.asPageable();
        
        paging.setItems(
            playlistTrackRepo.findByPlaylistId(playlist.getId(), pageable).stream().map(track -> {
                DTOTrackPlaylist trackPlaylist = new DTOTrackPlaylist();
                trackPlaylist.setAddedAt(track.getInsertedDate());
                trackPlaylist.setTrack(trackService.getTrackFullFromEntity(track.getTrack(), userId));
                return trackPlaylist;
            }).collect(Collectors.toList())
        );

        playlistFull.setTracks(paging);
        return playlistFull;
    }

    @Override
    public DTOPlaylistFull getPlaylistFull(String playlistId, String userId, int offset, int limit) {
        EntityPlaylist playlist = playlistRepo.findById(playlistId).get();
        return getPlaylistFull(playlist, userId, offset, limit);
    }

    @Override
    public DTOPlaylistSimple getPlaylistSimple(String playlistId, String userId)  {
        EntityPlaylist playlist = playlistRepo.findById(playlistId).get();
        return getPlaylistSimple(playlist, userId);
    }

    @Override
    public DTOPlaylistSimple getPlaylistSimple(EntityPlaylist playlist, String userId) {
        if(!hasPlaylistAccessPermission(playlist.getId(), userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        ModelMapper mapper = new ModelMapper();
        DTOPlaylistSimple playlistSimple = mapper.map(playlist, DTOPlaylistSimple.class);
        playlistSimple.setOwner(mapper.map(playlist.getOwner().get(0).getUser(), DTOUserPublic.class));
        playlistSimple.setTracksCount(playlist.getPlaylistTracks().size());
        playlistSimple.setRelation(userPlaylistRepo.getRelation(userId, playlist.getId()));
        return playlistSimple;
    }

    @Override
    public boolean actionPlaylistTrack(String trackId, String playlistId, String action, String userId) {
        EntityPlaylist playlist = playlistRepo.findById(playlistId).get();
        EntityTrack track = trackRepo.findById(trackId).get();

        if( !hasPlaylistAccessPermission(playlistId, userId)) new ResponseStatusException(HttpStatus.FORBIDDEN);
        if( !hasTrackAccessPermission(trackId, userId)) new ResponseStatusException(HttpStatus.FORBIDDEN);
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
    public boolean actionPlaylist(String playlistId, String userId, String action) {
        EntityPlaylist playlist = playlistRepo.findById(playlistId).get();
        EntityUser user = userRepo.findById(userId).get();

        if(!hasPlaylistAccessPermission(playlistId, userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
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
            ||  userPlaylistRepo.existsByUserIdAndPlaylistIdAndAction(userId , playlistId, "own");   
    }

    private boolean hasTrackAccessPermission(String trackId, String userId){
        return  trackRepo.existsByIdAndStatus(trackId,"public")
            ||  trackRepo.existsByIdAndTrackUsersUserIdAndTrackUsersAction(trackId,userId,"own");
    }

    @Override
    public List<String> playlistStream(String playlistId, String userId){
        if(!hasPlaylistAccessPermission(playlistId, userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return playlistRepo.findAllTrackIdById(playlistId); 
    }

    @Override
    public Paging<DTOPlaylistSimple> findPlaylist(String key, String userId, int offset, int limit) {
        int total = playlistRepo.countByTitleIgnoreCaseContainingAndStatus(key,"public");
        Paging<DTOPlaylistSimple> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = playlistRepo.findByTitleIgnoreCaseContainingAndStatus(key,"public", paging.asPageable());
        paging.setItems(list.stream().map(t -> getPlaylistSimple(t.getId(), userId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public Paging<DTOPlaylistSimple> getGenrePlaylists(String genreId, String userId, int offset, int limit) {
        int total = playlistRepo.countByUserPlaylistsUserUserRoleIdAndGenresIdAndStatus("r-curator", genreId, "public");
        Paging<DTOPlaylistSimple> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = playlistRepo.findByUserPlaylistsUserUserRoleIdAndGenresIdAndStatus("r-curator", genreId, "public", paging.asPageable());
        paging.setItems(list.stream().map(id -> getPlaylistSimple(id.getId(), userId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public boolean updatePlaylist(String userId, String playlistId, DTOPlaylistUpdate playlistUpdate) {
        if(!userPlaylistRepo.existsByUserIdAndPlaylistIdAndAction(userId , playlistId, "own"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        EntityPlaylist playlist = playlistRepo.getOne(playlistId);
        if(StringUtils.hasLength(playlistUpdate.getDescription())){
            playlist.setDescription(playlistUpdate.getDescription());
        }
        if(StringUtils.hasLength(playlistUpdate.getTitle())){
            playlist.setDescription(playlistUpdate.getTitle());
        }
        MultipartFile thumbnail = playlistUpdate.getThumbnail();
        if (thumbnail != null) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(thumbnail.getSize());
            String key = playlistId;
            try {
                s3.putObject(new PutObjectRequest(AWSConfig.BUCKET_NAME, key, thumbnail.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                playlist.setThumbnail(AWSConfig.BUCKET_URL+key);
            } catch (IOException e) {
                throw new RuntimeException("Error getting input stream for thumbnail",e);
            }
        }
        playlistRepo.save(playlist);
        return true;
    }
    
}
