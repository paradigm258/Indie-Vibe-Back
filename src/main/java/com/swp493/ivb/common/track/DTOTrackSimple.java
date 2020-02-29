package com.swp493.ivb.common.track;

import java.util.List;

import com.swp493.ivb.common.artist.DTOArtistSimple;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOTrackSimple {

    protected String id;

    protected String title;

    protected int duration128;

    protected int duration320;

    protected int fileSize128;

    protected int fileSize320;

    protected int mp3Offset;

    protected String status;

    protected String producer;

    protected List<DTOArtistSimple> artists;
}
