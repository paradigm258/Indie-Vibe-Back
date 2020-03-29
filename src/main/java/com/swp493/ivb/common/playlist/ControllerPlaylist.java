package com.swp493.ivb.common.playlist;

import java.io.IOException;

import javax.validation.Valid;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ControllerPlaylist {

    @Autowired
    ServicePlaylist playlistService;

    @PostMapping(value = "/playlists")
    public ResponseEntity<?> createPlaylist(@RequestAttribute("user") EntityUser user,
            @Valid DTOPlaylistCreate playlistInfo, BindingResult result) throws IOException {
        if (result.hasErrors()) {
            FieldError error = result.getFieldError();
            return Payload.failureResponse(error.getField() + " is invalid" + error.getCode());
        }
        return Payload.successResponse(playlistService.createPlaylist(playlistInfo, user.getId()));
    }

    @DeleteMapping(value = "/playlists/{id}")
    public ResponseEntity<?> deletePlaylist(@RequestAttribute("user") EntityUser user, @PathVariable String id) {
        if (playlistService.deletePlaylist(id, user.getId()))
            return Payload.successResponse(id);
        else
            return Payload.failureResponse("No permission");

    }

    @GetMapping(value = "/playlists/full/{id}")
    public ResponseEntity<?> getPlaylistFull(@RequestAttribute("user") EntityUser user, @PathVariable String id,
            @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {

        return Payload.successResponse(playlistService.getPlaylistFull(id, user.getId(), offset, limit));

    }

    @GetMapping(value = "/playlists/simple/{id}")
    public ResponseEntity<?> getPlaylistSimple(@PathVariable("id") String playlistId,
            @RequestAttribute("user") EntityUser user) throws Exception {

        return Payload.successResponse(playlistService.getPlaylistSimple(playlistId, user.getId()));
    }

    @PostMapping(value = "/playlists/{playlistId}/track")
    public ResponseEntity<?> addTrack(@PathVariable String playlistId, @RequestParam String trackId,
            @RequestAttribute EntityUser user) {

        if (playlistService.actionPlaylistTrack(trackId, playlistId, "add", user.getId())) {
            return Payload.successResponse("Added track to playlist");
        } else {
            return Payload.failureResponse("Can't add track to playlist");
        }

    }

    @DeleteMapping(value = "/playlists/{playlistId}/track")
    public ResponseEntity<?> removeTrack(@PathVariable String playlistId, @RequestParam String trackId,
            @RequestAttribute EntityUser user) {

        if (playlistService.actionPlaylistTrack(trackId, playlistId, "remove", user.getId())) {
            return Payload.successResponse("Track removed");
        } else {
            return Payload.failureResponse("Failed to remove track");
        }

    }

    @PostMapping(value = "/playlists/{id}")
    public ResponseEntity<?> actionPlaylist(@RequestAttribute("user") EntityUser user, @PathVariable String id,
            @RequestParam String action) {

        if (playlistService.actionPlaylist(id, user.getId(), action)) {
            return Payload.successResponse("Playlist successfully " + action);
        } else {
            return Payload.failureResponse("Failed to " + action + " playlist");
        }

    }
    

    @GetMapping(value = "/stream/playlist/{playlistId}")
    public ResponseEntity<?> streamPlaylist(@PathVariable String playlistId, @RequestAttribute EntityUser user) {
        return Payload.successResponse(playlistService.playlistStream(playlistId, user.getId()));
    }

    @PutMapping(value="/playlists/{id}")
    public ResponseEntity<?> updatePlaylist(
        @PathVariable String id, 
        @RequestAttribute EntityUser user, 
        @Valid DTOPlaylistUpdate update, 
        BindingResult result) {
            if(result.hasErrors()){
                FieldError error = result.getFieldError();
                return Payload.failureResponse(error.getDefaultMessage() + " is invalid");
            }
            if(playlistService.updatePlaylist(user.getId(),id,update)){
                return Payload.successResponse("Update successfully");
            }else{
                return Payload.failureResponse("Update failed");
            }
    }

}
