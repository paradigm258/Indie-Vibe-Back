package com.swp493.ivb.common.playlist;

import com.swp493.ivb.common.track.DTOTrackPlaylist;
import com.swp493.ivb.common.view.Paging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOPlaylistFull extends DTOPlaylistSimple {

    private int followersCount;

    private Paging<DTOTrackPlaylist> tracks;
}
