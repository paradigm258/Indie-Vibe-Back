package com.swp493.ivb.common.release;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOReleaseInfoUpload {

    private String title;
    private String typeId;
    private List<TrackReleaseUpload> tracks;

    @NoArgsConstructor
    @Getter
    @Setter
    private static class TrackReleaseUpload {
        private String title;
        private String[] genres;
        private String producer;
    }
}
