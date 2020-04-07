package com.swp493.ivb.common.track;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.relationship.EntityPlaylistTrack;
import com.swp493.ivb.common.relationship.EntityUserTrack;
import com.swp493.ivb.common.release.EntityRelease;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "track")
@NoArgsConstructor
@Getter
@Setter
public class EntityTrack {

    @Id
    @NotBlank
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    private String id;

    @NotBlank
    private String title;

    @NotNull
    private Long duration;

    @NotNull
    @Column(name = "file_size_128")
    private Long fileSize128;

    @NotNull
    @Column(name = "file_size_320")
    private Long fileSize320;

    @NotBlank
    private String status;

    private String producer;

    @NotBlank
    @Column(name = "mp3_128")
    private String mp3128;

    @NotBlank
    @Column(name = "mp3_320")
    private String mp3320;

    private int streamCount;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'own' or action = 'featured'")
    private Set<EntityUserTrack> artist = new HashSet<>();
    

    // for getting track's genres
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "object_genre", joinColumns = @JoinColumn(name = "track_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<EntityMasterData> genres;

    // for getting track's release
    @OneToOne
    @JoinColumn(name = "release_id")
    private EntityRelease release;

    // for insertion into 'user_object' table, user's operations with track
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityUserTrack> trackUsers = new ArrayList<>();

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityPlaylistTrack> trackPlaylists = new ArrayList<>();

}
