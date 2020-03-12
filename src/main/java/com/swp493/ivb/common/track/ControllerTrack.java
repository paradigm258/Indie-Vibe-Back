package com.swp493.ivb.common.track;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

@RestController
public class ControllerTrack {

    private static final Logger log = LoggerFactory.getLogger(ControllerTrack.class);

    @Autowired
    private ServiceTrack trackService;

    @PostMapping(value = "/track/{trackId}")
    public ResponseEntity<?> favoriteAction(@PathVariable String trackId, @RequestParam String action,
            @RequestAttribute EntityUser user) {
        try {
            boolean success = false;
            switch (action) {
                case "favorite":
                    success = trackService.favoriteTrack(user.getId(), trackId);
                    break;
                case "unfavorite":
                    success = trackService.unfavoriteTrack(user.getId(), trackId);
                    break;
                default:
                    break;
            }
            if (success) {
                return ResponseEntity.ok().body(new Payload<>().success("successfully " + action + " track"));
            } else {
                return ResponseEntity.badRequest().body(new Payload<>().fail("failed to " + action));
            }
        } catch (Exception e) {
            log.error("/track addFavorite", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping(value="/tracks/favorites")
    public ResponseEntity<?> getFavorites(@RequestAttribute EntityUser user) {
        try {
            return trackService.getFavorites(user.getId()).map(list ->{
                return ResponseEntity.ok().body(new Payload<>().success(list));
            }).orElse(ResponseEntity.badRequest().body(new Payload<>().fail("No data")));
        } catch (Exception e) {
            log.error("tracks/favorites", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Payload<>().error("Something is wrong"));
        }
    }

    @GetMapping(value = "/stream/info/{bitrate}/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Payload<DTOTrackStreamInfo>> getTrack(@PathVariable int bitrate,
            @PathVariable(required = true) String id) {
        Optional<DTOTrackStreamInfo> track = trackService.getTrackStreamInfo(id, bitrate);

        return track
                .map(t -> ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(new Payload<DTOTrackStreamInfo>().success(t)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Payload<DTOTrackStreamInfo>().error("Track not found")));
    }

    @GetMapping(value = "/tracks/test/{id}")
    @CrossOrigin(origins = "*")
    ResponseEntity<Payload<DTOTrackFull>> trackTest(
            @PathVariable String id) {
        Optional<DTOTrackFull> track = trackService.getTrackById(id);
        
        return track.map(t -> ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Payload<DTOTrackFull>().success(t)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Payload<DTOTrackFull>().error("Track not found")));
    }
}
