package com.swp493.ivb.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.user.ServiceUserSecurityImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * AuthenticationController
 */

@RestController
@CrossOrigin
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Value("${spring.security.oauth2.client.registration.facebook.access-token}")
    String access_token;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    AuthenticationManager manager;

    @Autowired
    ServiceUser userService;

    @Autowired
    ServiceUserSecurityImpl userSecurityService;

    @Autowired
    DefaultTokenServices services;

    @GetMapping(value = "/me")
    public ResponseEntity<?> me(@RequestAttribute EntityUser user) {
        return ResponseEntity.ok().body(userService.findByFbId(user.getId()));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(email, password);
        try {
            Authentication authentication = manager.authenticate(principal);
            return TokenResponse(authentication);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Bad credentials");
        } catch (Exception e) {
            logger.error("Location: /login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @RequestMapping(value = "/login/facebook")
    public ResponseEntity<?> loginFb(String userFbId, String userFbToken) {
        try {
            UserDetails userDetails = userSecurityService.loadUserByFbId(userFbId);

            if (checkFbToken(userFbId, userFbToken) == true) {
                UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                return TokenResponse(userAuth);
            } else {
                return messageResponse(HttpStatus.BAD_REQUEST, "failed", "Token invalid");
            }
        } catch (UsernameNotFoundException ex) {
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", "Not connected");
        } catch (Exception e) {
            logger.error("Location: /login/facebook", e);
            return messageResponse(HttpStatus.INTERNAL_SERVER_ERROR, "failed", "Something went wrong");
        }
    }

    private ResponseEntity<?> TokenResponse(Authentication authentication) {
        IndieUserPrincipal user = (IndieUserPrincipal) authentication.getPrincipal();
        OAuth2Request request = new OAuth2Request(null, user.getUser().getId(), user.getAuthorities(), true, null, null,
                null, null, null);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, authentication);
        OAuth2AccessToken accessToken = services.createAccessToken(oAuth2Authentication);
        return ResponseEntity.ok().body(accessToken);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@Valid DTORegisterForm registerForm, BindingResult result) {
        if (result.hasErrors()) {
            FieldError error = result.getFieldError();
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", error.getDefaultMessage() + " is invalid");
        }
        if (!registerForm.getPassword().equals(registerForm.getCfPassword())) {
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", "Password not match");
        }
        if (userService.existsByEmail(registerForm.getEmail())) {
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", "Email already exists");
        }
        try {
            userService.register(registerForm);
            return messageResponse(HttpStatus.ACCEPTED, "success", "Your account has been created.");
        } catch (Exception e) {
            logger.error("Location: /register", e);
            return messageResponse(HttpStatus.INTERNAL_SERVER_ERROR, "failed", "Failed to create account");
        }

    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization") String bearerToken) {
        try {
            String tokenValue = bearerToken.substring(7, bearerToken.length());
            OAuth2AccessToken token = tokenStore.readAccessToken(tokenValue);
            if (token != null) {
                tokenStore.removeAccessToken(token);
                return messageResponse(HttpStatus.OK, "success", "Logout success");
            }
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", "No token");
        } catch (Exception e) {
            logger.error("Location: /logout", e);
            return messageResponse(HttpStatus.INTERNAL_SERVER_ERROR, "failed", "Something is wrong");
        }
    }

    private ResponseEntity<?> messageResponse(HttpStatus code, String status, String data) {
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("data", data);
        return ResponseEntity.status(code.value()).body(response);
    }

    private boolean checkFbToken(String userFbId, String userFbToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com/v6.0/debug_token").queryParam("input_token", userFbToken)
                .queryParam("access_token", access_token);
        logger.debug("Facebook profile uri {}", uriBuilder.toUriString());

        JsonNode resp = restTemplate.getForObject(uriBuilder.toUriString(), JsonNode.class);
        return resp.findPath("data").findValue("is_valid").asBoolean()
                && resp.findPath("data").findValue("user_id").asText().equals(userFbId);
    }

    @PostMapping(value = "/register/facebook")
    public ResponseEntity<?> registerFb(@Valid DTORegisterFormFb fbForm, BindingResult result) {
        if (result.hasErrors()) {
            FieldError error = result.getFieldError();
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", error.getDefaultMessage() + " is invalid");
        }
        if (userService.existsByFbId(fbForm.getFbId())) {
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", "FbId already registered");
        }
        if (checkFbToken(fbForm.getFbId(), fbForm.getFbToken())) {
            if (userService.register(fbForm)) {
                return messageResponse(HttpStatus.ACCEPTED, "success", "Your accout has been created");
            }
            return messageResponse(HttpStatus.INTERNAL_SERVER_ERROR, "failed", "Something went wrong");
        }else{
            return messageResponse(HttpStatus.BAD_REQUEST, "failed", "Token invalid");
        }
    }

}