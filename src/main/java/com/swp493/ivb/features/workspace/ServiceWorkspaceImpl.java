package com.swp493.ivb.features.workspace;

import java.util.Date;
import java.util.Optional;

import com.swp493.ivb.common.release.DTOReleaseInfoUpload;
import com.swp493.ivb.common.release.EntityRelease;
import com.swp493.ivb.common.release.RepositoryRelease;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.RepositoryUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServiceWorkspaceImpl implements ServiceWorkspace {

    @Autowired
    RepositoryPlayObject playObjectRepository;

    @Autowired
    RepositoryRelease releaseRepo;

    @Autowired
    RepositoryUser userRepo;

    @Autowired
    ServiceRelease releaseService;

    @Override
    public void updateCount(String userId, String type, String id) {
        switch (type) {
            case "release":
                EntityRelease release = releaseRepo.getOne(id);
                release.setStreamCount(release.getStreamCount() + 1);
                break;

            default:
                break;
        }
        Optional<EntityPlayRecord> opUserPlay = playObjectRepository.findByUserIdAndObjectId(userId, id);
        EntityPlayRecord userPlay = opUserPlay.map(up -> {
            up.setCount(up.getCount() + 1);
            up.setTimestamp(new Date());
            return up;
        }).orElse(newUserRecord(userId, type, id));

        playObjectRepository.save(userPlay);
    }

    EntityPlayRecord newUserRecord(String userId, String type, String id) {
        EntityPlayRecord record = new EntityPlayRecord();
        EntityUser user = new EntityUser();
        user.setId(userId);
        record.setUser(user);
        record.setCount(1);
        record.setObjectId(id);
        record.setObjectType(type);
        record.setRecordType("user");
        record.setTimestamp(new Date());
        return record;
    }

    @Override
    public Optional<String> requestBecomeArtirst(String userId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
            MultipartFile[] audioFiles, String biography) {
        EntityUser user = userRepo.findById(userId).get();
        if(user.getArtistStatus().equals("open")){
            if(StringUtils.hasText(biography)) userRepo.insertBiography(biography, userId);
            return releaseService.uploadRelease(userId, info, thumbnail, audioFiles);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid artist status");
    }

}
