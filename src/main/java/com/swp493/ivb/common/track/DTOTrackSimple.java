package com.swp493.ivb.common.track;

import java.util.HashSet;
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

    protected String status;

    protected String producer;

    protected long duration;

    protected Set<DTOArtistSimple> artists;

    protected Set<EntityMasterData> genres;

    protected String type = "track";

    protected Set<String> relation = new HashSet<>();
}
