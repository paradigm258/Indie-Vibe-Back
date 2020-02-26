package com.indievibe.Features.Authentication.Controller;

import com.indievibe.Features.Authentication.Model.IndieUser;
import com.indievibe.Features.Authentication.Model.IndieUserPrinciple;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController{
    
    @GetMapping("/getUser")
    public IndieUser getUser(final Authentication authentication) {
        IndieUserPrinciple userDetails = ((IndieUserPrinciple)authentication.getPrincipal());
        return userDetails.getUser();
    }
    @GetMapping("/perform_register")
    public String register(){
        return "";
    }
    @GetMapping("/perform_facebook_register")
    public String facebookRegister(){
        return "";
    }

}