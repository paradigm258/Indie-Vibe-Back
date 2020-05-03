package com.swp493.ivb.common.mdata;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOGenreCreate {
    @NotBlank
    private String name;

    @NotNull
    private MultipartFile thumbnail;

    private String description;
}