package com.swp493.ivb.common.track;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOTrackPlaylist {

    private Timestamp addedAt;

    private DTOTrackFull track;
}
