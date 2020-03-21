package com.swp493.ivb.common.release;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.naming.NoPermissionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;
import com.swp493.ivb.util.CustomValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger log = LoggerFactory.getLogger(ServiceReleaseImpl.class);

    @Autowired
    private ServiceRelease releaseService;

    @PostMapping(value = "/releases")
    public ResponseEntity<?> uploadNewRelease(@RequestAttribute EntityUser user,
            @RequestParam(name = "info", required = true) String info,
            @RequestParam(name = "thumbnail", required = true) MultipartFile thumbnail,
            @RequestParam(name = "audioFiles", required = true) MultipartFile[] audioFiles){
        try {
            ObjectMapper mapper = new ObjectMapper();
            DTOReleaseInfoUpload releaseInfo = mapper.readValue(info, DTOReleaseInfoUpload.class);
            List<DTOTrackReleaseUpload> trackList = releaseInfo.getTracks();
            if (trackList.size() != audioFiles.length / 2) {
                return Payload.failureResponse("No audio file for track");
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
        } catch (NoSuchElementException e) {
            return Payload.failureResponse("Invalid Id");
        } catch (JsonProcessingException e){
            return Payload.failureResponse("Invalid info form");
        }catch (Exception e){
            log.error("Error create release", e);
            return Payload.internalError();
        }
    }

    @DeleteMapping(value = "/releases/{id}")
    public ResponseEntity<?> uploadNewRelease(
            @RequestAttribute EntityUser user,
            @PathVariable(required = true) String id) {
        try {
            Optional<String> releaseId = releaseService.deleteRelease(id, user.getId());
            if(releaseId.isPresent()){
                return Payload.successResponse(releaseId.get());
            }else{
                return Payload.failureResponse("Failed to delete release");
            }
        } catch (NoSuchElementException e){
            return Payload.failureResponse("Invalid Id");
        }catch (Exception e) {
            log.error("Error delete release", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value = "/releases/simple/{id}")
    public ResponseEntity<?> getRelease(@PathVariable(required = true) String id,@RequestAttribute EntityUser user) {
        try {
            Optional<DTOReleaseSimple> releaseSimple = releaseService.getSimpleRelease(id, user.getId());
            return Payload.successResponse(releaseSimple.get());
        } catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid Id");
        }catch (Exception e) {
            log.error("Error getting release info: ", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value="/stream/release/{releaseId}")
    public ResponseEntity<?> streamRelease(@PathVariable String releaseId,@RequestAttribute EntityUser user) {
        try {
            return Payload.successResponse(releaseService.streamRelease(releaseId, user.getId()));
        }catch(NoPermissionException e){
            return Payload.failureResponse("This release id private");
        }catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid Id");
        } catch (Exception e) {
            log.error("Error get release stream", e);
            return Payload.internalError();
        }
    }
    
    @GetMapping(value = "/releases/full/{id}")
    public ResponseEntity<?> getReleaseFull(
        @PathVariable(required = true) String id,
        @RequestAttribute EntityUser user,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int limit){
        try {
            Optional<DTOReleaseFull> releaseFull = releaseService.getReleaseFull(id, user.getId(), offset, limit);
            return Payload.successResponse(releaseFull.get());
        } catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid Id");
        }catch (Exception e) {
            log.error("Error getting release info: ", e);
            return Payload.internalError();
        }
    }

    @PostMapping(value="/releases/{releaseId}")
    public ResponseEntity<?> actionRelease(
        @PathVariable String releaseId, 
        @RequestAttribute EntityUser user,
        @RequestParam String action) {
            try{
                if(releaseService.actionRelease(releaseId, user.getId(), action)){
                    return Payload.successResponse("Release successfully "+action);
                }else{
                    return Payload.failureResponse("Failed to "+action+" release");
                }
            }catch(NoSuchElementException e){
                return Payload.failureResponse("Invalid id");
            }catch(Exception e){
                log.error("Error set favorite for playlist", e);
                return Payload.internalError();
            }
    }
    
}
