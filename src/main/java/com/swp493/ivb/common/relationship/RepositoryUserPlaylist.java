package com.swp493.ivb.common.relationship;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * RepositoryUserPlaylist
 */
public interface RepositoryUserPlaylist extends JpaRepository<EntityUserPlaylist,String> {
    boolean existsByUserIdAndPlaylistIdAndAction(String userId, String playlistId, String action);
    int countByPlaylistIdAndAction(String playlistId, String action);
    int countByUserIdAndPlaylistNotNullAndAction(String userId, String action);
    int countByUserIdAndPlaylistStatusAndAction(String userId, String status, String action);
    List<EntityUserPlaylist> findByPlaylistStatusAndUserIdAndAction(String status, String userId, String action, Pageable page);
    List<EntityUserPlaylist> findByUserIdAndPlaylistNotNullAndAction(String userId,String type, Pageable page);
    @Query(value = "SELECT action FROM user_object WHERE user_id = ?1 and playlist_id = ?2",nativeQuery = true)
    Set<String> getRelation(String userId, String playlistId);
}