package com.swp493.ivb.common.track;

import java.util.Optional;

public interface ServiceTrack {

    Optional<DTOTrackFull> getTrackById(String id);
    
    Optional<DTOTrackStream> getTrackStreamById(String id);
}
