package com.swp493.ivb.common.track;

import java.util.Optional;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

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

@RestController
public class ControllerTrack {

    

    @Autowired
    private ServiceTrack trackService;

    @PostMapping(value = "/tracks/{trackId}")
    public ResponseEntity<?> favoriteAction(@PathVariable String trackId, @RequestParam String action,
            @RequestAttribute EntityUser user) {

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
            return Payload.successResponse("successfully " + action + " track");
        } else {
            return Payload.failureResponse("failed to " + action);
        }

    }

    @GetMapping(value = "/stream/info/{bitrate}/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getTrack(@RequestAttribute("user") EntityUser user, @PathVariable int bitrate,
            @PathVariable(required = true) String id) {

        DTOTrackStreamInfo track = trackService.getTrackStreamInfo(id, bitrate, user.getId());
        return Payload.successResponse(track);

    }

    @GetMapping(value = "/tracks/test/{id}")
    @CrossOrigin(origins = "*")
    ResponseEntity<Payload<DTOTrackFull>> trackTest(@RequestAttribute(name = "user") EntityUser user,
            @PathVariable String id) {
        Optional<DTOTrackFull> track = trackService.getTrackById(id, user.getId());

        return track
                .map(t -> ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(new Payload<DTOTrackFull>().success(t)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Payload<DTOTrackFull>().error("Track not found")));
    }
}
