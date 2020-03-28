package com.swp493.ivb.common.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

/**
 * DTOUpdatePassword
 */
@Getter
@Setter
public class DTOUpdatePassword {

    @NotBlank(message = "Password")
    String pwd;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",message = "New password")
    String newPwd;

    @NotBlank(message = "Confirm password")
    String cfNewPwd;
}