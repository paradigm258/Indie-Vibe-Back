package com.swp493.ivb.common.relationship;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * RepositoryUserTrack
 */
public interface RepositoryUserTrack extends JpaRepository<EntityUserTrack,String>{

    int countByUserIdAndTrackStatusAndAction(String userId, String status, String action);
    int countByUserIdAndTrackNotNullAndAction(String userId, String action);
    boolean existsByTrackIdAndUserIdAndAction(String trackId, String userId, String action);
    List<EntityUserTrack> findAllByUserIdAndTrackNotNullAndAction(String userId, String action, Pageable pageable);
    EntityUserTrack findByTrackIdAndAction(String trackId, String action);
    List<EntityUserTrack> findAllByUserIdAndTrackStatusAndAction(String userId, String status, String action, Pageable pageable);
    @Query(value = "SELECT action FROM user_object where user_id=?1 and track_id=?2", nativeQuery = true)
    Set<String> getRelation(String userId, String trackId);
    @Query(value = "SELECT track_id FROM user_object where user_id=?1 and action ='own' and track_id is not null",nativeQuery = true)
    List<String> getOwnTracks(String userId);
}