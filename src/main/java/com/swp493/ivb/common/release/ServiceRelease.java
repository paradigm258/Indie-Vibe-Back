package com.swp493.ivb.common.release;

import java.util.List;
import java.util.Optional;

import com.swp493.ivb.common.view.Paging;

import org.springframework.web.multipart.MultipartFile;

public interface ServiceRelease {

    Optional<String> uploadRelease(String artistId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
            MultipartFile[] audioFiles);
    String deleteRelease(String releaseId, String artistId);
    List<String> streamRelease(String releaseId, String userId);
    Optional<DTOReleaseFull> getReleaseFull(String releaseId, String userId, int offset, int limit);
    DTOReleaseSimple getReleaseSimple(String releaseId, String userId);
    boolean actionRelease(String releaseId, String userId, String action);
    Paging<DTOReleaseSimple> getReleases(String userId, String viewerId, int offset, int limit, String type);
    Paging<DTOReleaseSimple> findRelease(String key, String userId, int offset, int limit);
    Paging<DTOReleaseSimple> getReleaseGenre(String genreId, String userId, int offset, int limit);
    List<DTOReleaseSimple> getLastest(String userId);
    void addTrackRelease(String userId, String releaseId, List<DTOTrackReleaseUpload> trackInfos, MultipartFile[] files);
    Paging<DTOReleaseSimple> getArtistReleaseByType(String artistId, String userId, String releaseType, int offset, int limit);
    DTOReleasePending getPendingRelease(String userId, int offset, int limit);
    boolean updateRelease(DTOReleaseUpdate data, String userId, String releaseId);
    List<DTOReleaseSimple> getPopular(String userId);
}
