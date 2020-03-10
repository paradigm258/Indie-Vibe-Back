package com.swp493.ivb.common.release;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOTrackReleaseUpload {

    @NotBlank
    private String title;

    @NotEmpty
    private String[] genres;

    @NotBlank
    private String producer;
}