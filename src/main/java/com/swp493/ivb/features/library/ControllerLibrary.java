package com.swp493.ivb.features.library;

import java.util.NoSuchElementException;

import com.swp493.ivb.common.playlist.ServicePlaylist;
import com.swp493.ivb.common.track.ServiceTrack;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerLibrary {

    private static Logger log = LoggerFactory.getLogger(ControllerLibrary.class);

    @Autowired
    ServicePlaylist playlistService;

    @Autowired
    ServiceTrack trackService;

    @Autowired
    ServiceLibrary libraryService;

    @GetMapping(value = "/library/playlists")
    public ResponseEntity<?> getMyPlaylist(
        @RequestAttribute("user") EntityUser user,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int limit) {
        try {
            return Payload.successResponse(playlistService.getPlaylists(user.getId(), true, offset, limit));
        } catch (NoSuchElementException e){
            log.debug(e.getMessage());
            return Payload.failureResponse("Invalid Id");
        } catch (Exception e) {
            log.error("Error get user playlists", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value = "/library/favorites/tracks")
    public ResponseEntity<?> getFavorites(@RequestAttribute EntityUser user) {
        try {
            return trackService.getUserFavorites(user.getId())
                    .map(list -> Payload.successResponse(list)).orElse(Payload.failureResponse("message"));
        } catch (NoSuchElementException e){
            log.debug(e.getMessage());
            return Payload.failureResponse("Invalid Id");
        } catch (Exception e) {
            log.error("tracks/favorites", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value="/library/{userId}/playlist")
    public ResponseEntity<?> getArtistRelease(@RequestParam String param) {
        try {
            return Payload.successResponse(null);
        } catch (NoSuchElementException e){
            log.debug(e.getMessage());
            return Payload.failureResponse("Invalid Id");
        } catch (Exception e) {
            log.error("Error getting artist release for user", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value="/library/{userId}")
    public ResponseEntity<?> getGeneral(@PathVariable String userId, @RequestAttribute EntityUser user) {
        try {
            return Payload.successResponse(libraryService.getGeneral(user.getId(), userId));
        } catch (NoSuchElementException e){
            log.debug(e.getMessage());
            return Payload.failureResponse("Invalid Id");
        } catch (Exception e) {
            log.error("Error getting artist release for user", e);
            return Payload.internalError();
        }
    }
}
