package com.swp493.ivb.config;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    JwtAccessTokenConverter accessTokenConverter;

    @Autowired
    AuthenticationManager manager;

    DefaultTokenServices services;

    @GetMapping(value = "/me")
    public ResponseEntity<?> me(@RequestParam String tokeString) {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(username, password);
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
        services = new DefaultTokenServices();
        services.setTokenStore(tokenStore);
        IndieUserPrincipal user = (IndieUserPrincipal) authentication.getPrincipal();
        OAuth2Request request = new OAuth2Request(null, user.getUser().getId(), null, true, null, null, null, null,
                null);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, authentication);
        OAuth2AccessToken accessToken = services.createAccessToken(oAuth2Authentication);
        accessToken = accessTokenConverter.enhance(accessToken, oAuth2Authentication);
        return ResponseEntity.ok().body(accessToken);
    }

}