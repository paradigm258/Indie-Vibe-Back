package com.swp493.ivb.common.user;

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

    protected String type = "user";
}
