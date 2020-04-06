package com.swp493.ivb.common.relationship;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * RepositoryUserRelease
 */
public interface RepositoryUserRelease extends JpaRepository<EntityUserRelease,String>{

    int countByUserIdAndReleaseNotNullAndAction(String userId, String action);
    int countByUserIdAndReleaseStatusAndAction(String userId, String status, String action);
    List<EntityUserRelease> findByUserIdAndReleaseNotNullAndAction(String userId, String action, Pageable pageable);
    EntityUserRelease findFirstByUserIdAndReleaseNotNullAndAction(String userId, String action);
    boolean existsByUserIdAndReleaseIdAndAction(String userId, String releaseId, String action);
    List<EntityUserRelease> findByReleaseStatusAndUserIdAndAction(String status,String userId, String action, Pageable pageable);
    @Query(value = "SELECT action from user_object where user_id = ?1 and release_id = ?2",nativeQuery = true)
    Set<String> getRelation(String userId, String releaseId);
}