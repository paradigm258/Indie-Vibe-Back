package com.swp493.ivb.common.playlist;

import java.util.List;
import java.util.Optional;

import com.swp493.ivb.common.view.Paging;

public interface ServicePlaylist {
    public String createPlaylist(DTOPlaylistCreate playlistInfo,String userId) throws Exception;
    public boolean deletePlaylist(String playlistId, String userId) throws Exception;
    public Paging<DTOPlaylistSimple> getPlaylists(String userId, boolean getPrivate, int offset, int limit);
    public Optional<DTOPlaylistFull> getPlaylistFull(String playlistId, String userId, int offset, int limit) throws Exception;
    public Optional<DTOPlaylistSimple> getPlaylistSimple(String playlistId, String userId) throws Exception;
    public boolean actionPlaylistTrack(String playlistId, String trackId, String action, String userId);
    public boolean actionPlaylist(String playlistId, String userId, String action) throws Exception;
    public List<String> playlistStream(String playlistId, String userId) throws Exception;
}
