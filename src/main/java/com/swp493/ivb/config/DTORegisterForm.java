package com.swp493.ivb.config;

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
    @Pattern(regexp = "")
    String email;
    @NotBlank
    @Pattern(regexp = "")
    String password;
    @NotBlank
    String cfPassword;
    @Min(0)
    @Max(2)
    int gender;
    
}