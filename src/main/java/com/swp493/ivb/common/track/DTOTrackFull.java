package com.swp493.ivb.common.track;

import com.swp493.ivb.common.release.DTOReleaseSimple;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOTrackFull extends DTOTrackSimple {

    private DTOReleaseSimple release;
}
