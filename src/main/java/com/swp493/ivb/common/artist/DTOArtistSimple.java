package com.swp493.ivb.common.artist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOArtistSimple {

    protected String id;

    protected String displayName;

    protected String type = "artist";
}
