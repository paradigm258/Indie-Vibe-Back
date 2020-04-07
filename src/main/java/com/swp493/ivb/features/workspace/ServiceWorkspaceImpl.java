package com.swp493.ivb.features.workspace;

import java.util.Date;
import java.util.Optional;

import com.swp493.ivb.common.relationship.RepositoryUserRelease;
import com.swp493.ivb.common.relationship.RepositoryUserTrack;
import com.swp493.ivb.common.release.DTOReleaseInfoUpload;
import com.swp493.ivb.common.release.DTOReleaseUpdate;
import com.swp493.ivb.common.release.EntityRelease;
import com.swp493.ivb.common.release.RepositoryRelease;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.track.DTOTrackUpdate;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.common.track.RepositoryTrack;
import com.swp493.ivb.common.track.ServiceTrack;
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
    RepositoryPlayRecord playRecordRepository;

    @Autowired
    RepositoryRelease releaseRepo;

    @Autowired
    RepositoryTrack trackRepo;

    @Autowired
    RepositoryUser userRepo;

    @Autowired
    RepositoryUserRelease userReleaseRepo;

    @Autowired
    RepositoryUserTrack userTrackRepo;

    @Autowired
    ServiceRelease releaseService;

    @Autowired
    ServiceTrack trackService;

    @Override
    public void updateCount(String userId, String type, String id) {
        EntityUser artist = null;
        switch (type) {
            case "release":
                EntityRelease release = releaseRepo.getOne(id);
                artist = release.getArtist();
                release.setStreamCount(release.getStreamCount() + 1);
                break;
            case "track":
                EntityTrack track = trackRepo.getOne(id);
                artist = userTrackRepo.findByTrackIdAndAction(track.getId(), "own").getUser();
                track.setStreamCount(track.getStreamCount() +1);
            default:
                break;
        }
        Optional<EntityPlayRecord> opUserPlay = playRecordRepository.findByUserIdAndObjectId(userId, id);
        EntityPlayRecord userPlay = opUserPlay.map(up -> {
            up.setCount(up.getCount() + 1);
            up.setTimestamp(new Date());
            return up;
        }).orElse(newUserRecord(userId, type, id));

        Optional<EntityPlayRecord> opUserPlayArtist = playRecordRepository.findByUserIdAndObjectId(userId, artist.getId());
        EntityPlayRecord userPlayArtist = opUserPlayArtist.map(upa ->{
            upa.setCount(upa.getCount()+1);
            upa.setTimestamp(new Date());
            return upa;
        }).orElse(newUserRecord(userId, "artist", artist.getId()));

        playRecordRepository.save(userPlayArtist);
        playRecordRepository.saveAndFlush(userPlay);
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
            Optional<String> releaseId = releaseService.uploadRelease(userId, info, thumbnail, audioFiles);
            if (releaseId.isPresent()) {
                user.setArtistStatus("pending");
                userRepo.save(user); 
            }
            return releaseId;
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid artist status");
    }

    @Override
    public boolean updateRelease(DTOReleaseUpdate data, String userId, String releaseId) {
        return releaseService.updateRelease(data, userId, releaseId);
    }

    @Override
    public String deleteRelease(String userId, String releaseId) {
        if(!userReleaseRepo.existsByUserIdAndReleaseIdAndAction(userId, releaseId, "own"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if(userReleaseRepo.countByUserIdAndReleaseNotNullAndAction(userId, "own")<=1) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't delete your last record");
        return releaseService.deleteRelease(releaseId, userId);
    }

    @Override
    public boolean actionRelease(String userId, String releaseId, String action) {
        return releaseService.actionRelease(releaseId, userId, action);
    }

    @Override
    public String deleteTrack(String userId, String trackId) {
        return trackService.deleteTrack(userId, trackId);
    }

    @Override
    public String updateTrack(String userId, String trackId, DTOTrackUpdate data) {
        return trackService.updateTrack(userId, trackId, data);
    }

}
