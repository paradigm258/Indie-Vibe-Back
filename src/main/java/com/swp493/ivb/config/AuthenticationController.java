package com.swp493.ivb.config;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.user.ServiceUserSecurityImpl;
import com.swp493.ivb.common.view.Payload;
import com.swp493.ivb.util.BillingUtils;
import com.swp493.ivb.util.ConfirmTokenUtils;
import com.swp493.ivb.util.EmailUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
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
import org.springframework.web.bind.annotation.RequestBody;


/**
 * AuthenticationController
 */

@RestController
@CrossOrigin
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Value("${FB_ACCESSTOKEN}")
    String access_token;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    ConfirmTokenUtils confirmUtils;

    @Autowired
    AuthenticationManager manager;

    @Autowired
    ServiceUser userService;

    @Autowired
    ServiceUserSecurityImpl userSecurityService;

    @Autowired
    DefaultTokenServices tokenServices;

    @Autowired
    BillingUtils billingUtils;

    @GetMapping(value = "/me")
    public ResponseEntity<?> me(@RequestAttribute EntityUser user) {
        return ResponseEntity.ok().body(user.getId());
    }

    @PostMapping(value = "/token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String bearerToken) {
        if (bearerToken.isEmpty() || !bearerToken.startsWith("Bearer "))
            return ResponseEntity.badRequest().body("Bad credentials");
        String refreshTokenValue = bearerToken.substring(7, bearerToken.length());
        TokenRequest tokenRequest = new TokenRequest(null, "web", null, null);
        try {
            OAuth2AccessToken accessToken = tokenServices.refreshAccessToken(refreshTokenValue, tokenRequest);
            return ResponseEntity.ok().body(accessToken);
        } catch (InvalidGrantException e) {
            return Payload.failedAuthorization("Invalid refresh token");
        }     
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(email, password);
        try {
            Authentication authentication = manager.authenticate(principal);
            return TokenResponse(authentication);
        } catch (BadCredentialsException e) {
            return Payload.failedAuthorization("Bad credential");
        } catch (DisabledException e){
            return Payload.failedAuthorization("inactive");
        }

    }

    @PostMapping(value = "/login/facebook")
    public ResponseEntity<?> loginFb(String userFbId, String userFbToken) {
        try {
            UserDetails userDetails = userSecurityService.loadUserByFbId(userFbId);

            if (checkFbToken(userFbId, userFbToken)) {
                UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                return TokenResponse(userAuth);
            } else {
                return Payload.failedAuthorization("Token invalid");
            }
        } catch (UsernameNotFoundException e) {
            return Payload.failedAuthorization("Facebook account not connected");
        }

    }

    private ResponseEntity<?> TokenResponse(Authentication authentication) {
        IndieUserPrincipal user = (IndieUserPrincipal) authentication.getPrincipal();
        userService.updateUserPlan(user.getUser());
        OAuth2Request request = new OAuth2Request(null, "web", user.getAuthorities(), true, null, null, null, null,
                null);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, authentication);
        OAuth2AccessToken accessToken = tokenStore.getAccessToken(oAuth2Authentication);
        if (accessToken != null) {
            tokenServices.revokeToken(accessToken.getValue());
        }
        accessToken = tokenServices.createAccessToken(oAuth2Authentication);
        return ResponseEntity.ok().body(accessToken);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@Valid DTORegisterForm registerForm, BindingResult result) {
        if (result.hasErrors()) {
            FieldError error = result.getFieldError();
            return Payload.failureResponse(error.getDefaultMessage() + " is invalid");
        }
        if (!registerForm.getPassword().equals(registerForm.getCfPassword())) {
            return Payload.failureResponse("Password not match");
        }
        if (userService.existsByEmail(registerForm.getEmail())) {
            return Payload.failureResponse("Email already exists");
        }
        userService.register(registerForm);
        return Payload.successResponse("Your account has been created.");
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization") String bearerToken) {
        String tokenValue = bearerToken.substring(7, bearerToken.length());
        OAuth2AccessToken token = tokenStore.readAccessToken(tokenValue);
        if (token != null) {
            tokenStore.removeAccessToken(token);
            return Payload.successResponse("Logout success");
        }
        return Payload.failureResponse("No token");
    }

    private boolean checkFbToken(String userFbId, String userFbToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com/v6.0/debug_token")
                .queryParam("input_token", userFbToken)
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
            return Payload.failureResponse(error.getDefaultMessage() + " is invalid");
        }
        if (userService.existsByFbId(fbForm.getFbId())) {
            return Payload.failureResponse("FbId already registered");
        }
        if (checkFbToken(fbForm.getFbId(), fbForm.getFbToken())) {
            userService.register(fbForm);
            return Payload.successResponse("Your accout has been created");
        } else {
            return Payload.failureResponse("Token invalid");
        }
    }

    @PostMapping(value = "/activate")
    public ResponseEntity<?> activateAccount(@RequestParam String activateToken, @RequestParam String id){
        try {
            String tokenId = confirmUtils.decodeConfirmToken(activateToken);
            if(!id.equals(tokenId)) return Payload.failureResponse("Incorrect id"); 
            userService.activateUser(id);
            return Payload.successResponse("Account activated");
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
            logger.info("Invalid token", e);
			return Payload.failureResponse("Token invalid");
		}
    }

    @PostMapping(value="/reset")
    public ResponseEntity<?> resendToken(String email, String password) {
        userService.resendActivateEmail(email, password);
        return Payload.successResponse("Success");
    }
    
}