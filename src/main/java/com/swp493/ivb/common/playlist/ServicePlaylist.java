package com.swp493.ivb.common.playlist;

import java.util.List;
import java.util.Optional;

public interface ServicePlaylist {
    public String createPlaylist(DTOPlaylistCreate playlistInfo,String userId) throws Exception;
    public boolean deletePlaylist(String playlistId, String userId) throws Exception;
    public List<DTOPlaylistSimple> getPlaylists(String userId);
    public Optional<DTOPlaylistFull> getPlaylistFull(String playlistId, String userId, int pageIndex) throws Exception;
}
