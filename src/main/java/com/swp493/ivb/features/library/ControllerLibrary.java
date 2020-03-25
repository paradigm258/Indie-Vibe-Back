package com.swp493.ivb.features.library;

import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.playlist.ServicePlaylist;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.track.ServiceTrack;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerLibrary {

    @Autowired
    ServicePlaylist playlistService;

    @Autowired
    ServiceTrack trackService;

    @Autowired
    ServiceLibrary libraryService;

    @Autowired
    ServiceRelease releaseService;

    @Autowired 
    ServiceArtist artistService;

    @Autowired
    ServiceUser userService;

    @GetMapping(value = "/library/playlists/{type:own|favorite}")
    public ResponseEntity<?> getMyPlaylist(
        @PathVariable String type,
        @RequestAttribute("user") EntityUser user,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(playlistService.getPlaylists(user.getId(), user.getId(), offset, limit, type));
    }

    @GetMapping(value = "/library/{userId}/playlists/{type:own|favorite}")
    public ResponseEntity<?> getUserPlaylistSimple(
            @PathVariable String type,
            @RequestAttribute("user") EntityUser user,
            @PathVariable String userId, @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {

        return Payload.successResponse(playlistService.getPlaylists(userId, user.getId(), offset, limit, type));
    }

    @GetMapping(value="/library/{userId}")
    public ResponseEntity<?> getGeneral(@PathVariable String userId, @RequestAttribute EntityUser user) {
        return Payload.successResponse(libraryService.getGeneral(user.getId(), userId));
    }

    @GetMapping(value="/library/{userId}/releases/{type:own|favorite}")
    public ResponseEntity<?> getReleases(@RequestAttribute EntityUser user, @PathVariable String userId, 
    @PathVariable String type,
    @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(releaseService.getReleases(userId, user.getId(), offset, limit, type));
    }
    
    @GetMapping(value="/library/{userId}/tracks/{type:own|favorite}")
    public ResponseEntity<?> getUserTracks(@RequestAttribute EntityUser user, 
        @PathVariable String userId, 
        @PathVariable String type,
        @RequestParam(defaultValue = "0") int offset, 
        @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(trackService.getTracks(userId, user.getId(), offset, limit, type));
    }
    
    @GetMapping(value="/library/{userId}/artists")
    public ResponseEntity<?>  getUserAtrists(@RequestAttribute EntityUser user, 
    @PathVariable String userId, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(artistService.getArtists(userId, user.getId(), offset, limit));
    }
    
    @GetMapping(value="/library/{userId}/followings")
    public ResponseEntity<?> getFollowings(@RequestAttribute EntityUser user, 
    @PathVariable String userId, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(userService.getFollowings(userId, user.getId(), offset, limit));
    }
    
    @GetMapping(value="/library/{userId}/followers")
    public ResponseEntity<?> getFollowers(@RequestAttribute EntityUser user, 
    @PathVariable String userId, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(userService.getFollowers(userId, user.getId(), offset, limit));
    }
}
