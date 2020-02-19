package com.indievibe.Authentication.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indievibe.Authentication.Model.IndieUser;/**
 * IndieUserRepository
 */
public interface IndieUserRepository extends JpaRepository<IndieUser,Long>{
    public IndieUser findByEmail(String email);
}