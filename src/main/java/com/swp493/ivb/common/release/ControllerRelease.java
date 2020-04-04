package com.swp493.ivb.common.release;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;
import com.swp493.ivb.util.CustomValidation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ControllerRelease {

    @Autowired
    private ServiceRelease releaseService;

    @PostMapping(value = "/releases")
    public ResponseEntity<?> uploadNewRelease(@RequestAttribute EntityUser user,
            @RequestParam(name = "info", required = true) String info,
            @RequestParam(name = "thumbnail", required = true) MultipartFile thumbnail,
            @RequestParam(name = "audioFiles", required = true) MultipartFile[] audioFiles)
            throws JsonMappingException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        DTOReleaseInfoUpload releaseInfo = mapper.readValue(info, DTOReleaseInfoUpload.class);
        List<DTOTrackReleaseUpload> trackList = releaseInfo.getTracks();
        if (trackList.size() != audioFiles.length / 2) {
            return Payload.failureResponse("Missing file for track");
        }
        Optional<String> error = CustomValidation.validate(releaseInfo);
        if (error.isPresent()) {
            return Payload.failureResponse(error.get());
        }
        Optional<String> releaseId = releaseService.uploadRelease(user.getId(), releaseInfo, thumbnail, audioFiles);
        if (releaseId.isPresent()) {
            return Payload.successResponse(releaseId.get());
        } else {
            return Payload.failureResponse("Failed to upload");
        }

    }

    @DeleteMapping(value = "/releases/{id}")
    public ResponseEntity<?> deleteRelease(@RequestAttribute EntityUser user,
            @PathVariable(required = true) String id) {

        Optional<String> releaseId = releaseService.deleteRelease(id, user.getId());
        if (releaseId.isPresent()) {
            return Payload.successResponse(releaseId.get());
        } else {
            return Payload.failureResponse("Failed to delete release");
        }

    }

    @GetMapping(value = "/releases/simple/{id}")
    public ResponseEntity<?> getRelease(
        @PathVariable(required = true) String id, 
        @RequestAttribute EntityUser user) {

        Optional<DTOReleaseSimple> releaseSimple = releaseService.getSimpleRelease(id, user.getId());
        return Payload.successResponse(releaseSimple.get());

    }

    @GetMapping(value = "/stream/release/{releaseId}")
    public ResponseEntity<?> streamRelease(
        @PathVariable String releaseId, 
        @RequestAttribute EntityUser user) {

        return Payload.successResponse(releaseService.streamRelease(releaseId, user.getId()));

    }

    @GetMapping(value = "/releases/full/{id}")
    public ResponseEntity<?> getReleaseFull(
        @PathVariable(required = true) String id, 
        @RequestAttribute EntityUser user,
        @RequestParam(defaultValue = "0") int offset, 
        @RequestParam(defaultValue = "20") int limit) {

        Optional<DTOReleaseFull> releaseFull = releaseService.getReleaseFull(id, user.getId(), offset, limit);
        return Payload.successResponse(releaseFull.get());

    }

    @PostMapping(value = "/releases/{releaseId}")
    public ResponseEntity<?> actionRelease(
        @PathVariable String releaseId, 
        @RequestAttribute EntityUser user,
        @RequestParam String action) {

        if (releaseService.actionRelease(releaseId, user.getId(), action)) {
            return Payload.successResponse("Release successfully " + action);
        } else {
            return Payload.failureResponse("Failed to " + action + " release");
        }

    }

}
