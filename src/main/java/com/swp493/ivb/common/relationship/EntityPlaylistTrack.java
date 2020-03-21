package com.swp493.ivb.common.relationship;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.swp493.ivb.common.playlist.EntityPlaylist;
import com.swp493.ivb.common.track.EntityTrack;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * EntityPlaylistTrack
 */
@Entity
@Table(name = "playlist_track")
@NoArgsConstructor
@Getter
@Setter
public class EntityPlaylistTrack{

    @EmbeddedId
    KeyPlaylistTrack id = new KeyPlaylistTrack();
    
    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    EntityPlaylist playlist;

    
    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name = "track_id")
    EntityTrack track;

    Date insertedDate;
    
}