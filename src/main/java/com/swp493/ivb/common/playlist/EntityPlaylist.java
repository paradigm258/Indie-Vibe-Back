package com.swp493.ivb.common.playlist;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

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
    private List<EntityPlaylistTrack> trackPlaylist = new ArrayList<>();

    @OneToMany(mappedBy = "playlist",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityUserPlaylist> userPlaylists = new ArrayList<>();

    @OneToMany(mappedBy = "playlist",cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'own'")
    private List<EntityUserPlaylist> owner = new ArrayList<>();

}
