package com.swp493.ivb.common.artist;

import com.swp493.ivb.common.view.Paging;

public interface ServiceArtist {
    DTOArtistFull getArtistFull(String userId, String artistId);
    DTOArtistSimple getArtistSimple(String userId, String artistId);
    Paging<DTOArtistFull> getArtists(String userId, String viewerId, int offset, int limit);
    Paging<DTOArtistFull> findArtist(String key, String viewerId, int offset, int limit);
}

