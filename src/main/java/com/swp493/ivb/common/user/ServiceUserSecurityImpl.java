package com.swp493.ivb.common.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        try {
            Optional<EntityUser> user = repo.findByEmail(username);

            if (!user.isPresent()) {
                throw new UsernameNotFoundException(username);
            }
            return new IndieUserPrincipal(user.get());
        } catch (UsernameNotFoundException e) {
           throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public UserDetails loadUserByFbId(String fbId) throws UsernameNotFoundException {
        Optional<EntityUser> user = repo.findByFbId(fbId);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(fbId);
        }
        return new IndieUserPrincipal(user.get());
    }
}