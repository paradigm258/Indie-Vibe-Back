package com.swp493.ivb.common.mdata;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryMasterData extends JpaRepository<EntityMasterData, String> {

    List<EntityMasterData> findByType(String type);
    
    Optional<EntityMasterData> findByIdAndType(String id, String type);
}
