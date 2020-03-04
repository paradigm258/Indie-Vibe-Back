package com.swp493.ivb.features.common.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/me/simple")
    ResponseEntity<?> me(@RequestAttribute(name = "user") UserEntity userEntity)
    {
        return ResponseEntity.ok(userEntity.getId());
    }

}
