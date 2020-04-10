package com.swp493.ivb.features.cms;

import java.util.Optional;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.release.DTOReleasePending;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.features.report.DTOReport;

public interface ServiceCMS {
    public Paging<DTOArtistFull> getRequests(String adminId, int offset, int limit);
    public DTOReleasePending getArtistRequest(String artistId, int offset, int limit);
    public boolean responseRequest(String userId, String action);
    public Paging<DTOUserPublic> listUserProfiles(String key, int offset, int limit);
    public void makeCurator(String userId);
    public Paging<DTOReport> findReport(Optional<String> type, Optional<String> status, int offset, int limit);
    public void reviewReport(String id,String action);
}