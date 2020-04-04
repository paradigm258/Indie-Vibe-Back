package com.swp493.ivb.features.browse;

import org.springframework.web.bind.annotation.RestController;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class ControllerBrowse {

    @Autowired
    ServiceBrowse browseService;

    @GetMapping(value="/browse/releases")
    public ResponseEntity<?> browseReleases(@RequestAttribute EntityUser user) {
        return Payload.successResponse(browseService.getReleaseGenreCollections(user.getId()));
    }

    @GetMapping(value="/browse/genres/{id}")
    public ResponseEntity<?> browseGenres(@RequestAttribute EntityUser user, @PathVariable String id) {
        return Payload.successResponse(browseService.getGenre(user.getId(), id));
    }

    @GetMapping(value="/browse/genres/{id}/{type:playlists|releases}")
    public ResponseEntity<?> browseGenresType(
        @RequestAttribute EntityUser user,
        @PathVariable String id, 
        @PathVariable String type, 
        @RequestParam(defaultValue = "0") int offset, 
        @RequestParam(defaultValue = "20") int limit) {
        switch (type) {
            case "playlists":
                return Payload.successResponse(browseService.getGenrePlaylist(user.getId(), id, offset, limit));
            case "releases":
                return Payload.successResponse(browseService.getGenreRelease(user.getId(), id, offset, limit)); 
            default:
                return Payload.failureResponse("Unknown type");
        }
    }

    @GetMapping(value="/browse/general")
    public ResponseEntity<?> browseGeneral(@RequestAttribute EntityUser user) {
        return Payload.successResponse(browseService.getGeneral(user.getId()));
    }

    @GetMapping(value="/home")
    public ResponseEntity<?> getHome(@RequestAttribute EntityUser user) {
        return Payload.successResponse(browseService.getHome(user.getId()));
    }
    

}
