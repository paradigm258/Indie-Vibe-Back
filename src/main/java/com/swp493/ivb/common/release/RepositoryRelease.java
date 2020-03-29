package com.swp493.ivb.common.release;

import java.util.Date;
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
    int countByGenresIdAndStatusAndDateAfter(String genreId, String status, Date date);
    List<IOnlyId> findByGenresIdAndStatusAndDateAfter(String genreId, String status, Date date, Pageable pageable);
    int countByStatusAndDateAfter(String status, Date date);
    List<IOnlyId> findByStatusAndDateAfter(String status, Date date, Pageable pageable);
    int countByArtistReleaseUserIdAndReleaseTypeId(String artistId, String releaseType);
    int countByArtistReleaseUserIdAndReleaseTypeIdAndStatus(String artistId, String releaseType, String status);
    List<IOnlyId> findByArtistReleaseUserIdAndReleaseTypeId(String artistId, String releaseType, Pageable pageable);
    List<IOnlyId> findByArtistReleaseUserIdAndReleaseTypeIdAndStatus(String artistId, String releaseType, String status, Pageable pageable);
}
