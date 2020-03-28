package com.swp493.ivb.common.artist;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import com.swp493.ivb.common.release.EntityRelease;
import com.swp493.ivb.common.user.EntityUser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(value = "artist")
@NoArgsConstructor
@Getter
@Setter
public class EntityArtist extends EntityUser{

    private String biography;

    public List<EntityRelease> getOwnReleases(){
        return getReleaseUsers().stream().map(userRelease ->{
            return userRelease.getRelease();
        }).collect(Collectors.toList());
    }
}
