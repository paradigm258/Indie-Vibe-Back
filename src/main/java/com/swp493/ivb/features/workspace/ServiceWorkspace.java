package com.swp493.ivb.features.workspace;

import java.util.Optional;

import com.swp493.ivb.common.release.DTOReleaseInfoUpload;

import org.springframework.web.multipart.MultipartFile;

public interface ServiceWorkspace {
    public void updateCount(String userId, String type, String id);
    public Optional<String> requestBecomeArtirst(String userId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
    MultipartFile[] audioFiles, String biography);
}
