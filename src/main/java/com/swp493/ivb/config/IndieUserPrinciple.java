package com.swp493.ivb.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.swp493.ivb.features.common.user.UserEntity;

/**
 * IndieUserPrinciple
 */
public class IndieUserPrinciple implements UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private UserEntity user;

    public IndieUserPrinciple(UserEntity user) {
        super();
        this.user = user;
    }

    /**
     * @return the user
     */
    public UserEntity getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}