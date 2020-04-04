package com.swp493.ivb.common.release;

import com.swp493.ivb.common.track.DTOTrackSimpleWithLink;
import com.swp493.ivb.common.view.Paging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOReleasePending extends DTOReleaseSimple{
    private Paging<DTOTrackSimpleWithLink> tracks;
}