package com.swp493.ivb.common.artist;

import java.util.Optional;

public interface ServiceArtist {
    public Optional<EntityArtist> getArtist(String id);
}
