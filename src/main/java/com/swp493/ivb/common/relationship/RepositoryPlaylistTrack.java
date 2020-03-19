package com.swp493.ivb.common.relationship;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RepositoryPlaylistTrack
 */
public interface RepositoryPlaylistTrack extends JpaRepository<EntityPlaylistTrack,String>{
    int countByPlaylistId(String playlistId);
    List<EntityPlaylistTrack> findByPlaylistId(String playlistId, Pageable pageable);
}