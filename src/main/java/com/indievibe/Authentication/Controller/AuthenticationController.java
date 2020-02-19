package com.indievibe.Authentication.Controller;

import com.indievibe.Authentication.Model.IndieUser;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController{
    @GetMapping("/perform_login")
    public IndieUser performLogin(){
        return new IndieUser();
    }
}