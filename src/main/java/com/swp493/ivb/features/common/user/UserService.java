package com.swp493.ivb.features.common.user;

import java.util.Optional;

import com.swp493.ivb.config.DTORegisterForm;

public interface UserService {

    int countFollowers(String userId);

    Optional<UserEntity> getUserForProcessing(String id);

    Optional<UserPublicDTO> getUserPublic(String id);

    boolean register(DTORegisterForm userForm);

    boolean existsByEmail(String email);

    boolean existsByFbId(String fbId);
}
