package com.swp493.ivb.features.common.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.swp493.ivb.config.IndieUserPrinciple;

/**
 * IndieUserDetailsService
 */
@Service
public class UserSecurityServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = repo.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException(username);
        }
        return new IndieUserPrinciple(user);
    }
    public UserDetails loadUserByFbId(String fbId) throws UsernameNotFoundException{
        UserEntity user = repo.findByFbId(fbId);
        if(user == null){
            throw new UsernameNotFoundException(fbId);
        }
        return new IndieUserPrinciple(user);
    }
}