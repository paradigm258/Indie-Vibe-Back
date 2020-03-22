package com.swp493.ivb.common.user;

import com.swp493.ivb.common.view.Payload;

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

    @Autowired
    private ServiceUser userService;

    @GetMapping("/library/{id}/profile")
    public ResponseEntity<?> getSimple(@PathVariable String id, @RequestAttribute EntityUser user) {
        
            return Payload.successResponse(userService.getUserPublic(id,user.getId()));
        
    }

    @PostMapping(value="/users/{userId}")
    public ResponseEntity<?> actionUser(@PathVariable String userId,@RequestAttribute EntityUser user,@RequestParam String action) {
        
            switch (action) {
                case "follow":
                    userService.followUser(user.getId(), userId);
                    break;
                case "unfollow":
                    userService.unfollowUser(user.getId(), userId);
                    break;
                default:
                    break;
            }
            return Payload.successResponse("User "+action+"ed");
        
    }
    
}
