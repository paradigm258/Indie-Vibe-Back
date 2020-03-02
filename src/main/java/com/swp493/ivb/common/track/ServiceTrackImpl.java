package com.swp493.ivb.common.track;

import java.util.Optional;

import org.modelmapper.ModelMapper;
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
    public Optional<DTOTrackStream> getTrackStreamById(String id) {
        Optional<EntityTrack> trackEntity = trackRepo.findById(id);

        return trackEntity.map(track -> new ModelMapper().map(track, DTOTrackStream.class));
    }
}
