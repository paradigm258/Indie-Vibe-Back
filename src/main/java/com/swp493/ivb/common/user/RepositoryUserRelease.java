package com.swp493.ivb.common.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swp493.ivb.common.release.EntityRelease;

@Repository
public interface RepositoryUserRelease extends JpaRepository<EntityUserRelease2, String> {

    Optional<EntityUserRelease2> findByReleaseAndAction(EntityRelease release, String action);
}
