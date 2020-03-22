package com.swp493.ivb.common.track;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.swp493.ivb.common.artist.DTOArtistSimple;
import com.swp493.ivb.common.relationship.EntityUserRelease;
import com.swp493.ivb.common.relationship.EntityUserTrack;
import com.swp493.ivb.common.relationship.RepositoryUserTrack;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.view.Paging;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ServiceTrackImpl implements ServiceTrack {

    @Autowired
    private RepositoryTrack trackRepo;

    @Autowired
    private RepositoryUser userRepo;

    @Autowired
    private RepositoryUserTrack userTrackRepo;

    @Override
    public DTOTrackStreamInfo getTrackStreamInfo(String id, int bitrate, String userId) {
        EntityTrack track = trackRepo.findById(id).get();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        DTOTrackFull info = getTrackFullFromEntity(track, userId);
        DTOTrackStreamInfo trackStreamInfo = mapper.map(track, DTOTrackStreamInfo.class);
        trackStreamInfo.setInfo(info);
        switch (bitrate) {
            case 128:
                trackStreamInfo.setUrl(track.getMp3128());
                break;
            case 320:
                trackStreamInfo.setUrl(track.getMp3320());
                break;
            default:
                break;
        }
        
        return trackStreamInfo;
    }

    @Override
    public boolean favoriteTrack(String userId, String trackId) {
        return actionTrack(userId, trackId, "favorite");
    }

    @Override
    public boolean unfavoriteTrack(String userId, String trackId) {
        return actionTrack(userId, trackId, "unfavorite");
    }

    private boolean actionTrack(String userId, String trackId, String action) {
        EntityUser user = userRepo.findById(userId).get();
        EntityTrack track = trackRepo.findById(trackId).get();
            switch (action) {
                case "unfavorite":
                    if (user.unfavoriteTracks(track)) {
                        trackRepo.flush();
                    }
                    break;
                case "favorite":
                    if (user.favoriteTracks(track)) {
                        trackRepo.flush();
                    }
                    break;
                default:
                    break;
            }
            return true;
    }

    @Override
    public Paging<DTOTrackFull> getTracks(String userId, String viewerId, int offset, int limit, String type) {
        Paging<DTOTrackFull> paging = new Paging<>();

        paging.setPageInfo(0, limit, offset);
        Pageable pageable = paging.asPageable();
        List<EntityUserTrack> list = userId.equals(viewerId)?userTrackRepo.findAllByUserIdAndTrackNotNullAndAction(userId, type, pageable)
                                                            :userTrackRepo.findAllByUserIdAndTrackStatusAndAction(userId, "public", type, pageable);
        paging.setTotal(list.size());

        paging.setItems(list.stream().map(ut ->{
            return getTrackFullFromEntity(ut.getTrack(), userId);
        }).collect(Collectors.toList()));

        return paging;
    }

    @Override
    public DTOTrackFull getTrackById(String id, String userId) {
        if(!hasTrackAccessPermission(id,userId)) throw new AccessDeniedException(id);
        return getTrackFullFromEntity(trackRepo.findById(id).get(),userId);
    }

    public DTOTrackFull getTrackFullFromEntity(EntityTrack track, String userId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        DTOTrackFull res = mapper.map(track, DTOTrackFull.class);
        res.setRelation(userTrackRepo.getRelation(userId,track.getId()));

        // set artists who own or featured the track
        Set<EntityUserTrack> trackArtists = track.getArtist();
        res.setArtists(trackArtists.stream().map(ut -> mapper.map(ut.getUser(), DTOArtistSimple.class))
                .collect(Collectors.toSet()));

        // set artist who own the release (that the track belongs to)
        Optional<EntityUserRelease> releaseOwner = Optional.of(track.getRelease().getArtistRelease().get(0));
        res.setRelease(releaseOwner.map(ro -> {
            DTOReleaseSimple resRelease = mapper.map(ro.getRelease(), DTOReleaseSimple.class);
            resRelease.setArtist(mapper.map(ro.getUser(), DTOArtistSimple.class));
            return resRelease;
        }).orElse(null));

        return res;
    }

    @Override
    public boolean hasTrackAccessPermission(String trackId, String userId){
        return  trackRepo.existsByIdAndStatus(trackId,"public")
            ||  userTrackRepo.existsByTrackIdAndUserIdAndAction(trackId,userId,"own");
    }

    @Override
    public List<String> streamFavorite(String userId) {
        return trackRepo.getFavIdList(userId);
    }

}
