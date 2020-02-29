package com.swp493.ivb.common.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryArtist extends JpaRepository<EntityArtist, String> {

}
