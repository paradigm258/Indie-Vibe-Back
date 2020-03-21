package com.swp493.ivb.common.playlist;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    @NotNull
    private MultipartFile thumbnail;
    
}