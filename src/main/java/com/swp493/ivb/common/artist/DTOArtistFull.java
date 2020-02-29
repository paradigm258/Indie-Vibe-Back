package com.swp493.ivb.common.artist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOArtistFull extends DTOArtistSimple {

    private String biography;

    private int followersCount;
}
