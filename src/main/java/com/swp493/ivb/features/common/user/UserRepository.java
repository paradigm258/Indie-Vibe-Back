package com.swp493.ivb.features.common.user;


import org.springframework.data.jpa.repository.JpaRepository;


/**
 * IndieUserRepository
 */
public interface UserRepository extends JpaRepository<UserEntity,Long>{
    public UserEntity findByEmail(String email);
    public UserEntity findByFbId(String fbId);
    public Boolean existsByEmail(String email);
    public Boolean existsByFbId(String fbId);
}