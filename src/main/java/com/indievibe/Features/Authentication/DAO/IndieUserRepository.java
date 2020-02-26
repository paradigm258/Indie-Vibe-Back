package com.indievibe.Features.Authentication.DAO;

import com.indievibe.Features.Authentication.Model.IndieUser;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * IndieUserRepository
 */
public interface IndieUserRepository extends JpaRepository<IndieUser,Long>{
    public IndieUser findByEmail(String email);
    public IndieUser findByFbId(String fbId);
}