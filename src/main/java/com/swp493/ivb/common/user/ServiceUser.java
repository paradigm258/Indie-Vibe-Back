package com.swp493.ivb.common.user;

import java.util.Optional;

import com.swp493.ivb.config.DTORegisterForm;
import com.swp493.ivb.config.DTORegisterFormFb;

public interface ServiceUser {

    int countFollowers(String userId) throws Exception;

    Optional<DTOUserPublic> getUserPublic(String id) throws Exception;

    void register(DTORegisterForm userForm) throws Exception;

    void register(DTORegisterFormFb fbForm) throws Exception;

    boolean existsByEmail(String email) throws Exception;

    boolean existsByFbId(String fbId) throws Exception;

    Optional<EntityUser> findByFbId(String fbId) throws Exception;
    
    void followUser(String followerId, String followedId);

    void unfolloweUser(String followerId, String followedId);
}
