package com.swp493.ivb.common.report;

import java.util.Optional;

import javax.mail.MessagingException;

import com.swp493.ivb.common.view.Paging;

public interface ServiceReport {
    public DTOReport getReport (String id);
    public void reportArtist(String userId, String artistId, String type, String reason);
    public void reviewReport(String id, String action) throws MessagingException;
    public Paging<DTOReport> findReport(Optional<String> type, Optional<String> status, int offset, int limit);
}