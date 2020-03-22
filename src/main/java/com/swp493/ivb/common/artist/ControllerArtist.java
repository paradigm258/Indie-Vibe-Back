package com.swp493.ivb.common.artist;

import com.swp493.ivb.common.view.Payload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ControllerArtist {

    @GetMapping(value="/artist/{artistId}/release")
    public ResponseEntity<?> getArtistRelease(@RequestParam String param) {
        return Payload.successResponse(null);
    }
    
}
