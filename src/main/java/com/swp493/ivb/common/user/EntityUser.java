package com.swp493.ivb.common.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.track.EntityTrack;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
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

    private String thumbnail;

    private String fbId;
    
    @NotBlank
    private String artistStatus;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityUserRelease> releaseUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityUserTrack> trackUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'favorite'")
    private List<EntityUserTrack> userFavoriteTracks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'own'")
    private List<EntityUserTrack> ownUserTracks = new ArrayList<>();

    public List<EntityTrack> getFavoriteTracks(){
        return getUserFavoriteTracks().stream().map(userTrack ->{
            return userTrack.getTrack();
        }).collect(Collectors.toList());
    }

    public void favoriteTracks(EntityTrack track){
        EntityUserTrack userTrack = new EntityUserTrack();
        userTrack.setTrack(track);
        userTrack.setUser(this);
        userTrack.setAction("favorite");
        this.userFavoriteTracks.add(userTrack);
        track.getTrackUsers().add(userTrack);
    }

    public void unfavoriteTracks(EntityTrack track){
        for(EntityUserTrack userTrack : userFavoriteTracks){
            if(userTrack.getTrack().equals(track)){
                userFavoriteTracks.remove(userTrack);
                userTrack.getTrack().getTrackUsers().remove(userTrack);
                userTrack.setTrack(null);
                userTrack.setUser(null);
                return ;
            }
        }
    }

    @OneToMany(mappedBy = "user")
    private Set<EntityUserTrack2> userTracks;
}