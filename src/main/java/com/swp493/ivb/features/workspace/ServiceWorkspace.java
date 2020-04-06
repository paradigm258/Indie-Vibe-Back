package com.swp493.ivb.features.workspace;

import java.util.Optional;

import com.swp493.ivb.common.release.DTOReleaseInfoUpload;
import com.swp493.ivb.common.release.DTOReleaseUpdate;
import com.swp493.ivb.common.track.DTOTrackUpdate;

import org.springframework.web.multipart.MultipartFile;

public interface ServiceWorkspace {
    public void updateCount(String userId, String type, String id);
    public Optional<String> requestBecomeArtirst(String userId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
    MultipartFile[] audioFiles, String biography);
    public boolean updateRelease(DTOReleaseUpdate data, String userId, String releaseId);
    public String deleteRelease(String userId, String releaseId);
    public boolean actionRelease(String userId, String releaseId, String action);
    public String deleteTrack(String userId, String trackId);
    public String updateTrack(String userId, String trackId, DTOTrackUpdate data);
}
