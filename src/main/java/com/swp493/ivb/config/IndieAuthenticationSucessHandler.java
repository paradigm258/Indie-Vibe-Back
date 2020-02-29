package com.swp493.ivb.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.swp493.ivb.features.common.user.UserSecurityServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
            ServletRequest res = (ServletRequest) request;
            res.setAttribute("authentication", authentication);
            request.getRequestDispatcher("/loginFb").forward(request, response);
    }
}