package com.swp493.ivb.common.relationship;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * RepositoryUserTrack
 */
public interface RepositoryUserTrack extends JpaRepository<EntityUserTrack,String>{

    @Query(value = "SELECT action FROM user_object where user_id=?1 and track_id=?2", nativeQuery = true)
    Set<String> getRelation(String userId, String trackId);
}