package com.swp493.ivb.common.relationship;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

/**
 * KeyPlaylistTrack
 */
@Embeddable
public class KeyPlaylistTrack implements Serializable{
    
    
    /**
     *
     */
    private static final long serialVersionUID = 7659757952875198917L;
    String playlistId;
    String trackId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntityPlaylistTrack)) return false;

        KeyPlaylistTrack that = (KeyPlaylistTrack) obj;
        return Objects.equals(playlistId,that.playlistId) && Objects.equals(trackId, that.trackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId,trackId);
    }
    
}