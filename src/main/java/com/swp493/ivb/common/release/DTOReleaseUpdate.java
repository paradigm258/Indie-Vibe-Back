package com.swp493.ivb.common.release;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOReleaseUpdate {
    private String title;
    private String type;
    private MultipartFile thumbnail;
}