package com.swp493.ivb.features.common.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("oauth/me")
    ResponseEntity<Map<String,Object>> me(Authentication authentication )
    {
        DefaultOAuth2AuthenticatedPrincipal principal = (DefaultOAuth2AuthenticatedPrincipal)authentication.getPrincipal();
        Map<String,Object> res = principal.getAttributes();
        
        return ResponseEntity.ok(res);
    }

}
