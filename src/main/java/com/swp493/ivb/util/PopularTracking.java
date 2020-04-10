package com.swp493.ivb.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.swp493.ivb.common.release.RepositoryRelease;
import com.swp493.ivb.common.user.IOnlyId;
import com.swp493.ivb.features.workspace.EntityArtistStats;
import com.swp493.ivb.features.workspace.EntityPlayRecord;
import com.swp493.ivb.features.workspace.RepositoryArtistStats;
import com.swp493.ivb.features.workspace.RepositoryPlayRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class PopularTracking {

    @Autowired
    RepositoryRelease releaseRepository;

    @Autowired
    RepositoryArtistStats artistStatsRepo;

    @Autowired
    RepositoryPlayRecord playRecordRepo;

    private List<String> popular = new ArrayList<>();
    private List<String> newRelease = new ArrayList<>();

    @Scheduled(fixedDelay = 60000)
    private void updatePopular() {
        Pageable pageable = PageRequest.of(0, 5, Direction.DESC, "streamCount");
        List<IOnlyId> list = releaseRepository.findByStatus("public", pageable);
        this.popular.clear();
        for (IOnlyId entityRelease : list) {
            popular.add(entityRelease.getId());
        }
    }

    @Scheduled(fixedDelay = 60000)
    private void updateNewRelease() {
        Date date = Date.from(LocalDate.now().minusWeeks(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Pageable pageable = PageRequest.of(0, 5, Direction.DESC, "date");
        List<IOnlyId> list = releaseRepository.findByStatusAndDateAfter("public", date, pageable);
        this.newRelease.clear();
        for (IOnlyId entityRelease : list) {
            newRelease.add(entityRelease.getId());
        }
    }

    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    private void updateArtistStats() {
        Date date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        Date month = Date.from(LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<EntityPlayRecord> list = playRecordRepo.findDistinctObjectIdByTimestampAfter(month);
        for (EntityPlayRecord entityPlayRecord : list) {
            String type = entityPlayRecord.getObjectType();
                    Optional<EntityArtistStats> artistStats = artistStatsRepo
                            .findByObjectIdAndRecordMonth(entityPlayRecord.getObjectId(), month);
            EntityArtistStats at = artistStats.map(a -> {
                a.setCount(playRecordRepo.getCountBetween(entityPlayRecord.getObjectId(), month, date));
                return a;
            }).orElse(newArtistStat(entityPlayRecord.getObjectId(), type, month, playRecordRepo.getCountBetween(entityPlayRecord.getObjectId(), month, date)));
            artistStatsRepo.save(at);
        }
    }

    private EntityArtistStats newArtistStat(String objectId, String type, Date month, long count){
        EntityArtistStats artistStats = new EntityArtistStats();
        artistStats.setCount(count);
        artistStats.setObjectId(objectId);
        artistStats.setType(type);
        artistStats.setRecordMonth(month);
        return artistStats;
    }

}