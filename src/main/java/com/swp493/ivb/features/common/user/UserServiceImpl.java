package com.swp493.ivb.features.common.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * IndieUserService
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
    public boolean existsByFbId(String fbId){
        return userRepository.existsByFbId(fbId);
    }
    public void register(UserEntity user){

        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setRole("user");
        
        userRepository.save(user);
    }

    @Override
    public int countFollowers(String userId) {
        return userRepository.countFollowers(userId);
    }
}