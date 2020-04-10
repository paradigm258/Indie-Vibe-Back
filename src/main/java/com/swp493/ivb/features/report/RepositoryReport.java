package com.swp493.ivb.features.report;

import java.util.List;

import com.swp493.ivb.common.mdata.EntityMasterData;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryReport extends JpaRepository<EntityReport, String> {

    public int countByType(EntityMasterData type);
    public List<EntityReport> findByType(EntityMasterData type, Pageable pageable);
    
}