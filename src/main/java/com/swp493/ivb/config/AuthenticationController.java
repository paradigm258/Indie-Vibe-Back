package com.swp493.ivb.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.swp493.ivb.features.common.user.UserEntity;
import com.swp493.ivb.features.common.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthenticationController
 */

@RestController
@CrossOrigin
public class AuthenticationController {

    @Autowired
    TokenStore tokenStore;

    @Autowired
    AuthenticationManager manager;

    @Autowired
    UserService userService;

    @Autowired
    DefaultTokenServices services;

    @GetMapping(value = "/me")
    public ResponseEntity<?> me(@RequestAttribute UserEntity user) {
        return ResponseEntity.ok().body(user);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(email, password);
        try {
            Authentication authentication = manager.authenticate(principal);
            return TokenResponse(authentication);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Bad credentials");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @RequestMapping(value = "/loginFb")
    public ResponseEntity<?> loginFb(HttpServletRequest request) {
        Authentication authentication = (Authentication) request.getAttribute("authentication");
        if (authentication != null) {
            return TokenResponse(authentication);
        }
        return ResponseEntity.ok().body(null);
    }

    private ResponseEntity<?> TokenResponse(Authentication authentication) {
        IndieUserPrincipal user = (IndieUserPrincipal) authentication.getPrincipal();
        OAuth2Request request = new OAuth2Request(null, user.getUser().getId(), user.getAuthorities(), true, null, null, null, null,
                null);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, authentication);
        OAuth2AccessToken accessToken = services.createAccessToken(oAuth2Authentication);
        return ResponseEntity.ok().body(accessToken);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@Valid DTORegisterForm registerForm, BindingResult result) {
        if(result.hasErrors()){
            FieldError error = result.getFieldError();
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", error.getDefaultMessage()+" is invalid");
        }
        if(!registerForm.getPassword().equals(registerForm.getCfPassword())){
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", "Password not match");
        }
        if(userService.existsByEmail(registerForm.getEmail())){
            return messageResponse(HttpStatus.BAD_REQUEST,"failed", "Email already exists");
        }
        try {
            userService.register(registerForm);
            return messageResponse(HttpStatus.ACCEPTED, "success", "Your account has been created.");
        } catch (Exception e) {
            e.printStackTrace();
            return messageResponse(HttpStatus.INTERNAL_SERVER_ERROR, "failed", "Failed to create account");
        }
        
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization")String bearerToken) {
        try {
            String tokenValue = bearerToken.substring(7, bearerToken.length());
            OAuth2AccessToken token = tokenStore.readAccessToken(tokenValue);
            if (token != null){
                tokenStore.removeAccessToken(token);
                return messageResponse(HttpStatus.OK, "success", "Logout success");
            }
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", "No token");
        } catch (Exception e) {
            e.printStackTrace();
            return messageResponse(HttpStatus.INTERNAL_SERVER_ERROR, "failed", "Something is wrong");
        }
    }

    private ResponseEntity<?> messageResponse(HttpStatus code, String status, String data) {
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("data", data);
        return ResponseEntity.status(HttpStatus.OK.value()).body(response);
    }

}