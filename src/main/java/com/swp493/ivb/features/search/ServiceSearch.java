package com.swp493.ivb.features.search;

import java.util.Map;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.mdata.DTOGenre;
import com.swp493.ivb.common.playlist.DTOPlaylistSimple;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.common.track.DTOTrackFull;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.view.Paging;

import org.springframework.stereotype.Service;

@Service
public interface ServiceSearch {
    Paging<DTOArtistFull> findArtist(String userId, String key, int offset, int limit);
    Paging<DTOTrackFull> findTrack(String userId, String key, int offset, int limit);
    Paging<DTOPlaylistSimple> findPlaylist(String userId, String key, int offset, int limit);
    Paging<DTOReleaseSimple> findReleases(String userId, String key, int offset, int limit);
    Paging<DTOGenre> findGenre(String key, int offset, int limit);
    Paging<DTOUserPublic> findProfile(String userId, String key, int offset, int limit);
    Map<String, Object> findGeneral(String userId, String key);
}
