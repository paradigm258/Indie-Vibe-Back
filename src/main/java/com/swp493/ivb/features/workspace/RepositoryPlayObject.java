package com.swp493.ivb.features.workspace;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryPlayObject extends JpaRepository<EntityPlayRecord,String>{

    public Optional<EntityPlayRecord> findByUserIdAndObjectId(String userId, String objectId);
    public List<ITypeAndId> findByUserId(String userId, Pageable pageable);
}