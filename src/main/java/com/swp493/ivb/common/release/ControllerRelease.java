package com.swp493.ivb.common.release;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;
import com.swp493.ivb.util.CustomValidation;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class ControllerRelease {

    private static Logger log = LoggerFactory.getLogger(ServiceReleaseImpl.class);

    @Autowired
    private ServiceRelease releaseService;

    @PostMapping(value = "/artist/releases")
    public ResponseEntity<Payload<String>> uploadNewRelease(@RequestAttribute EntityUser user,
            @RequestParam(name = "info", required = true) String info,
            @RequestParam(name = "thumbnail", required = true) MultipartFile thumbnail,
            @RequestParam(name = "audioFiles", required = true) MultipartFile[] audioFiles)
            throws JsonMappingException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        DTOReleaseInfoUpload releaseInfo = mapper.readValue(info, DTOReleaseInfoUpload.class);

        List<DTOTrackReleaseUpload> trackList = releaseInfo.getTracks();
        if (trackList.size() != audioFiles.length / 2) {
            return ResponseEntity.badRequest().body(new Payload<String>().fail("No audio file for track"));
        }
        Optional<String> error = CustomValidation.validate(releaseInfo);
        if (error.isPresent()) {
            return ResponseEntity.badRequest().body(new Payload<String>().fail(error.get()));
        }
        try {
            Optional<String> releaseId = releaseService.uploadRelease(user.getId(), releaseInfo, thumbnail, audioFiles);
            return releaseId.map(r -> ResponseEntity.ok().body(new Payload<String>().success(r))).orElse(
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Payload<String>().fail("Failed to upload")));
        } catch (NoSuchElementException e) {
            log.error("/release", e);
            return ResponseEntity.badRequest().body(new Payload<String>().fail("Invalid id"));
        }
    }

    @DeleteMapping(value = "/artist/releases/{id}")
    public ResponseEntity<Payload<String>> uploadNewRelease(@RequestAttribute EntityUser user,
            @PathVariable(required = true) String id) {

        Optional<String> releaseId = releaseService.deleteRelease(id,user.getId());
        return releaseId.map(r -> ResponseEntity.ok().body(new Payload<String>().success(r))).orElse(
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Payload<String>().fail("Failed to delete")));
    }

    @GetMapping(value = "/releases/info/{id}/")
    public ResponseEntity<?> getRelease(@PathVariable(required = true) String id) {
        ModelMapper mapper = new ModelMapper();
        Optional<EntityRelease> release = releaseService.getRelease(id);
        return release.map(r -> {
            DTOReleaseSimple releaseSimple = mapper.map(r, DTOReleaseSimple.class);
            return ResponseEntity.ok().body(new Payload<>().success(releaseSimple));
        }).orElse(ResponseEntity.noContent().build());
    }

}
