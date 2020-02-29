package com.swp493.ivb.common.release;

import java.sql.Date;

import com.swp493.ivb.common.artist.DTOArtistFull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOReleaseSimple {

    private String id;

    private String title;

    private String thumbnail;

    private Date date;

    private String status;

    private DTOArtistFull artist;
}
