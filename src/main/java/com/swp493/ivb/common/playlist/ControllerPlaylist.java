package com.swp493.ivb.common.playlist;

import javax.validation.Valid;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
                return ResponseEntity.badRequest()
                        .body(new Payload<>().fail(error.getField() + " is invalid" + error.getCode()));
            }
            return ResponseEntity.ok()
                    .body(new Payload<>().success(playlistService.createPlaylist(playlistInfo, user.getId())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Payload<>().fail("Something is wrong"));
        }

    }

    @DeleteMapping(value = "/playlists/{id}")
    public ResponseEntity<?> deletePlaylist(@RequestAttribute("user") EntityUser user, @PathVariable String id) {
        try {
            if (playlistService.deletePlaylist(id, user.getId()))
                return ResponseEntity.ok().body(new Payload<>().success(id));
            else
                return ResponseEntity.badRequest().body(new Payload<>().fail("No permission"));
        } catch (Exception e) {
            log.error("Error delete playlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Payload<>().fail("Something is wrong"));
        }
    }

    @GetMapping(value = "/me/playlists")
    public ResponseEntity<?> getMyPlaylist(@RequestAttribute("user") EntityUser user,
            @RequestParam(defaultValue = "0") int pageIndex) {
        try {
            return ResponseEntity.ok().body(
                    new Payload<>().success(playlistService.getPlaylists(user.getId(),true,pageIndex))
                );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Payload<>().fail("Something is wrong"));
        }
    }

    @GetMapping(value = "/playlists/full/{id}")
    public ResponseEntity<?> getPlaylistFull(@RequestAttribute("user") EntityUser user, @PathVariable String id,
            @RequestParam(defaultValue = "0") int pageIndex) {
        try {
            return playlistService.getPlaylistFull(id, user.getId(), pageIndex)
                    .map(p -> ResponseEntity.ok().body(new Payload<>().success(p)))
                    .orElse(ResponseEntity.noContent().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Payload<>().fail("Something is wrong"));
        }
    }

    @GetMapping(value = "/{userId}/playlists")
    public ResponseEntity<?> getPlaylistSimple(@RequestAttribute("user") EntityUser user, @PathVariable String id,
    @RequestParam(defaultValue = "0") int pageIndex){
        try {
            return ResponseEntity.ok().body(new Payload<>().success(playlistService.getPlaylists(user.getId(),false,pageIndex)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Payload<>().fail("Something is wrong"));
        }
    }

}
