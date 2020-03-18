package com.swp493.ivb.common.user;

import java.util.HashSet;
import java.util.Set;

import com.swp493.ivb.common.mdata.DTORole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOUserPublic {

    protected String id;

    protected String displayName;

    protected String thumbnail;

    protected int followersCount;

    protected Set<String> relation = new HashSet<>();

    protected String type = "profile";

    protected DTORole role;
}
