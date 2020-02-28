package com.swp493.ivb.common.track;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryTrack extends JpaRepository<EntityTrack, String> {
    EntityTrack findTrackById(String id);
}
