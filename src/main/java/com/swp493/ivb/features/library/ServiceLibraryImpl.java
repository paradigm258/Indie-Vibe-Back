package com.swp493.ivb.features.library;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.playlist.DTOPlaylistSimple;
import com.swp493.ivb.common.playlist.ServicePlaylist;
import com.swp493.ivb.common.user.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceLibraryImpl implements ServiceLibrary {

    @Autowired
    ServiceUser userService;

    @Autowired
    ServicePlaylist playlistService;

    @Override
    public Map<String,Object> getGeneral(String userId, String profileId) {
        List<DTOPlaylistSimple> playlists = playlistService.getPlaylists(profileId, userId, 0, 20, "own").getItems();
        List<DTOArtistFull> artists = userService.getFollowing(userId, 0, 20);
        Map<String,Object> result = new HashMap<>();
        result.put("playlists", playlists);
        result.put("artists", artists);
        return result;
    }

}
