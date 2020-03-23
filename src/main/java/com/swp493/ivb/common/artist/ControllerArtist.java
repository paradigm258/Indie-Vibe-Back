package com.swp493.ivb.common.artist;

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
public class ControllerArtist {

    @Autowired
    ServiceArtist artistService;

    @GetMapping(value="/artists/{artistId}/releases/{type}")
    public ResponseEntity<?> getArtistRelease(@RequestParam String param) {
        return Payload.successResponse(null);
    }

    @GetMapping(value="/artists/{artistId}")
    public ResponseEntity<?> getArtist(@PathVariable String artistId, @RequestAttribute EntityUser user) {
        return Payload.successResponse(artistService.getArtistFull(user.getId(), artistId));
    }
    
    
}
