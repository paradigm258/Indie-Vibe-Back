package com.swp493.ivb.common.artist;

import com.swp493.ivb.common.user.DTOUserPublic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOArtistFull extends DTOUserPublic {

    private String biography;

    private String type = "artist";
}
