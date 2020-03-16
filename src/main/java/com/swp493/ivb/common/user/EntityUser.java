package com.swp493.ivb.common.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.playlist.EntityPlaylist;
import com.swp493.ivb.common.track.EntityTrack;

import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode.Include;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula(value = "role_id")
@DiscriminatorValue("r-free")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EntityUser {

    @Id
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Include
    private String id;

    @NotBlank
    @Email
    private String email;

    private String password;

    @NotBlank
    private String displayName;

    private String thumbnail;

    private String fbId;

    @NotBlank
    private String artistStatus;

    private int followerCount;

    // dùng cái này để lấy role nhá, xóa cái trên đi, dùng 1 cái thôi ko lỗi
    // dùng cái này get nó ra là nó tự query.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private EntityMasterData userRole;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private EntityMasterData userCountry;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private EntityMasterData userPlan;

    @ManyToMany
    @JoinTable(name = "user_follow_user",joinColumns = @JoinColumn(name = "follower_id"), 
    inverseJoinColumns = @JoinColumn(name = "followed_id"))
    private List<EntityUser> followedUsers;

    @ManyToMany(mappedBy = "followedUsers")
    private List<EntityUser> followerUsers;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityUserRelease> releaseUsers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityUserTrack> trackUsers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityUserPlaylist> userPlaylists = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'favorite'")
    private Set<EntityUserTrack> userFavoriteTracks = new HashSet<>();

    public boolean favoriteTracks(EntityTrack track) {
        EntityUserTrack userTrack = new EntityUserTrack();
        userTrack.setTrack(track);
        userTrack.setUser(this);
        userTrack.setAction("favorite");
        if(userFavoriteTracks.contains(userTrack)) return false;
        this.userFavoriteTracks.add(userTrack);
        return true;
    }

    public boolean unfavoriteTracks(EntityTrack track) {
        EntityUserTrack userTrack = new EntityUserTrack();
        userTrack.setTrack(track);
        userTrack.setUser(this);
        userTrack.setAction("favorite");
        if(!userFavoriteTracks.contains(userTrack)) return false;
        this.userFavoriteTracks.remove(userTrack);
        return true;
    }

    public boolean favoritePlaylist(EntityPlaylist playlist) {
        EntityUserPlaylist userPlaylist = new EntityUserPlaylist();
        userPlaylist.setPlaylist(playlist);
        userPlaylist.setUser(this);
        userPlaylist.setAction("favorite");
        if(userPlaylists.contains(userPlaylist)) return false;
        this.userPlaylists.add(userPlaylist);
        return true;
    }

    public boolean unfavoritePlaylist(EntityPlaylist playlist) {
        EntityUserPlaylist userPlaylist = new EntityUserPlaylist();
        userPlaylist.setPlaylist(playlist);
        userPlaylist.setUser(this);
        userPlaylist.setAction("favorite");
        if(!userPlaylists.contains(userPlaylist)) return false;
        this.userPlaylists.remove(userPlaylist);
        return true;
    }
}