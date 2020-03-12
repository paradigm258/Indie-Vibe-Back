package com.swp493.ivb.common.user;

import java.util.Optional;

import com.swp493.ivb.config.DTORegisterForm;
import com.swp493.ivb.config.DTORegisterFormFb;

public interface ServiceUser {

    int countFollowers(String userId);

    Optional<DTOUserPublic> getUserPublic(String id);

    boolean register(DTORegisterForm userForm);

    boolean register(DTORegisterFormFb fbForm);

    boolean existsByEmail(String email);

    boolean existsByFbId(String fbId);

    Optional<EntityUser> findByFbId(String fbId);
    
}
