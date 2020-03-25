package com.swp493.ivb.features.search;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ControllerSearch {
    @Autowired
    ServiceSearch searchService;
    @GetMapping(value="/search/{key}")
    public ResponseEntity<?> search(@PathVariable String key,
    @RequestAttribute EntityUser user, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(searchService.findGeneral(user.getId(), key));
    }
    @GetMapping(value="/search/{key}/tracks")
    public ResponseEntity<?> searchTracks(@PathVariable String key,
    @RequestAttribute EntityUser user, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(searchService.findTrack(user.getId(), key, offset, limit));
    }
    @GetMapping(value="/search/{key}/artists")
    public ResponseEntity<?> searchArtists(@PathVariable String key,
    @RequestAttribute EntityUser user, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(searchService.findArtist(user.getId(), key, offset, limit));
    }
    @GetMapping(value="/search/{key}/playlists")
    public ResponseEntity<?> searchPlaylists(@PathVariable String key,
    @RequestAttribute EntityUser user, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(searchService.findPlaylist(user.getId(), key, offset, limit));
    }
    @GetMapping(value="/search/{key}/releases")
    public ResponseEntity<?> searchReleases(@PathVariable String key,
    @RequestAttribute EntityUser user, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(searchService.findReleases(user.getId(), key, offset, limit));
    }
    @GetMapping(value="/search/{key}/profiles")
    public ResponseEntity<?> searchProfiles(@PathVariable String key,
    @RequestAttribute EntityUser user, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(searchService.findProfile(user.getId(), key, offset, limit));
    }

    @GetMapping(value="/search/{key}/genres")
    public ResponseEntity<?> searchGenres(@PathVariable String key,
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(searchService.findGenre(key, offset, limit));
    }
    
}
