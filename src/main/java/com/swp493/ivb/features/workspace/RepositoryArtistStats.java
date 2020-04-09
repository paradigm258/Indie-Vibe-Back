package com.swp493.ivb.features.workspace;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryArtistStats extends JpaRepository<EntityArtistStats, String>{

    List<IObjectIdCount> findByObjectIdInAndRecordMonth(List<String> ids, Date month, Pageable pageable);
    Optional<EntityArtistStats> findByObjectIdAndRecordMonth(String id, Date month);
}