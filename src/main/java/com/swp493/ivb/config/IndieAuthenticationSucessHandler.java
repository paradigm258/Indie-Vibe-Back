package com.swp493.ivb.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.swp493.ivb.features.common.user.UserSecurityServiceImpl;

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
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String name = principal.getAttribute("name");
        try {
            UserDetails dbPrinciple = userDetailsService.loadUserByFbId(name);
            Authentication authDB = new UsernamePasswordAuthenticationToken(dbPrinciple,null, dbPrinciple.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authDB);
        } catch (UsernameNotFoundException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            request.logout();
        }
        new SavedRequestAwareAuthenticationSuccessHandler()
        .onAuthenticationSuccess(request, response,SecurityContextHolder.getContext().getAuthentication());
        
    }
}