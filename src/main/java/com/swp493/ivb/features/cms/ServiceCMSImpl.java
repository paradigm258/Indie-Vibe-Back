package com.swp493.ivb.features.cms;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.common.release.DTOReleasePending;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.report.DTOReport;
import com.swp493.ivb.common.report.ServiceReport;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.features.workspace.ServiceWorkspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServiceCMSImpl implements ServiceCMS {

    @Autowired
    RepositoryRevenueRecord revenueRecordRepo;

    @Autowired
    ServiceArtist artistService;

    @Autowired
    ServiceUser userService;

    @Autowired
    ServiceRelease releaseService;

    @Autowired
    ServiceReport reportService;

    @Autowired
    ServiceWorkspace workspaceService;

    @Autowired
    RepositoryMasterData masterDataRepo;

    @Override
    public Paging<DTOArtistFull> getRequests(String adminId, int offset, int limit) {
        return artistService.getArtistsRequestProfile(adminId, offset, limit);
    }

    @Override
    public DTOReleasePending getArtistRequest(String artistId, int offset, int limit) {
        return releaseService.getPendingRelease(artistId, offset, limit);
    }

    @Override
    public boolean responseRequest(String userId, String action) {
        userService.updateArtist(userId, action);
        return true;
    }

    @Override
    public Paging<DTOUserPublic> listUserProfiles(String key, int offset, int limit) {
        return userService.listUserProfiles(key, offset, limit);
    }

    @Override
    public void makeCurator(String userId) {
        userService.makeCurator(userId);
    }

    @Override
    public Paging<DTOReport> findReport(Optional<String> type, Optional<String> status, int offset, int limit) {
        return reportService.findReport(type, status, offset, limit);
    }

    @Override
    public void reviewReport(String id, String action) {
        reportService.reviewReport(id, action);
    }

    @Override
    public List<Long> yearlySumStream(int start, int end) {
        return workspaceService.streamStatsYear(start, end);
    }

    @Override
    public List<Long> monthlySumStream(int year) {
        return workspaceService.streamStatsMonth(year);
    }

    @Override
    public Map<String, Object> yearlySumRevenue(int start, int end) {

        if (start > end)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Map<String,Object> res = new HashMap<>();
        List<Long> fixed = new ArrayList<>();
        List<Long> monthly = new ArrayList<>();
        for (; start <= end; start++) {
            fixed.add(revenueRecordRepo.getYearRevenue(start, "p-fixed"));
            monthly.add(revenueRecordRepo.getYearRevenue(start, "p-montly"));
        }
        res.put("fixed", fixed);
        res.put("montly", monthly);
        return res;
    }

    @Override
    public Map<String, Object> monthlySumRevenue(int year) {
        Map<String,Object> res = new HashMap<>();
        List<Long> fixed = new ArrayList<>();
        List<Long> monthly = new ArrayList<>();
        for(int i=1;i<13;i++){
            fixed.add(revenueRecordRepo.getMonthRevenue(year, i, "p-fixed"));
            monthly.add(revenueRecordRepo.getMonthRevenue(year, i, "p-monthly"));
        }
        res.put("fixed", fixed);
        res.put("montly", monthly);
        return res;
    }

    @Override
    public void recordPurchase(Long amount, String type) {
        Date month = Date.from(LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        EntityRevenueRecord record = new EntityRevenueRecord();
        record.setAmount(amount);
        record.setPreminumType(masterDataRepo.findByIdAndType(type, "plan").get());
        record.setRecordedMonth(month);
        revenueRecordRepo.save(record);
    }

    

}