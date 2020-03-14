package com.swp493.ivb.common.release;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.EntityUserRelease;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode.Include;

@Entity
@Table(name = "audio_release")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EntityRelease {

    @Id
    @NotBlank
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Include
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String thumbnail;

    @NotNull
    @Column(name = "released_date")
    private Timestamp date;

    @NotBlank
    private String status;

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'own'")
    private List<EntityUserRelease> artistRelease = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "object_genre", joinColumns = @JoinColumn(name = "release_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<EntityMasterData> genres;

    // for getting release's tracks
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "release", cascade = CascadeType.ALL)
    private List<EntityTrack> tracks;

    // for getting release's type
    @OneToOne
    @JoinColumn(name = "type_id")
    private EntityMasterData releaseType;

    // for insertion into 'user_object' table, user's operations with release
    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityUserRelease> releaseUsers = new HashSet<>();

    public Optional<EntityUser> getArtist() {
        return artistRelease.stream().map(releaseUsers ->{
            return releaseUsers.getUser();
        }).findFirst();
    }
}
