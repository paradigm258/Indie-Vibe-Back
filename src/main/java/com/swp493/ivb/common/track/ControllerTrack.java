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
    private ServiceTrack trackService;

    @GetMapping(value = "/stream/info/{bitrate}/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Payload<DTOTrackStreamInfo>> getTrack(
            @PathVariable int bitrate,
            @PathVariable(required = true) String id) {
        Optional<DTOTrackStreamInfo> track = trackService.getTrackStreamInfo(id, bitrate);

        return track.map(t -> ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Payload<DTOTrackStreamInfo>().success(t)))
        .orElse(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new Payload<DTOTrackStreamInfo>().error("Track not found")));
    }

    @GetMapping(value = "/stream/{bitrate}/{id}")
    @CrossOrigin(origins = "*")
    ResponseEntity<ResourceRegion> stream(
            @PathVariable int bitrate,
            @PathVariable String id, 
            @RequestHeader(HttpHeaders.RANGE) Optional<String> range) {
        DTOTrackStream track = trackService.getTrackStreamById(id, bitrate).get();

        String url = track.getUrl();
        int fileSize = track.getFileSize();
        int tenSecSize = (bitrate * 1000) / 8 * 10;

        HttpRange httpRange = range
                .map(str -> HttpRange.parseRanges(str).get(0))
                .orElse(HttpRange.createByteRange(0, tenSecSize));

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
