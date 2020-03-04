package com.swp493.ivb.config;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

/**
 * DTORegisterForm
 */
@Getter
@Setter
public class DTORegisterForm {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    private String password;

    @NotBlank
    private String cfPassword;

    @Min(0)
    @Max(2)
    private int gender;
    
    @NotBlank
    private String displayName;

}