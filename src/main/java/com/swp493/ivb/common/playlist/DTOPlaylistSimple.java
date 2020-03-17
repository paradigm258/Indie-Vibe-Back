package com.swp493.ivb.common.playlist;

import java.util.HashSet;
import java.util.Set;

import com.swp493.ivb.common.user.DTOUserPublic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOPlaylistSimple {

    protected String id;

    protected String title;

    protected String thumbnail;

    protected String description;

    protected String status;

    protected Set<String> relation = new HashSet<>();

    protected DTOUserPublic owner;

    protected String type = "playlist";

    private int tracksCount;
}
