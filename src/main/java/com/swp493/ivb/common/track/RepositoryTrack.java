package com.swp493.ivb.common.track;

import java.util.List;
import java.util.Optional;

import com.swp493.ivb.common.user.IOnlyId;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryTrack extends JpaRepository<EntityTrack, String> {

    Optional<EntityTrack> findById(String id);
    int countByReleaseId(String releaseId);
    boolean existsByIdAndTrackUsersUserIdAndTrackUsersAction(String trackId, String userId, String action);
    boolean existsByIdAndStatus(String id, String status);
    List<EntityTrack> findAllByReleaseId(String releaseId, Pageable pageable);
    List<EntityTrack> findAllByReleaseId(String releaseId);
    List<IOnlyId> findByTitleIgnoreCaseContainingAndStatus(String key, String status, Pageable pageable);
    int countByTitleIgnoreCaseContainingAndStatus(String key, String status);
    List<IOnlyId> findByReleaseId(String releaseId, Pageable pageable);
    @Query(value = "select track_id from user_object where track_id is not null and user_id = ?1",nativeQuery = true)
    List<String> getFavIdList(String userId);
}
