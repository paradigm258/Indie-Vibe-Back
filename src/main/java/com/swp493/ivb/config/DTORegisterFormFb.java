package com.swp493.ivb.config;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * DTORegisterFormFb
 */
@Getter
@Setter
public class DTORegisterFormFb {

    @NotBlank(message = "Display name")
    private String displayName;

    private String thumbnail;

    @NotBlank(message = "Facebook Id")
    private String fbId;
    
    @NotBlank(message = "Facebook token")
    private String fbToken;

    private Date dob;
}