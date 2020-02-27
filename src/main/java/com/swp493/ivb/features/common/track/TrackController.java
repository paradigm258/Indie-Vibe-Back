package com.swp493.ivb.features.common.track;

import java.util.Optional;

import com.swp493.ivb.features.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackController {

    @Autowired
    TrackRepository repo;
    @GetMapping(value = "/tracks")
    Payload<TrackEntity> tracks(String id){
        return new Payload<TrackEntity>().success(repo.findTrackById(id));
    }

    @GetMapping(value = "/tracks/stream/{id}")
    ResourceRegion stream(@PathVariable String id,@RequestHeader(HttpHeaders.RANGE) Optional<String> range){
        return null;
    }
}
