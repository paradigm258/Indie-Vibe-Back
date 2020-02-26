package com.swp493.indievibe.Features.User;

import com.swp493.indievibe.Features.User.IndieUser;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * IndieUserRepository
 */
public interface IndieUserRepository extends JpaRepository<IndieUser,Long>{
    public IndieUser findByEmail(String email);
    public IndieUser findByFbId(String fbId);
    public Boolean existsByEmail(String email);
    public Boolean existsByFbId(String fbId);
}