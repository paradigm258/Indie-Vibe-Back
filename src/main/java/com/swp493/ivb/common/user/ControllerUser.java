package com.swp493.ivb.common.user;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.swp493.ivb.common.view.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ControllerUser {

    private static final Logger log = LoggerFactory.getLogger(ControllerUser.class);

    @Autowired
    private ServiceUser userService;

    @GetMapping("/library/{id}/profile")
    public ResponseEntity<?> getSimple(@PathVariable String id) {
        try {
            Optional<DTOUserPublic> simple = userService.getUserPublic(id);
            return simple.map(user -> Payload.successResponse(user))
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting other user profile", e);
            return Payload.internalError();
        }
    }

    @PostMapping(value="/users/{userId}")
    public ResponseEntity<?> actionUser(@PathVariable String userId,@RequestAttribute EntityUser user,@RequestParam String action) {
        try {
            switch (action) {
                case "follow":
                    userService.followUser(user.getId(), userId);
                    break;
                case "unfollow":
                    userService.unfolloweUser(user.getId(), userId);
                    break;
                default:
                    break;
            }
            return Payload.successResponse("User "+action+"ed");
        }catch(NoSuchElementException e){
            return Payload.failureResponse("Invalid Id");
        } catch (Exception e) {
            log.error("Error set user follow", e);
            return Payload.internalError();
        }
    }
    
}
