package com.swp493.ivb.common.track;

import java.util.Set;

import com.swp493.ivb.common.artist.DTOArtistSimple;
import com.swp493.ivb.common.mdata.EntityMasterData;

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

    protected Set<DTOArtistSimple> artists;

    private Set<EntityMasterData> genres;
}
