package com.swp493.ivb.common.release;

import java.util.List;
import java.util.Optional;

import com.swp493.ivb.common.view.Paging;

import org.springframework.web.multipart.MultipartFile;

public interface ServiceRelease {

    Optional<String> uploadRelease(String artistId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
            MultipartFile[] audioFiles);

    Optional<String> deleteRelease(String releaseId, String artistId);

    Optional<List<DTOReleaseSimple>> getOwnRelease(String userId);

    Optional<DTOReleaseSimple> getSimpleRelease(String releaseId, String userId);

    List<String> streamRelease(String releaseId, String userId);

    Optional<DTOReleaseFull> getReleaseFull(String releaseId, String userId, int offset, int limit);

    boolean actionRelease(String releaseId, String userId, String action);

    Paging<DTOReleaseSimple> getReleases(String userId, String viewerId, int offset, int limit, String type);

    Paging<DTOReleaseSimple> findRelease(String key, String userId, int offset, int limit);
}
