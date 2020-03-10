package com.swp493.ivb.config;

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

    @NotBlank
    @Email(message = "Email")
    private String email;

    @NotBlank(message = "Display name")
    private String displayName;

    private String thumbnail;

    @NotBlank(message = "Facebook Id")
    private String fbId;
    
    @NotBlank(message = "Facebook token")
    private String fbToken;
}