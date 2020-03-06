package com.swp493.ivb.common.user;

import java.util.Optional;

import com.swp493.ivb.config.DTORegisterForm;

public interface ServiceUser {

    int countFollowers(String userId);

    Optional<EntityUser> getUserForProcessing(String id);

    Optional<DTOUserPublic> getUserPublic(String id);

    boolean register(DTORegisterForm userForm);

    boolean existsByEmail(String email);

    boolean existsByFbId(String fbId);

    Optional<EntityUser> findByFbId(String fbId);
}
