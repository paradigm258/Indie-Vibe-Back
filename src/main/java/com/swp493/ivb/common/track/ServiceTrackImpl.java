package com.swp493.ivb.common.track;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.RepositoryUser;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceTrackImpl implements ServiceTrack {

    @Autowired
    private RepositoryTrack trackRepo;

    @Autowired
    private RepositoryUser userRepo;

    @Override
    public Optional<DTOTrackFull> getTrackById(String id) {
        Optional<EntityTrack> trackEntity = trackRepo.findById(id);

        return trackEntity.map(track -> {
            ModelMapper mapper = new ModelMapper();
            return mapper.map(track, DTOTrackFull.class);
        });
    }

    @Override
    public Optional<DTOTrackStreamInfo> getTrackStreamInfo(String id, int bitrate) {
        Optional<EntityTrack> trackEntity = trackRepo.findById(id);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        TypeMap<EntityTrack, DTOTrackStreamInfo> typeMap = mapper.createTypeMap(EntityTrack.class,
                DTOTrackStreamInfo.class);

        switch (bitrate) {
            case 128:
                typeMap.addMappings(m -> {
                    m.map(src -> src.getDuration128(), DTOTrackStreamInfo::setDuration);
                    m.map(src -> src.getFileSize128(), DTOTrackStreamInfo::setFileSize);
                });
                break;
            case 320:
                typeMap.addMappings(m -> {
                    m.map(src -> src.getDuration320(), DTOTrackStreamInfo::setDuration);
                    m.map(src -> src.getFileSize320(), DTOTrackStreamInfo::setFileSize);
                });
                break;
            default:
                return Optional.empty();
        }

        return trackEntity.map(track -> {
            DTOTrackFull info = mapper.map(track, DTOTrackFull.class);
            DTOTrackStreamInfo trackStreamInfo = mapper.map(track, DTOTrackStreamInfo.class);
            trackStreamInfo.setInfo(info);
            return trackStreamInfo;
        });
    }

    @Override
    public Optional<DTOTrackStream> getTrackStreamById(String id, int bitrate) {
        Optional<EntityTrack> trackEntity = trackRepo.findById(id);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        TypeMap<EntityTrack, DTOTrackStream> typeMap = mapper.createTypeMap(EntityTrack.class, DTOTrackStream.class);

        switch (bitrate) {
            case 128:
                typeMap.addMappings(m -> {
                    m.map(src -> src.getMp3128(), DTOTrackStream::setUrl);
                    m.map(src -> src.getDuration128(), DTOTrackStream::setDuration);
                    m.map(src -> src.getFileSize128(), DTOTrackStream::setFileSize);
                });
                break;
            case 320:
                typeMap.addMappings(m -> {
                    m.map(src -> src.getMp3320(), DTOTrackStream::setUrl);
                    m.map(src -> src.getDuration320(), DTOTrackStream::setDuration);
                    m.map(src -> src.getFileSize320(), DTOTrackStream::setFileSize);
                });
                break;
            default:
                return Optional.empty();
        }

        return trackEntity.map(track -> mapper.map(track, DTOTrackStream.class));
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
                    user.unfavoriteTracks(track);
                    break;
                case "favorite":
                    user.favoriteTracks(track);
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public Optional<List<DTOTrackSimple>> getFavorites(String userId) {
        return userRepo.findById(userId).map(user ->{
            return user.getFavoriteTracks().stream().map(track ->{
                ModelMapper mapper = new ModelMapper();
                return mapper.map(track, DTOTrackSimple.class);
            }).collect(Collectors.toList());
        });
    }

}
