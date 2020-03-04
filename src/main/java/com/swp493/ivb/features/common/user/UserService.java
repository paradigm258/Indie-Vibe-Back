package com.swp493.ivb.features.common.user;

import java.util.Optional;

public interface UserService {

    int countFollowers(String userId);

    Optional<UserEntity> getUserForProcessing(String id);

    Optional<UserPublicDTO> getUserPublic(String id);
}
