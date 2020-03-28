package com.swp493.ivb.common.user;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
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
import com.swp493.ivb.common.relationship.EntityUserPlaylist;
import com.swp493.ivb.common.relationship.EntityUserRelease;
import com.swp493.ivb.common.relationship.EntityUserTrack;
import com.swp493.ivb.common.release.EntityRelease;
import com.swp493.ivb.common.track.EntityTrack;

import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula(value = "case when role_id = 'r-artist' then 'artist' else 'user' end")
@DiscriminatorValue("user")
@NoArgsConstructor
@Getter
@Setter
public class EntityUser {

    @Id
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    
    private String id;

    @NotBlank
    @Email
    private String email;

    private String password;

    @NotBlank
    private String displayName;

    private int gender;

    private Date dob;

    private String thumbnail;

    private String fbId;

    @NotBlank
    private String artistStatus = "open";

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
    private Set<EntityUser> followingUsers;

    @ManyToMany(mappedBy = "followingUsers")
    private Set<EntityUser> followerUsers;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "release_id is not null")
    private Set<EntityUserRelease> releaseUsers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "track_id is not null")
    private Set<EntityUserTrack> trackUsers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "playlist_id is not null")
    private Set<EntityUserPlaylist> userPlaylists = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'favorite' and track_id is not null")
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

    public boolean favoriteRelease(EntityRelease release){
        EntityUserRelease userRelease = new EntityUserRelease();
        userRelease.setAction("favorite");
        userRelease.setRelease(release);
        userRelease.setUser(this);
        if(releaseUsers.contains(userRelease)) return false;
        releaseUsers.add(userRelease);
        return true;
    }

    public boolean unfavoriteRelease(EntityRelease release){
        EntityUserRelease userRelease = new EntityUserRelease();
        userRelease.setAction("favorite");
        userRelease.setRelease(release);
        userRelease.setUser(this);
        if(!releaseUsers.contains(userRelease)) return false;
        releaseUsers.remove(userRelease);
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntityUser)) return false;
        
        EntityUser that = (EntityUser) obj;
        return Objects.equals(id, that.getId());
    }
}