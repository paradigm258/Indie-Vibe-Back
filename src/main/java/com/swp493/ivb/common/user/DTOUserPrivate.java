package com.swp493.ivb.common.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOUserPrivate extends DTOUserPublic {

    private String email;

    private String fbId;

    private int gender;

    private String dob;

    private String artistStatus;
}
