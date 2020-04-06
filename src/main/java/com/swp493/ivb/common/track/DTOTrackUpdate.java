package com.swp493.ivb.common.track;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOTrackUpdate {
    private String title;
    private String[] genres;
    private MultipartFile mp3128;
    private MultipartFile mp3320;
    private String producer;
}