package com.swp493.ivb.common.track;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOTrackStream {

    private String mp3128;

    private String mp3320;

    private int duration128;

    private int duration320;

    private int fileSize128;

    private int fileSize320;

    private int mp3Offset;
}
