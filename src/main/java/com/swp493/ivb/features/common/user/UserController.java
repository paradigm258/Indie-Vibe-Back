package com.swp493.ivb.features.common.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.swp493.ivb.common.view.Payload;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me/simple")
    public ResponseEntity<Payload<UserPublicDTO>> getSimpleMe(@RequestAttribute(name = "user") UserEntity me) {
        Optional<UserPublicDTO> simple = userService.getUserPublic(me.getId());

        return simple.map(user -> ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Payload<UserPublicDTO>().success(user)))
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new Payload<UserPublicDTO>().error("User not found")));
    }
}
