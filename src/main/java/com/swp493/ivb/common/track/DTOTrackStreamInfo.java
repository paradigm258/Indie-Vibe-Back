package com.swp493.ivb.common.track;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOTrackStreamInfo {

    private int duration;

    private int fileSize;

    private int mp3Offset;

    private DTOTrackFull info;
}
