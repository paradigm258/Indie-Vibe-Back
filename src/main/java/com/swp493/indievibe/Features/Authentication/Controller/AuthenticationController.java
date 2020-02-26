package com.swp493.indievibe.Features.Authentication.Controller;


import javax.validation.Valid;

import com.swp493.indievibe.Features.Authentication.Model.MessageResponse;
import com.swp493.indievibe.Features.User.IndieUser;
import com.swp493.indievibe.Features.User.IndieUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController{
    @Autowired
    IndieUserService userService;
    
    @PostMapping("/perform_register")
    public ResponseEntity<?> register(@Valid @RequestBody IndieUser user,BindingResult result){
        if(!result.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse(result.getFieldError().toString()));
        }

		if (userService.existsByEmail(user.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Email is already in use!"));
        }
        userService.save(user);
        return ResponseEntity.ok("User is valid");
    }
    @PostMapping("/perform_facebook_register")
    public String facebookRegister(){
        return "";
    }

}