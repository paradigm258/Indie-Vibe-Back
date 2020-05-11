package com.swp493.ivb.common.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/**
 * DTOUserUpdate
 */
@Getter
@Setter
public class DTOUserUpdate {

    private String displayName;

    @Email(message = "Incorrect email format")
    private String email;

    @Min(value = 0, message = "Incorrect gender format")
    @Max(value = 2, message = "Incorrect gender format")
    private int gender;

    private String dob;

    private MultipartFile thumbnail;
}