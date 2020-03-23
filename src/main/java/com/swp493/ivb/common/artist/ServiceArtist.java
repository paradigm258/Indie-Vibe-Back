package com.swp493.ivb.common.artist;

public interface ServiceArtist {
    DTOArtistFull getArtistFull(String userId, String artistId);
    DTOArtistSimple getArtistSimple(String userId, String artistId);
}

