package com.swp493.ivb.common.user;

import java.util.Optional;

import com.swp493.ivb.common.view.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerUser {

    private static final Logger log = LoggerFactory.getLogger(ControllerUser.class);

    @Autowired
    private ServiceUser userService;

    @GetMapping("/me/simple")
    public ResponseEntity<?> getSimpleMe(@RequestAttribute(name = "user") EntityUser me) {
        try {
            Optional<DTOUserPublic> simple = userService.getUserPublic(me.getId());
            return simple.map(user -> Payload.successResponse(user))
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting user profile", e);
            return Payload.internalError();
        }
    }

    @GetMapping("/users/{id}")
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
}
