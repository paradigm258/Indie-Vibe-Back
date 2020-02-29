package com.swp493.ivb.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthenticationController
 */

@RestController
public class AuthenticationController {

    @Autowired
    TokenStore tokenStore;

    @Autowired
    JwtAccessTokenConverter accessTokenConverter;

    @Autowired
    AuthenticationManager manager;

    @GetMapping(value = "/me")
    public ResponseEntity<?> me(@RequestParam String tokeString) {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        OAuth2AccessToken accessToken = null;
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(username, password);
        try{
            Authentication authentication = manager.authenticate(principal);
            DefaultTokenServices service = new DefaultTokenServices();
            service.setTokenStore(tokenStore);
            OAuth2Request request = new OAuth2Request(null, "user", null, true, null, null, null, null, null);
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, authentication);
            accessToken = service.createAccessToken(oAuth2Authentication);
            accessToken = accessTokenConverter.enhance(accessToken, oAuth2Authentication);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Bad credentials");
        }
        
        return ResponseEntity.ok().body(accessToken);
    }

    @RequestMapping(value = "/loginFb")
    public ResponseEntity<?> loginFb(HttpServletRequest request) {
        OAuth2Authentication authentication = (OAuth2Authentication) request.getAttribute("authentication");
        if (authentication != null) {
            DefaultTokenServices service = new DefaultTokenServices();
            service.setTokenStore(tokenStore);
            OAuth2AccessToken accessToken = service.createAccessToken(authentication);
            accessToken = accessTokenConverter.enhance(accessToken, authentication);
        }
        return ResponseEntity.ok().body(null);
    }

}