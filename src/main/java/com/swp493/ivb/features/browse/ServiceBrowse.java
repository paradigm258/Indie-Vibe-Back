package com.swp493.ivb.features.browse;

import java.util.List;
import java.util.Map;

import com.swp493.ivb.common.release.DTOReleaseSimple;

public interface ServiceBrowse {
    Map<String, Object> getGeneral(String userId);
    List<DTOGerneCollection<DTOReleaseSimple>> getReleaseGenreCollections(String userId);
    Map<String, Object> getGenreRelease(String userId, String genreId, int offset, int limit);
    Map<String, Object> getGenrePlaylist(String userId, String genreId, int offset, int limit);
    Map<String, Object> getGenre(String userId, String genreId);
    Map<String, Object> getHome(String userId);
    List<Object> getRecent(String userId);
    List<Object> getMost(String userId);
}
