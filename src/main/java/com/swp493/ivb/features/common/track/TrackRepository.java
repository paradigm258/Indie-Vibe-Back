package com.swp493.ivb.features.common.track;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, String> {
    TrackEntity findTrackById(String id);
}
