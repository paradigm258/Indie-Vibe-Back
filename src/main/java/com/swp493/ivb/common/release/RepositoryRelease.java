package com.swp493.ivb.common.release;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryRelease extends JpaRepository<EntityRelease, String> {
    
}
