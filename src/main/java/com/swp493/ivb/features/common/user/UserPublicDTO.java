package com.swp493.ivb.features.common.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserPublicDTO {

    protected String id;

    protected String displayName;

    protected String thumbnail;

    protected int followersCount;
}
