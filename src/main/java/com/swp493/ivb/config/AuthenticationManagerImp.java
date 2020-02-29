package com.swp493.ivb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * AuthenticationManagerImp
 */
@Component
public class AuthenticationManagerImp implements AuthenticationManager{

    @Autowired
    UserDetailsService userService;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal() + "";
        String password = authentication.getCredentials() + "";
    
        UserDetails user = userService.loadUserByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("1000");
        }
        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("1000");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("1001");
        }
        return new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities() );
    }
}