package com.swp493.ivb.features.cms;

import java.util.List;
import java.util.Optional;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.release.DTOReleasePending;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.features.report.DTOReport;
import com.swp493.ivb.features.report.ServiceReport;
import com.swp493.ivb.features.workspace.ServiceWorkspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceCMSImpl implements ServiceCMS {

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

    

}