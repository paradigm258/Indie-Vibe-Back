package com.swp493.ivb.common.playlist;

import java.util.NoSuchElementException;

import javax.validation.Valid;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ControllerPlaylist {

    private static Logger log = LoggerFactory.getLogger(ControllerPlaylist.class);

    @Autowired
    ServicePlaylist playlistService;

    @PostMapping(value = "/playlists")
    public ResponseEntity<?> createPlaylist(@RequestAttribute("user") EntityUser user,
            @Valid DTOPlaylistCreate playlistInfo, BindingResult result) {

        try {
            if (result.hasErrors()) {
                FieldError error = result.getFieldError();
                return Payload.failureResponse(error.getField() + " is invalid" + error.getCode());
            }
            return Payload.successResponse(playlistService.createPlaylist(playlistInfo, user.getId()));
        } catch (Exception e) {
            log.error("Error create playlist", e);
            return Payload.internalError();
        }

    }

    @DeleteMapping(value = "/playlists/{id}")
    public ResponseEntity<?> deletePlaylist(@RequestAttribute("user") EntityUser user, @PathVariable String id) {
        try {
            if (playlistService.deletePlaylist(id, user.getId()))
                return Payload.successResponse(id);
            else
                return Payload.failureResponse("No permission");
        }catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid id");
        } catch (Exception e) {
            log.error("Error delete playlist", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value = "/me/playlists")
    public ResponseEntity<?> getMyPlaylist(@RequestAttribute("user") EntityUser user,
        @RequestParam(defaultValue = "0") int offset,@RequestParam(defaultValue = "20") int limit) {
        try {
            return Payload.successResponse(playlistService.getPlaylists(user.getId(), true, offset, limit));
        } catch (Exception e) {
            log.error("Error get user playlists", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value = "/playlists/full/{id}")
    public ResponseEntity<?> getPlaylistFull(@RequestAttribute("user") EntityUser user, @PathVariable String id,
            @RequestParam(defaultValue = "0") int offset,@RequestParam(defaultValue = "20") int limit) {
        try {
            return playlistService.getPlaylistFull(id, user.getId(), offset, limit)
                    .map(p -> Payload.successResponse(p))
                    .orElse(ResponseEntity.noContent().build());
        }catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid id");
        } catch (Exception e) {
            log.error("Error get full playlist", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value = "/playlists/simple/{id}")
    public ResponseEntity<?> getPlaylistSimple(@PathVariable("id") String playlistId,@RequestAttribute("user") EntityUser user) {
        try{
            return playlistService.getPlaylistSimple(playlistId,user.getId())
                                    .map(p -> Payload.successResponse(p))
                                    .orElse(ResponseEntity.noContent().build());
        }catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid id");
        }catch (Exception e){
            log.error("Error get simple playlist", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value = "/{userId}/playlists")
    public ResponseEntity<?> getUserPlaylistSimple(@RequestAttribute("user") EntityUser user, @PathVariable String userId,
    @RequestParam(defaultValue = "0") int offset,@RequestParam(defaultValue = "20")int limit){
        try {
            return Payload.successResponse(playlistService.getPlaylists(userId,false,offset,limit));
        }catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid id");
        } catch (Exception e) {
            log.error("Error get user public playlists", e);
            return Payload.internalError();
        }
    }
    
    @PostMapping(value="/playlists/{playlistId}/track")
    public ResponseEntity<?> addTrack(
        @PathVariable String playlistId, 
        @RequestParam String trackId,
        @RequestAttribute EntityUser user) {
        try {
            if(playlistService.actionPlaylistTrack(playlistId, trackId, "add", user.getId())){
                return Payload.successMessage("Added track to playlist");
            }else{
                return Payload.failureResponse("Can't add track to playlist");
            }
        }catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid id");
        } catch (Exception e) {
            log.error("Error get add track to playlist", e);
            return Payload.internalError();
        }
    }

    @DeleteMapping(value="/playlists/{playlistId}/track")
    public ResponseEntity<?> removeTrack(
        @RequestParam String playlistId, 
        @RequestParam String trackId,
        @RequestAttribute EntityUser user) {
        try {
            if(playlistService.actionPlaylistTrack(playlistId, trackId,"remove", user.getId())){
                return Payload.successMessage("Track removed");
            }else{
                return Payload.failureResponse("Failed to remove track");
            }
        }catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid id");
        } catch (Exception e) {
            log.error("Error remove track from playlist", e);
            return Payload.internalError();
        }
    }

    @PostMapping(value="/playlists/{id}")   
    public ResponseEntity<?> actionPlaylist(
        @RequestAttribute("user") EntityUser user, 
        @PathVariable String id,
        @RequestParam String action) {
        try{
            if(playlistService.actionPlaylist(id, user.getId(), action)){
                return Payload.successMessage("Playlist successfully "+action);
            }else{
                return Payload.failureResponse("Failed to "+action+" playlist");
            }
        }catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid id");
        }catch(Exception e){
            log.error("Error set favorite for playlist", e);
            return Payload.internalError();
        }
    }
    
}
