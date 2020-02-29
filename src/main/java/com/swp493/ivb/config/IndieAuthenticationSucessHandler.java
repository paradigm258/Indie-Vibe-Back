package com.swp493.ivb.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.swp493.ivb.features.common.user.UserSecurityServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * IndieAuthenticationSucessHandler
 */
@Component
public class IndieAuthenticationSucessHandler implements AuthenticationSuccessHandler {

    @Autowired
    UserSecurityServiceImpl userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        try {
            UserDetails user = userDetailsService.loadUserByFbId(oAuth2User.getName());
            OAuth2Request req = new OAuth2Request(null, "user", user.getAuthorities(), true, null, null, null, null,
                    null);
            Authentication principal = new UsernamePasswordAuthenticationToken(user, null);
            OAuth2Authentication auth2 = new OAuth2Authentication(req, principal);
            ServletRequest res = (ServletRequest) request;
            res.setAttribute("authentication", auth2);
            request.getRequestDispatcher("/loginFb").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}