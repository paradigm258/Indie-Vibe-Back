package com.swp493.ivb.common.track;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOTrackStream {

    private String url;

    private int duration;

    private int fileSize;

    private int mp3Offset;
}
