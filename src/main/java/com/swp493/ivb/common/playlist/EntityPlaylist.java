package com.swp493.ivb.common.playlist;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.common.user.EntityUserPlaylist;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "playlist")
@NoArgsConstructor
@Getter
@Setter
public class EntityPlaylist {
    @Id
    @NotBlank
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    private String id;

    @NotBlank
    private String title;

    private String description;

    private String thumbnail;

    private String status;

    @OneToMany(mappedBy = "playlist",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityPlaylistTrack> playlistTracks = new ArrayList<>();

    @OneToMany(mappedBy = "playlist",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityUserPlaylist> userPlaylists = new ArrayList<>();

    @OneToMany(mappedBy = "playlist",cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'favorite'")
    private List<EntityUserPlaylist> userFollowPlaylists = new ArrayList<>();

    @OneToMany(mappedBy = "playlist",cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'own'")
    private List<EntityUserPlaylist> owner = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "object_genre", joinColumns = @JoinColumn(name = "playlist_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<EntityMasterData> genres = new HashSet<>();

    public boolean addTrack(EntityTrack track){
        EntityPlaylistTrack playlistTrack = new EntityPlaylistTrack();
        playlistTrack.setPlaylist(this);
        playlistTrack.setTrack(track);
        playlistTrack.setInsertedDate(new Date());
        if(playlistTracks.contains(playlistTrack)) return false;
        playlistTracks.add(playlistTrack);
        return true;
    }
    public boolean removeTrack(EntityTrack track){
        EntityPlaylistTrack playlistTrack = new EntityPlaylistTrack();
        playlistTrack.setPlaylist(this);
        playlistTrack.setTrack(track);
        playlistTrack.setInsertedDate(new Date());
        if(!playlistTracks.contains(playlistTrack)) return false;
        playlistTracks.remove(playlistTrack);
        return true;
    }

}
