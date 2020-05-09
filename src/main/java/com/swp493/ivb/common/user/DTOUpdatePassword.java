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

    @NotBlank(message = "Missing old password")
    String pwd;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password word must be at least 8 characters with a number")
    String newPwd;

    @NotBlank(message = "Missing confirm password")
    String cfNewPwd;
}