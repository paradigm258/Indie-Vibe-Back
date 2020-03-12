package com.swp493.ivb.common.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.swp493.ivb.common.mdata.EntityMasterData;
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
@DiscriminatorFormula(value = "role_id")
@DiscriminatorValue("r-free")
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
    private Set<EntityUserRelease> releaseUsers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityUserTrack> trackUsers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "action = 'favorite'")
    private Set<EntityUserTrack> userFavoriteTracks = new HashSet<>();

    public List<EntityTrack> getFavoriteTracks() {
        return getUserFavoriteTracks().stream().map(userTrack -> {
            return userTrack.getTrack();
        }).collect(Collectors.toList());
    }

    public void favoriteTracks(EntityTrack track) {
        EntityUserTrack userTrack = new EntityUserTrack();
        userTrack.setTrack(track);
        userTrack.setUser(this);
        userTrack.setAction("favorite");
        this.userFavoriteTracks.add(userTrack);
    }

    public void unfavoriteTracks(EntityTrack track) {
        EntityUserTrack userTrack = new EntityUserTrack();
        userTrack.setTrack(track);
        userTrack.setUser(this);
        userTrack.setAction("favorite");
        this.userFavoriteTracks.remove(userTrack);
    }

}