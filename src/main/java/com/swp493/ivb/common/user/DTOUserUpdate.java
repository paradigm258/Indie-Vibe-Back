package com.swp493.ivb.common.user;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * DTOUserUpdate
 */
@Getter
@Setter
public class DTOUserUpdate {

    @NotBlank
    String displayName;

    @NotBlank
    @Email(message = "Email")
    private String email;
    
    @Min(value = 0,message = "Gender")
    @Max(value = 2,message = "Gender")
    int gender;

    @NotNull
    private Date dob;
}