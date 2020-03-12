package com.swp493.ivb.common.track;

import java.util.List;
import java.util.Optional;

public interface ServiceTrack {

    public Optional<DTOTrackStreamInfo> getTrackStreamInfo(String id, int bitrate);

    public Optional<DTOTrackStream> getTrackStreamById(String id, int bitrate);

    public boolean favoriteTrack(String userId, String trackId);

    public boolean unfavoriteTrack(String userId, String trackId);

    public Optional<List<DTOTrackSimple>> getFavorites(String userId);

    public Optional<DTOTrackFull> getTrackById(String id);
}
