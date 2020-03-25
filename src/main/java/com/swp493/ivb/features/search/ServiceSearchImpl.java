package com.swp493.ivb.features.search;

import java.util.HashMap;
import java.util.Map;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.DTOGenre;
import com.swp493.ivb.common.mdata.ServiceMasterData;
import com.swp493.ivb.common.playlist.DTOPlaylistSimple;
import com.swp493.ivb.common.playlist.ServicePlaylist;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.track.DTOTrackFull;
import com.swp493.ivb.common.track.ServiceTrack;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.view.Paging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceSearchImpl implements ServiceSearch {

    @Autowired
    ServiceArtist artistService;

    @Autowired
    ServiceTrack trackService;

    @Autowired
    ServicePlaylist playlistService;

    @Autowired
    ServiceRelease releaseService;

    @Autowired
    ServiceUser userService;

    @Autowired
    ServiceMasterData masterDataService;

    @Override
    public Paging<DTOArtistFull> findArtist(String userId, String key, int offset, int limit) {
        return artistService.findArtist(key, userId, offset, limit);
    }

    @Override
    public Paging<DTOTrackFull> findTrack(String userId, String key, int offset, int limit) {
        return trackService.findTrack(userId, key, offset, limit);
    }

    @Override
    public Paging<DTOPlaylistSimple> findPlaylist(String userId, String key, int offset, int limit) {
        return playlistService.findPlaylist(key, userId, offset, limit);
    }

    @Override
    public Paging<DTOReleaseSimple> findReleases(String userId, String key, int offset, int limit) {
        return releaseService.findRelease(key, userId, offset, limit);
    }

    @Override
    public Paging<DTOGenre> findGenre(String key, int offset, int limit) {
        return masterDataService.findGenre(key, offset, limit);
    }

    @Override
    public Paging<DTOUserPublic> findProfile(String userId, String key, int offset, int limit) {
        return userService.findProfile(key, userId, offset, limit);
    }

    @Override
    public Map<String,Object> findGeneral(String userId, String key) {
        Map<String,Object> result = new HashMap<>();
        int offset = 0;
        int limit = 5;
        result.put("tracks", findTrack(userId, key, offset, limit));
        result.put("artists", findArtist(userId, key, offset, limit));
        result.put("releases", findReleases(userId, key, offset, limit));
        result.put("playlists", findPlaylist(userId, key, offset, limit));
        result.put("profiles", findProfile(userId, key, offset, limit));
        result.put("genres", findGenre(key, offset, limit));
        return result;
    }
    
}
