package com.swp493.ivb.common.playlist;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/**
 * DTOPlaylistCreate
 */
@Getter
@Setter
public class DTOPlaylistCreate {

    @NotBlank
    private String title;

    private String description;

    private MultipartFile thumbnail;
}