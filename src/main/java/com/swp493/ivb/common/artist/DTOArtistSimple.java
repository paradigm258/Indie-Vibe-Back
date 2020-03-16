package com.swp493.ivb.common.artist;

import java.util.Set;

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

    protected Set<String> relation;
}
