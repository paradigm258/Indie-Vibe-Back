package com.swp493.ivb.common.release;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.swp493.ivb.common.artist.DTOArtistSimple;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOReleaseSimple {

    protected String id;

    protected String title;

    protected String thumbnail;

    protected Timestamp date;

    protected String status;

    protected Set<String> relation = new HashSet<>();

    protected DTOArtistSimple artist;

    protected String type = "release";
}
