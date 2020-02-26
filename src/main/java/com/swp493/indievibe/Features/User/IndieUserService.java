package com.swp493.indievibe.Features.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * IndieUserService
 */
@Service
public class IndieUserService {

    @Autowired
    IndieUserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
    public boolean existsByFbId(String fbId){
        return userRepository.existsByFbId(fbId);
    }
    public void save(IndieUser user){
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}