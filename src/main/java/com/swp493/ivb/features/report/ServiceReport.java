package com.swp493.ivb.features.report;

import java.util.Optional;

import com.swp493.ivb.common.view.Paging;

public interface ServiceReport {
    public DTOReport getReport (String id);
    public void reportArtist(String userId, String artistId, String type, String reason);
    public void reviewReport(String id, String action);
    public Paging<DTOReport> findReport(Optional<String> type, Optional<String> status, int offset, int limit);
}