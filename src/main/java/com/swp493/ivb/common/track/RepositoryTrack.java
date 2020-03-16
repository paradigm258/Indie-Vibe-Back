package com.swp493.ivb.common.track;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryTrack extends JpaRepository<EntityTrack, String> {

    Optional<EntityTrack> findById(String id);
    boolean existsByIdAndTrackUsersUserIdAndTrackUsersAction(String trackId, String userId, String action);
    List<EntityTrack> findAllByTrackPlaylistsPlaylistId(String playlistId, Pageable page);
}
