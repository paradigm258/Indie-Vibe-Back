package com.swp493.ivb.features.library;

import com.swp493.ivb.common.playlist.ServicePlaylist;
import com.swp493.ivb.common.track.ServiceTrack;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

public class ControllerLibrary {

    private static Logger log = LoggerFactory.getLogger(ControllerLibrary.class);

    @Autowired
    ServicePlaylist playlistService;

    @Autowired
    ServiceTrack trackService;

    @GetMapping(value = "/library/playlists")
    public ResponseEntity<?> getMyPlaylist(
        @RequestAttribute("user") EntityUser user,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int limit) {
        try {
            return Payload.successResponse(playlistService.getPlaylists(user.getId(), true, offset, limit));
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
        } catch (Exception e) {
            log.error("tracks/favorites", e);
            return Payload.internalError();
        }
    }
}
