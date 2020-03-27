package com.swp493.ivb.config;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @Email(message = "Email")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",message = "Password")
    private String password;

    @NotBlank
    private String cfPassword;

    @Min(value = 0,message = "Gender")
    @Max(value = 2,message = "Gender")
    private int gender;
    
    @NotBlank(message = "Display name")
    private String displayName;

    private Date dob;

}