package com.swp493.ivb.features.workspace;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryArtistStats extends JpaRepository<EntityArtistStats, String>{

    List<IObjectIdCount> findByObjectIdInAndRecordMonth(List<String> ids, Date month, Pageable pageable);
    Optional<EntityArtistStats> findByObjectIdAndRecordMonth(String id, Date month);
    List<EntityArtistStats> findByObjectId(String id);
    @Query(value =  "select release_id as object_id, ifnull(count,0) as count from "+
                    "(select object_id, count from artist_stats "+
                        "where record_month = :month) as a "+
                    "right join (select release_id from user_object "+ 
                        "where user_id = :userId and action = 'own' and release_id is not null "+
                    ") as b on a.object_id = b.release_id",
                nativeQuery = true)
    List<Map<String,Object>> getReleaseIdAndCount(String userId, Date month, Pageable pageable);
    
    @Query(value =  "select track_id as object_id, ifnull(count,0) as count from "+
                    "(select object_id, count from artist_stats "+
                        "where record_month = :month) as a "+
                    "right join (select track_id from user_object "+ 
                        "where user_id = :userId and action = 'own' and track_id is not null "+
                    ") as b on a.object_id = b.track_id",
                nativeQuery = true)
    List<Map<String,Object>> getTrackIdAndCount(String userId, Date month, Pageable pageable);
    
    @Query(value = "select ifnull(sum(count),0) from artist_stats where type='artist' and year(record_month) = :year",nativeQuery = true)
    long getSumYearStream(int year);
    
    @Query(value = "select ifnull(sum(count),0) from artist_stats where type='artist' and record_month = :month", nativeQuery = true)
    long getSumMonthStream(Date month);
}