package com.swp493.ivb.common.playlist;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/**
 * DTOPlaylistUpdate
 */
@Getter
@Setter
public class DTOPlaylistUpdate {

    private String title;
    private String description;
    private MultipartFile thumbnail;
}