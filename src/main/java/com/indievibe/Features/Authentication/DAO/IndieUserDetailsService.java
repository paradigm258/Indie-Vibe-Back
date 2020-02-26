package com.indievibe.Features.Authentication.DAO;

import com.indievibe.Features.Authentication.Model.IndieUser;
import com.indievibe.Features.Authentication.Model.IndieUserPrinciple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * IndieUserDetailsService
 */
@Service
public class IndieUserDetailsService implements UserDetailsService {

    @Autowired
    private IndieUserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        IndieUser user = repo.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException(username);
        }
        return new IndieUserPrinciple(user);
    }
    public UserDetails loadUserByFbId(String fbId) throws UsernameNotFoundException{
        IndieUser user = repo.findByFbId(fbId);
        if(user == null){
            throw new UsernameNotFoundException(fbId);
        }
        return new IndieUserPrinciple(user);
    }
}