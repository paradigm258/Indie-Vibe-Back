package com.swp493.ivb.features.workspace;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryPlayRecord extends JpaRepository<EntityPlayRecord,String>{

    public Optional<EntityPlayRecord> findByUserIdAndObjectIdAndTimestampAfter(String userId, String objectId, Date date);
    public List<EntityPlayRecord> findDistinctObjectIdByTimestampAfter(Date date);
    public List<EntityPlayRecord> findByObjectId(String objectId);
    public List<ITypeAndId> findDistinctObjectIdByUserIdAndObjectTypeNot(String userId, String objectType, Pageable pageable);
    @Query(value = "select COALESCE(SUM(count),0) from play_record where object_id = ?1 and timestamp between ?2 and ?3",nativeQuery = true)
    public Long getCountBetween(String id, Date start, Date end);
}