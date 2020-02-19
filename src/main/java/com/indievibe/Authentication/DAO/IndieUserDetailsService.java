package com.indievibe.Authentication.DAO;

import com.indievibe.Authentication.Model.IndieUser;

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
}