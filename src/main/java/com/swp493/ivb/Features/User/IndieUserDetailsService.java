<<<<<<< HEAD:src/main/java/com/swp493/ivb/Features/User/IndieUserDetailsService.java
package com.swp493.indievibe.Features.User;

import com.swp493.indievibe.Features.Authentication.Model.IndieUserPrinciple;
import com.swp493.indievibe.Features.User.IndieUser;

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
=======
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
>>>>>>> 3e2dfee47721ffdf0dc7710b5dd3a21483996944:src/main/java/com/swp493/ivb/features/common/user/UserSecurityServiceImpl.java
}