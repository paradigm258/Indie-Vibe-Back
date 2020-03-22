package com.swp493.ivb.common.playlist;

import java.io.IOException;
import java.util.List;

import com.swp493.ivb.common.view.Paging;

public interface ServicePlaylist {
    public String createPlaylist(DTOPlaylistCreate playlistInfo,String userId) throws IOException;
    public boolean deletePlaylist(String playlistId, String userId);
    public Paging<DTOPlaylistSimple> getPlaylists(String userId, String viewerId, int offset, int limit, String type);
    public DTOPlaylistFull getPlaylistFull(String playlistId, String userId, int offset, int limit);
    public DTOPlaylistFull getPlaylistFull(EntityPlaylist playlist, String userId, int offset, int limit);
    public DTOPlaylistSimple getPlaylistSimple(EntityPlaylist playlist, String userId);
    public DTOPlaylistSimple getPlaylistSimple(String playlistId, String userId);
    public boolean actionPlaylistTrack(String trackId, String playlistId, String action, String userId);
    public boolean actionPlaylist(String playlistId, String userId, String action);
    public List<String> playlistStream(String playlistId, String userId);
}
