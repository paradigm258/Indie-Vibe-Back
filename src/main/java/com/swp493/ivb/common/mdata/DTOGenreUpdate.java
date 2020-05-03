package com.swp493.ivb.common.mdata;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOGenreUpdate {
    
    private String name;

    private MultipartFile thumbnail;

    private String description;
}