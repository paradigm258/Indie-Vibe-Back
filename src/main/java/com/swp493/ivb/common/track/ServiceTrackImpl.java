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
        DTOTrackFull dtoTrackFull = new DTOTrackFull();

        trackEntity.ifPresent(track -> {
            ModelMapper mapper = new ModelMapper();
            mapper.map(track, dtoTrackFull);
        });

        return Optional.of(dtoTrackFull);
    }
}
