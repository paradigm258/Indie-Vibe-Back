package com.swp493.ivb.common.track;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.features.common.user.UserService;

@Service
public class ServiceTrackImpl implements ServiceTrack {

    @Autowired
    private RepositoryTrack trackRepo;

    @Autowired
    private UserService userService;

    @Override
    public Optional<DTOTrackFull> getTrackById(String id) {
        Optional<EntityTrack> trackEntity = trackRepo.findById(id);
        DTOTrackFull dtoTrackFull = new DTOTrackFull();
        
        trackEntity.ifPresent(track -> {
            ModelMapper mapper = new ModelMapper();
            // skip mapping to release that the track belongs to
            mapper.addMappings(new PropertyMap<EntityTrack, DTOTrackFull>() {
                @Override
                protected void configure() {
                    skip(destination.getRelease());
                }
            });
            mapper.map(track, dtoTrackFull);

            // set the release directly, with additional information
            dtoTrackFull.setRelease(mapper.map(track.getRelease(), DTOReleaseSimple.class));
            DTOArtistFull releaseArtist = dtoTrackFull.getRelease().getArtist();
            releaseArtist.setFollowersCount(userService.countFollowers(releaseArtist.getId()));
        });

        return Optional.of(dtoTrackFull);
    }
}
