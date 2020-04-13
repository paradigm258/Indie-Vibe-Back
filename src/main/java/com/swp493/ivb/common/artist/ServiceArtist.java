package com.swp493.ivb.common.artist;

import java.util.List;
import java.util.Map;

import com.swp493.ivb.common.view.Paging;

public interface ServiceArtist {
    DTOArtistFull getArtistFull(String userId, String artistId);
    DTOArtistSimple getArtistSimple(String userId, String artistId);
    Paging<DTOArtistFull> getArtists(String userId, String viewerId, int offset, int limit);
    Paging<DTOArtistFull> findArtist(String key, String viewerId, int offset, int limit);
    Map<String, Object> getArtistReleaseByType(String artistId, String userId, String releaseTypeId, int offset, int limit);
    Paging<DTOArtistFull> getArtistsRequestProfile(String adminId, int offset, int limit);
    List<String> streamArtist(String artistId);
}

