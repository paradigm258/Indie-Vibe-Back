package com.swp493.ivb.common.release;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface ServiceRelease {

    Optional<String> uploadRelease(String artistId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
            MultipartFile[] audioFiles);

    Optional<String> deleteRelease(String releaseId);
}
