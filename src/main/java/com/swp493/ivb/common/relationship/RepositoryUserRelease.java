package com.swp493.ivb.common.relationship;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * RepositoryUserRelease
 */
public interface RepositoryUserRelease extends JpaRepository<EntityUserRelease,String>{

    @Query(value = "SELECT action from user_object where user_id = ?1 and release_id = ?2",nativeQuery = true)
    Set<String> getRelation(String userId, String releaseId);
}