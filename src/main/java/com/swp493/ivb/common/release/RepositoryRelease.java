package com.swp493.ivb.common.release;

import java.util.List;

import com.swp493.ivb.common.user.IOnlyId;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryRelease extends JpaRepository<EntityRelease, String> {
    List<String> findAllTrackIdById(String id);
    boolean existsByIdAndStatus(String id, String status);
    boolean existsByIdAndReleaseUsersUserIdAndReleaseUsersAction(String id, String userId, String action);
    List<IOnlyId> findByTitleIgnoreCaseContainingAndStatus(String key, String status, Pageable pageable);
    int countByTitleIgnoreCaseContainingAndStatus(String key, String status);
}
