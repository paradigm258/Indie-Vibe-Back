package com.swp493.ivb.features.common.track;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TrackRepository extends JpaRepository<TrackEntity,String>{
    Page<TrackEntity> findTrackByGenre(String genre, Pageable pageable);
    TrackEntity findTrackById(String id);
}
