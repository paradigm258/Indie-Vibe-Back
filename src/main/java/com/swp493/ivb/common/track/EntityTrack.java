package com.swp493.ivb.common.track;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.release.EntityRelease;
import com.swp493.ivb.common.user.EntityUserTrack;

import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

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
    private String id = (new RandomValueStringGenerator(20).generate());

    @NotBlank
    private String title;

    @NotNull
    @Column(name = "duration_128")
    private long duration128;

    @NotNull
    @Column(name = "duration_320")
    private long duration320;

    @NotNull
    @Column(name = "file_size_128")
    private long fileSize128;

    @NotNull
    @Column(name = "file_size_320")
    private long fileSize320;

    @NotNull
    @Column(name = "mp3_offset")
    private int mp3Offset;

    @NotBlank
    private String status;

    private String producer;

    @NotBlank
    @Column(name = "mp3_128")
    private String mp3128;

    @NotBlank
    @Column(name = "mp3_320")
    private String mp3320;

//    // for getting track's artists
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "user_object", 
//            joinColumns = @JoinColumn(name = "track_id"), 
//            inverseJoinColumns = @JoinColumn(name = "user_id"))
//    @WhereJoinTable(clause = "action='own'")
//    private List<EntityArtist> artists;

    // for getting track's genres
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @JoinTable(
            name="object_genre",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<EntityMasterData> genres;

    // for getting track's release
    @OneToOne
    @JoinColumn(name = "release_id")
    private EntityRelease release;

    // for insertion into 'user_object' table, user's operations with track
    @OneToMany(mappedBy = "track",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<EntityUserTrack> trackUsers = new ArrayList<>();
}
