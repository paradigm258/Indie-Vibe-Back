package com.swp493.ivb.features.workspace;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp493.ivb.common.release.DTOReleaseInfoUpload;
import com.swp493.ivb.common.release.DTOReleaseUpdate;
import com.swp493.ivb.common.release.DTOTrackReleaseUpload;
import com.swp493.ivb.common.track.DTOTrackAddRelease;
import com.swp493.ivb.common.track.DTOTrackUpdate;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;
import com.swp493.ivb.util.CustomValidation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;




@RestController
public class ControllerWorkspace {

    @Autowired
    ServiceWorkspace workspaceService;

    @PostMapping(value="/stream/count/{type}/{id}")
    public ResponseEntity<?> updateCount(@RequestAttribute EntityUser user,@PathVariable String type,@PathVariable String id) {
        workspaceService.updateCount(user.getId(),type, id);
        return Payload.successResponse("Success");
    }

    @PostMapping(value="/account/baa")
    public ResponseEntity<?> artistRequest(@RequestAttribute EntityUser user,
    @RequestParam(required = false) String biography,
    @RequestParam(name = "info", required = true) String info,
    @RequestParam(name = "thumbnail", required = true) MultipartFile thumbnail,
    @RequestParam(name = "audioFiles", required = true) MultipartFile[] audioFiles)
    throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        DTOReleaseInfoUpload releaseInfo = mapper.readValue(info, DTOReleaseInfoUpload.class);
        List<DTOTrackReleaseUpload> trackList = releaseInfo.getTracks();
        if (trackList.size() != audioFiles.length / 2) {
            return Payload.failureResponse("Missing audio file for track");
        }
        Optional<String> error = CustomValidation.validate(releaseInfo);
        if (error.isPresent()) {
            return Payload.failureResponse(error.get());
        }
        Optional<String> releaseId = workspaceService.requestBecomeArtirst(user.getId(), releaseInfo, thumbnail, audioFiles, biography);
        if (releaseId.isPresent()) {
            return Payload.successResponse(releaseId.get());
        } else {
            return Payload.failureResponse("Failed to upload");
        }
    }
    
    @PutMapping(value="/workspace/releases/{id}")
    public ResponseEntity<?> updateRelease(@Valid DTOReleaseUpdate data, @PathVariable String id, @RequestAttribute EntityUser user) {
        if(workspaceService.updateRelease(data, user.getId(), id))
            return Payload.successResponse("Release updated");
        else
            return Payload.failureResponse("Update release failed");
    }

    @PostMapping(value="/workspace/releases/{id}/track")
    public ResponseEntity<?> addTrackToRelease(
        @RequestAttribute EntityUser user,
        @PathVariable String id,
        String tracks, 
        MultipartFile[] files) {
            ObjectMapper mapper = new ObjectMapper();
            try{
                tracks = "{\"tracks\":"+tracks+"}";
                DTOTrackAddRelease releaseInfo = mapper.readValue(tracks, DTOTrackAddRelease.class);
                List<DTOTrackReleaseUpload> trackList = releaseInfo.getTracks();
                if (trackList.size() != files.length / 2) {
                    return Payload.failureResponse("Missing file for track");
                }
                workspaceService.addToRelease(user.getId(),id, trackList, files);
            }catch(JsonProcessingException e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Malformed data");
            }
        return Payload.successResponse("Added track to playlist");
    }
    
    @DeleteMapping(value = "/workspace/releases/{id}")
    public ResponseEntity<?> deleteRelease(@PathVariable String id, @RequestAttribute EntityUser user){  
        return Payload.successResponse("Release deleted: "+workspaceService.deleteRelease(user.getId(), id));
    }

    @PostMapping(value="/workspace/releases/{id}")
    public ResponseEntity<?> actionRelease(@PathVariable String id, @RequestAttribute EntityUser user, @RequestParam String action) {
        if(workspaceService.actionRelease(user.getId(),id, action)){
            return Payload.successResponse("Successfully "+action);
        }else {
            return Payload.failureResponse("Failed to "+action);
        }
    }
    
    @DeleteMapping(value = "/workspace/tracks/{id}")
    public ResponseEntity<?> deleteTrack(@PathVariable String id, @RequestAttribute EntityUser user){  
        return Payload.successResponse("Track deleted: "+workspaceService.deleteTrack(user.getId(), id));
    }

    @PutMapping(value="/workspace/tracks/{id}")
    public ResponseEntity<?> updateTrack(@PathVariable String id, @RequestAttribute EntityUser user, @Valid DTOTrackUpdate data) {     
        return Payload.successResponse("Track updated "+workspaceService.updateTrack(user.getId(), id, data));
    }

    @GetMapping(value="/workspace/statistics/{artistId}")
    public ResponseEntity<?> getYearlyStats(@RequestAttribute EntityUser user, @PathVariable String artistId,@RequestParam int year) {
        return Payload.successResponse(workspaceService.yearStats(artistId,year));
    }

    @GetMapping(value="/workspace/statistics/{artistId}/releases")
    public ResponseEntity<?> getReleaseStats(@RequestAttribute EntityUser user,
        @PathVariable String artistId, 
        @RequestParam int month, 
        @RequestParam int year,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int limit ) {
        return Payload.successResponse(workspaceService.releaseStats(artistId, month, year, offset, limit));
    }

    @GetMapping(value="/workspace/statistics/{artistId}/tracks")
    public ResponseEntity<?> getTrackStats(@RequestAttribute EntityUser user, 
        @PathVariable String artistId, 
        @RequestParam int month, 
        @RequestParam int year,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int limit ) {
        return Payload.successResponse(workspaceService.trackStats(artistId, month, year, offset, limit));
    }
    
    @PutMapping(value="/workspace/biography")
    public ResponseEntity<?> putMethodName(@RequestParam String biography, @RequestAttribute EntityUser user) {
        workspaceService.updateBiography(biography, user.getId());
        return Payload.successResponse("Bio updated");
    }
    
}
