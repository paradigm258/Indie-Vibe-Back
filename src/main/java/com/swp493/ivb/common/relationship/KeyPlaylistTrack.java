package com.swp493.ivb.common.relationship;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

/**
 * KeyPlaylistTrack
 */
@Embeddable
@Setter
@Getter
public class KeyPlaylistTrack implements Serializable{
    
    
    /**
     *
     */
    private static final long serialVersionUID = 7659757952875198917L;
    @Column(name = "playlist_id")
    private String playlistId;
    @Column(name = "track_id")
    private String trackId;
    
    public void setPlaylistId(String playlistId){
        this.playlistId = playlistId;
    }

    public void setTrackId(String trackId){
        this.trackId = trackId;
    }

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