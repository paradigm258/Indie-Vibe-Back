package com.swp493.ivb.common.release;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

@RestController
public class ControllerRelease {

    @Autowired
    private ServiceRelease releaseService;

    @PostMapping(value = "/releases")
    public ResponseEntity<Payload<String>> uploadNewRelease(
            @RequestAttribute EntityUser user,
            @RequestParam(name = "info", required = true) String info,
            @RequestParam(name = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(name = "aduioFiles", required = false) MultipartFile[] audioFiles)
                    throws JsonMappingException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        DTOReleaseInfoUpload releaseInfo = mapper.readValue(info, DTOReleaseInfoUpload.class);

        Optional<String> releaseId = releaseService.uploadRelease(user.getId(), releaseInfo, thumbnail,
                audioFiles);
        return releaseId.map(r -> ResponseEntity
                .ok()
                .body(new Payload<String>()
                        .success(r)))
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new Payload<String>()
                                .fail("Failed to upload")));
    }
    
    @DeleteMapping(value = "/releases/{id}")
    public ResponseEntity<Payload<String>> uploadNewRelease(
            @RequestAttribute EntityUser user,
            @PathVariable(required = true) String id) {
        
        Optional<String> releaseId = releaseService.deleteRelease(id);
        return releaseId.map(r -> ResponseEntity
                .ok()
                .body(new Payload<String>()
                        .success(r)))
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new Payload<String>()
                                .fail("Failed to delete")));
    }
}
