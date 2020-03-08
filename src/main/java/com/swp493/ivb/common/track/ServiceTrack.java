package com.swp493.ivb.common.track;

import java.util.Optional;

public interface ServiceTrack {

    Optional<DTOTrackFull> getTrackById(String id);

    Optional<DTOTrackStreamInfo> getTrackStreamInfo(String id, int bitrate);

    Optional<DTOTrackStream> getTrackStreamById(String id, int bitrate);
}
