package com.swp493.ivb.common.track;

import java.net.MalformedURLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.swp493.ivb.common.view.Payload;

@RestController
public class ControllerTrack {

    private static final Logger log = LoggerFactory.getLogger(ControllerTrack.class);

    @Autowired
    RepositoryTrack repo;

    @GetMapping(value = "/tracks/{id}")
    @CrossOrigin(origins = "*")
    Payload<EntityTrack> tracks(@PathVariable String id) {
        return new Payload<EntityTrack>().success(repo.findTrackById(id));
    }

    @GetMapping(value = "/tracks/stream/{id}")
    @CrossOrigin(origins = "*")
    ResponseEntity<ResourceRegion> stream(
            @PathVariable String id, 
            @RequestHeader(HttpHeaders.RANGE) Optional<String> range) {
        EntityTrack entity = repo.findTrackById(id);

        // demo for streaming 128kbps mp3 file
        String url = entity.getMp3128();
        int fileSize = entity.getFileSize128();

        log.debug(range.get());
        HttpRange httpRange = range
                .map(str -> HttpRange.parseRanges(str).get(0))
                .orElse(HttpRange.createByteRange(0, 160000));

        int start = (int) httpRange.getRangeStart(fileSize);
        int length = (int) (httpRange.getRangeEnd(fileSize) - start + 1);

        try {
            Resource resource = new UrlResource(url);
            ResourceRegion region = new ResourceRegion(resource, start, length);
            
            return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(region);
        } catch (MalformedURLException e) {
            log.error("Error with resource URL", e);
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
    }
}
