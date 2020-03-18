package com.swp493.ivb.common.playlist;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositoryPlaylist extends JpaRepository<EntityPlaylist,String> {
    boolean existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(String playlistId, String userId, String action);
    List<EntityPlaylist> findByUserPlaylistsUserId(String userId);
    List<EntityPlaylist> findByStatusAndUserPlaylistsUserId(String status, String userId);
    boolean existsByIdAndUserPlaylistsUserId(String playlistId, String userId);
    boolean existsByIdAndStatus(String playlistId, String status);
    @Query(value = "SELECT track_id from playlist_track where playlist_id = :playlistId",nativeQuery = true)
    List<String> findAllTrackIdById(@Param("playlistId")String playlistId);
}
