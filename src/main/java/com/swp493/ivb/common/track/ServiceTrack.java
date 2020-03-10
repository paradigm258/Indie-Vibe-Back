package com.swp493.ivb.common.track;

import java.util.Optional;

import com.swp493.ivb.common.user.EntityUser;

public interface ServiceTrack {

    public Optional<DTOTrackFull> getTrackById(String id);

    public Optional<DTOTrackStreamInfo> getTrackStreamInfo(String id, int bitrate);

    public Optional<DTOTrackStream> getTrackStreamById(String id, int bitrate);

    public boolean favoriteTrack(String userId, String trackId);

    public boolean unfavoriteTrack(String userId, String trackId);
}
