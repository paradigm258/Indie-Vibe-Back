package com.swp493.ivb.common.track;

import java.util.List;

import com.swp493.ivb.common.release.DTOTrackReleaseUpload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOTrackAddRelease {
    private List<DTOTrackReleaseUpload> tracks;
}