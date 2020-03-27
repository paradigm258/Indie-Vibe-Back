package com.swp493.ivb.common.playlist;

import java.util.List;

import com.swp493.ivb.common.user.IOnlyId;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositoryPlaylist extends JpaRepository<EntityPlaylist,String> {
    boolean existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(String playlistId, String userId, String action);
    List<EntityPlaylist> findByUserPlaylistsUserId(String userId);
    List<EntityPlaylist> findByStatusAndUserPlaylistsUserId(String status, String userId);
    boolean existsByIdAndUserPlaylistsUserId(String playlistId, String userId);
    boolean existsByIdAndStatus(String playlistId, String status);
    List<IOnlyId> findByTitleIgnoreCaseContainingAndStatus(String key, String status, Pageable pageable);
    int countByTitleIgnoreCaseContainingAndStatus(String key, String status);
    int countByUserPlaylistsUserUserRoleIdAndGenresIdAndStatus(String role, String genreId, String status);
    List<IOnlyId> findByUserPlaylistsUserUserRoleIdAndGenresIdAndStatus(String role, String genreId, String status, Pageable pageable);
    @Query(value = "SELECT track_id from playlist_track where playlist_id = :playlistId",nativeQuery = true)
    List<String> findAllTrackIdById(@Param("playlistId")String playlistId);
}
