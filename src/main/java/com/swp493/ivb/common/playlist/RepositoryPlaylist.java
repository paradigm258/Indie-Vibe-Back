package com.swp493.ivb.common.playlist;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryPlaylist extends JpaRepository<EntityPlaylist,String> {
    boolean existsByIdAndUserPlaylistsUserIdAndUserPlaylistsAction(String playlistId, String userId, String action);
    List<EntityPlaylist> findByUserPlaylistsUserId(String userId);
    boolean existsByIdAndUserPlaylistsUserId(String playlistId, String userId);
}
