package com.swp493.ivb.common.release;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTOReleaseInfoUpload {

    @NotBlank(message = "title must not be empty")
    private String title;

    @NotBlank(message = "type must not be empty")
    private String typeId;

    @NotEmpty(message = "tracks must not be empty")
    private List<DTOTrackReleaseUpload> tracks;

    
}
