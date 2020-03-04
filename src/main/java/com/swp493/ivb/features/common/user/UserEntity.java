package com.swp493.ivb.features.common.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.swp493.ivb.common.mdata.EntityMasterData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String displayName;

    private String thumbnail;

   // dùng cái này để lấy role nhá, xóa cái trên đi, dùng 1 cái thôi ko lỗi
   // dùng cái này get nó ra là nó tự query.
   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "role_id")
   private EntityMasterData userRole;

    private String fbId;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<EntityUserRelease> releaseUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<EntityUserTrack> trackUsers = new ArrayList<>();
}