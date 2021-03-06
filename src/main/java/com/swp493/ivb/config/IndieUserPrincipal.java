package com.swp493.ivb.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.swp493.ivb.common.user.EntityUser;

/**
 * IndieUserPrinciple
 */
public class IndieUserPrincipal implements UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private EntityUser user;

    public IndieUserPrincipal(EntityUser user) {
        super();
        this.user = user;
    }

    /**
     * @return the user
     */
    public EntityUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(EntityUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role;
        if ("active".equals(user.getPlanStatus())
                || user.getUserRole().getId().equals("r-curator") || user.getUserRole().getId().equals("r-admin")) {
            role = user.getUserRole().getId();
        } else {
            role = "r-free";
        }
        return Collections.singleton(new SimpleGrantedAuthority(role));
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