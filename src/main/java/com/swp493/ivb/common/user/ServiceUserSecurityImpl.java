package com.swp493.ivb.common.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.swp493.ivb.config.IndieUserPrincipal;

/**
 * IndieUserDetailsService
 */
@Service
public class ServiceUserSecurityImpl implements UserDetailsService {

    @Autowired
    private RepositoryUser repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EntityUser user = repo.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException(username);
        }
        return new IndieUserPrincipal(user);
    }
    public UserDetails loadUserByFbId(String fbId) throws UsernameNotFoundException{
        EntityUser user = repo.findByFbId(fbId);
        if(user == null){
            throw new UsernameNotFoundException(fbId);
        }
        return new IndieUserPrincipal(user);
    }
}