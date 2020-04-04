package com.swp493.ivb.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.swp493.ivb.common.release.RepositoryRelease;
import com.swp493.ivb.common.user.IOnlyId;

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

    private List<String> popular = new ArrayList<>();
    private List<String> newRelease = new ArrayList<>();

    @Scheduled(fixedDelay = 60000)
    private void updatePopular(){
        Pageable pageable = PageRequest.of(0, 5, Direction.DESC, "streamCount");
        List<IOnlyId> list = releaseRepository.findByStatus("public",pageable);
        this.popular.clear();
        for (IOnlyId entityRelease : list) {
            popular.add(entityRelease.getId());
        }
    }

    @Scheduled(fixedDelay = 60000)
    private void updateNewRelease(){
        Date date = Date.from(LocalDate.now().minusWeeks(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Pageable pageable = PageRequest.of(0, 5, Direction.DESC, "date");
        List<IOnlyId> list = releaseRepository.findByStatusAndDateAfter("public", date, pageable);
        this.popular.clear();
        for (IOnlyId entityRelease : list) {
            newRelease.add(entityRelease.getId());
        }
    }

}