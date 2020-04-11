package com.swp493.ivb.features.workspace;

import java.util.List;
import java.util.Optional;

import com.swp493.ivb.common.release.DTOReleaseInfoUpload;
import com.swp493.ivb.common.release.DTOReleaseStatistic;
import com.swp493.ivb.common.release.DTOReleaseUpdate;
import com.swp493.ivb.common.release.DTOTrackReleaseUpload;
import com.swp493.ivb.common.track.DTOTrackStatistic;
import com.swp493.ivb.common.track.DTOTrackUpdate;
import com.swp493.ivb.common.view.Paging;

import org.springframework.web.multipart.MultipartFile;

public interface ServiceWorkspace {
    public void updateCount(String userId, String type, String id);
    public Optional<String> requestBecomeArtirst(String userId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
    MultipartFile[] audioFiles, String biography);
    public boolean updateRelease(DTOReleaseUpdate data, String userId, String releaseId);
    public void addToRelease(String userId, String releaseId, List<DTOTrackReleaseUpload> trackInfos, MultipartFile[] files);
    public String deleteRelease(String userId, String releaseId);
    public boolean actionRelease(String userId, String releaseId, String action);
    public String deleteTrack(String userId, String trackId);
    public String updateTrack(String userId, String trackId, DTOTrackUpdate data);
    public List<Long> yearStats(String userId, int year);
    public Paging<DTOReleaseStatistic> releaseStats(String userId, int month, int year, int offset, int limit);
    public Paging<DTOTrackStatistic> trackStats(String userId, int month, int year, int offset, int limit);
    public List<Long> streamStatsYear(int start, int end);
    public List<Long> streamStatsMonth(int year);
}
