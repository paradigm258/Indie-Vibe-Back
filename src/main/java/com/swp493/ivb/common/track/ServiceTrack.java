package com.swp493.ivb.common.track;

import java.util.List;

import com.swp493.ivb.common.view.Paging;

public interface ServiceTrack {
    public DTOTrackStreamInfo getTrackStreamInfo(String id, int bitrate, String userId);
    public boolean favoriteTrack(String userId, String trackId);
    public boolean unfavoriteTrack(String userId, String trackId);
    public Paging<DTOTrackFull> getUserTracks(String userId, String viewerId, int offset, int limit, String type);
    public DTOTrackFull getTrackById(String id,String userId);
    public DTOTrackFull getTrackFullFromEntity(EntityTrack track, String userId);
    public boolean hasTrackAccessPermission(String trackId, String userId);
    public List<String> streamFavorite(String userId);
    public Paging<DTOTrackFull> findTrack(String userId, String key, int offset, int limit);
    public DTOTrackSimpleWithLink getTrackSimpleWithLink(String trackId);
    public String deleteTrack(String userId, String trackId);
	public String updateTrack(String userId, String trackId, DTOTrackUpdate data);
	public DTOTrackSimple getTrackSimple(String trackId, String userId);
}
