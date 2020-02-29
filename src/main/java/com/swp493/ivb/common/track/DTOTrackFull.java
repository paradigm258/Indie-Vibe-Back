package com.swp493.ivb.common.track;

import java.util.List;

import com.swp493.ivb.common.artist.DTOArtistSimple;
import com.swp493.ivb.common.release.DTOReleaseSimple;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOTrackFull {

    private String id;

    private String title;

    private int duration128;

    private int duration320;

    private int fileSize128;

    private int fileSize320;

    private int mp3Offset;

    private String status;

    private String producer;

    private List<DTOArtistSimple> artists;

    private DTOReleaseSimple release;
}
