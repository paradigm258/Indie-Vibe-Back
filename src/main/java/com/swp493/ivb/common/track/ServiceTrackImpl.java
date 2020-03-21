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

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
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
    public DTOTrackStreamInfo getTrackStreamInfo(String id, int bitrate, String userId) throws Exception {
        EntityTrack track = trackRepo.findById(id).get();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        DTOTrackFull info = getTrackFullFromEntity(track, userId).map(t -> t).orElse(null);
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
        EntityUser user = userRepo.findById(userId).orElse(null);
        EntityTrack track = trackRepo.findById(trackId).orElse(null);
        if (user != null && track != null) {
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
        return false;
    }

    @Override
    public Optional<List<DTOTrackSimple>> getUserFavorites(String userId) {
        return userRepo.findById(userId).map(user -> {
            return user.getUserFavoriteTracks().stream().map(track -> {
                ModelMapper mapper = new ModelMapper();
                DTOTrackSimple trackSimple = mapper.map(track.getTrack(), DTOTrackSimple.class);
                trackSimple.setDuration(track.getTrack().getDuration());
                return trackSimple;
            }).collect(Collectors.toList());
        });
    }

    @Override
    public Optional<DTOTrackFull> getTrackById(String id, String userId) {
        return getTrackFullFromEntity(trackRepo.findById(id).get(),userId);
    }

    public Optional<DTOTrackFull> getTrackFullFromEntity(EntityTrack track, String userId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        DTOTrackFull res = mapper.map(track, DTOTrackFull.class);
        res.setDuration(track.getDuration());
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

        return Optional.of(res);
    }

}
