package com.swp493.ivb.features.cms;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.release.DTOReleasePending;
import com.swp493.ivb.common.report.DTOReport;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.view.Paging;

public interface ServiceCMS {
    public Paging<DTOArtistFull> getRequests(String adminId, int offset, int limit);
    public DTOReleasePending getArtistRequest(String artistId, int offset, int limit);
    public boolean responseRequest(String userId, String action);
    public Paging<DTOUserPublic> listUserProfiles(String key, int offset, int limit);
    public void makeCurator(String userId);
    public void unmakeCurator(String userId);
    public Paging<DTOReport> findReport(Optional<String> type, Optional<String> status, int offset, int limit);
    public void reviewReport(String id,String action);
    public List<Long> yearlySumStream(int start, int end);
    public List<Long> monthlySumStream(int year);
    public Map<String,Object> yearlySumRevenue(int start, int end);
    public Map<String,Object> monthlySumRevenue(int year);
    public void recordPurchase(Long amount, String type);
}