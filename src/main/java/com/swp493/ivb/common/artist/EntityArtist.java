package com.swp493.ivb.common.artist;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Where(clause = "role_id = 'r-artist'")
@NoArgsConstructor
@Getter
@Setter
public class EntityArtist {

    @Id
    private String id;

    @NotBlank
    private String displayName;

    @NotBlank
    private String biography;
}
