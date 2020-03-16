package com.swp493.ivb.common.track;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swp493.ivb.common.artist.DTOArtistSimple;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.EntityUserRelease;
import com.swp493.ivb.common.user.EntityUserTrack;
import com.swp493.ivb.common.user.RepositoryUser;

@Service
public class ServiceTrackImpl implements ServiceTrack {

    @Autowired
    private RepositoryTrack trackRepo;

    @Autowired
    private RepositoryUser userRepo;

    @Override
    public Optional<DTOTrackStreamInfo> getTrackStreamInfo(String id, int bitrate, String userId) {
        Optional<EntityTrack> trackEntity = trackRepo.findById(id);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        TypeMap<EntityTrack, DTOTrackStreamInfo> typeMap = mapper.createTypeMap(EntityTrack.class,
                DTOTrackStreamInfo.class);

        switch (bitrate) {
            case 128:
                typeMap.addMappings(m -> {
                    m.map(src -> src.getMp3128(), DTOTrackStreamInfo::setUrl);
                    m.map(src -> src.getDuration128(), DTOTrackStreamInfo::setDuration);
                    m.map(src -> src.getFileSize128(), DTOTrackStreamInfo::setFileSize);
                });
                break;
            case 320:
                typeMap.addMappings(m -> {
                    m.map(src -> src.getMp3320(), DTOTrackStreamInfo::setUrl);
                    m.map(src -> src.getDuration320(), DTOTrackStreamInfo::setDuration);
                    m.map(src -> src.getFileSize320(), DTOTrackStreamInfo::setFileSize);
                });
                break;
            default:
                return Optional.empty();
        }
        EntityUser user = userRepo.findById(userId).get();
        return trackEntity.map(track -> {
            DTOTrackFull info = getTrackFullFromEntity(track, user).map(t -> t).orElse(null);
            DTOTrackStreamInfo trackStreamInfo = mapper.map(track, DTOTrackStreamInfo.class);
            trackStreamInfo.setInfo(info);
            return trackStreamInfo;
        });
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
                        trackRepo.save(track);
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
    public Optional<List<DTOTrackSimple>> getFavorites(String userId) {
        return userRepo.findById(userId).map(user -> {
            return user.getUserFavoriteTracks().stream().map(track -> {
                ModelMapper mapper = new ModelMapper();
                mapper.addMappings(new PropertyMap<EntityTrack, DTOTrackSimple>() {
                    @Override
                    protected void configure() {
                        map().setDuration(track.getTrack().getDuration320());
                    }
                });
                return mapper.map(track.getTrack(), DTOTrackSimple.class);
            }).collect(Collectors.toList());
        });
    }

    @Override
    public Optional<DTOTrackFull> getTrackById(String id, String userId) {
        Optional<EntityTrack> track = trackRepo.findById(id);
        if (track.isPresent()) {
            Optional<EntityUser> user = userRepo.findById(userId);
            return getTrackFullFromEntity(track.get(), user.get());
        }

        return Optional.empty();
    }

    public static Optional<DTOTrackFull> getTrackFullFromEntity(EntityTrack track, EntityUser user) {
        ModelMapper mapper = new ModelMapper();
        DTOTrackFull res = mapper.map(track, DTOTrackFull.class);
        res.setDuration(track.getDuration320());
        res.setRelation(track.getTrackUsers().stream().map(eut -> {
            if (eut.getUser().equals(user)) {
                return eut.getAction();
            } else {
                return "";
            }
        }).collect(Collectors.toSet()));

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
