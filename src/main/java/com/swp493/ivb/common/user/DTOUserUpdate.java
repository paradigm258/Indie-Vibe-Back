package com.swp493.ivb.common.user;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

/**
 * DTOUserUpdate
 */
@Getter
@Setter
public class DTOUserUpdate {

    private String displayName;

    private String email;

    @Min(value = 0, message = "Gender")
    @Max(value = 2, message = "Gender")
    private int gender;

    private String dob;
}