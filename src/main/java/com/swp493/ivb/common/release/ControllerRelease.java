package com.swp493.ivb.common.release;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp493.ivb.common.view.Payload;

@RestController
public class ControllerRelease {
    
    private static Logger log = LoggerFactory.getLogger(ControllerRelease.class);

    @PostMapping(value = "/releases")
    public ResponseEntity<Payload> uploadNewRelease(
            Authentication authentication,
            @RequestParam(name = "info", required = true) String info,
            @RequestParam(name = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(name = "aduio-files", required = false) MultipartFile[] audioFiles)
                    throws JsonMappingException, JsonProcessingException {
        
        ObjectMapper mapper = new ObjectMapper();
        DTOReleaseInfoUpload releaseInfo = mapper.readValue(info, DTOReleaseInfoUpload.class);

        return ResponseEntity
                .ok()
                .body(new Payload<DTOReleaseInfoUpload>()
                        .success(releaseInfo));
    }
}
