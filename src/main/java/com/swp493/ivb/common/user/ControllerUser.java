package com.swp493.ivb.common.user;

import java.util.Optional;

import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerUser {

    @Autowired
    private ServiceUser userService;

    @GetMapping("/me/simple")
    public ResponseEntity<Payload<DTOUserPublic>> getSimpleMe(@RequestAttribute(name = "user") EntityUser me) {
        Optional<DTOUserPublic> simple = userService.getUserPublic(me.getId());

        return simple.map(user -> ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Payload<DTOUserPublic>().success(user)))
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new Payload<DTOUserPublic>().error("User not found")));
    }
}
