package com.swp493.ivb.common.release;

import com.swp493.ivb.common.track.DTOTrackSimple;
import com.swp493.ivb.common.view.Paging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOReleaseFull extends DTOReleaseSimple {

    private Paging<DTOTrackSimple> tracks;
}
