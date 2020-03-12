package com.swp493.ivb.common.user;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swp493.ivb.common.track.EntityTrack;

@Repository
public interface RepositoryUserTrack extends JpaRepository<EntityUserTrack2, String> {

    Set<EntityUserTrack2> findByTrackAndActionOrAction(EntityTrack track, String action, String action2);
}
