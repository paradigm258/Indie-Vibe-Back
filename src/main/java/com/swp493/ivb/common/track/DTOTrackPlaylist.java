package com.swp493.ivb.common.track;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOTrackPlaylist {

    private Date addedAt;

    private DTOTrackFull track;
}
