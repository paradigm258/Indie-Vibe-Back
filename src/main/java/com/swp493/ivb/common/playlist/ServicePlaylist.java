package com.swp493.ivb.common.playlist;

import java.util.List;
import java.util.Optional;

public interface ServicePlaylist {
    public String createPlaylist(DTOPlaylistCreate playlistInfo,String userId) throws Exception;
    public boolean deletePlaylist(String playlistId, String userId) throws Exception;
    public List<DTOPlaylistSimple> getPlaylists(String userId, boolean getPrivate, int offset, int limit);
    public Optional<DTOPlaylistFull> getPlaylistFull(String playlistId, String userId, int offset, int limit) throws Exception;
    public Optional<DTOPlaylistSimple> getPlaylistSimple(String playlistId, String userId) throws Exception;
    public boolean actionPlaylistTrack(String trackId, String playlistId, String action, String userId);
    public boolean actionPlaylist(String playlistId, String userId, String action);
    
}
