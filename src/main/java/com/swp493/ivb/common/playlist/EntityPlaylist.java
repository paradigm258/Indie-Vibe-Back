package com.swp493.ivb.common.playlist;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

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
    private String id = (new RandomValueStringGenerator(20).generate());

    @NotBlank
    private String title;

    private String description;

    private String thumbnail;

}
