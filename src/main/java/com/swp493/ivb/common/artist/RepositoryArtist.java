package com.swp493.ivb.common.artist;

import java.util.List;

import com.swp493.ivb.common.user.IOnlyId;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryArtist extends JpaRepository<EntityArtist, String> {
    List<IOnlyId>findAllByFollowerUsersId(String userId, Pageable pageable );
    List<IOnlyId> findByDisplayNameIgnoreCaseContaining(String key, Pageable pageable);
    int countByDisplayNameIgnoreCaseContaining(String key);
    int countByFollowerUsersId(String userId);
}
