package com.swp493.ivb.common.track;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceTrackImpl implements ServiceTrack {

    @Autowired
    private RepositoryTrack trackRepo;

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
}
