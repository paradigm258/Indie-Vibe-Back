package com.swp493.ivb.features.common.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * IndieUserRepository
 */
public interface UserRepository extends JpaRepository<UserEntity, String> {
    public UserEntity findByEmail(String email);

    public UserEntity findByFbId(String fbId);

    public Boolean existsByEmail(String email);

    public Boolean existsByFbId(String fbId);

    @Query(
            value = "SELECT COUNT(follower_id) FROM user_follow_user WHERE followed_id=:userId",
            nativeQuery = true)
    public int countFollowers(@Param("userId") String userId);
}