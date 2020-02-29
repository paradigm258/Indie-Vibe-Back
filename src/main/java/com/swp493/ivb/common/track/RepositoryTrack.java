package com.swp493.ivb.common.track;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryTrack extends JpaRepository<EntityTrack, String> {

    Optional<EntityTrack> findById(String id);

    EntityTrack findTrackById(String id);
}
