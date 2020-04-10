package com.swp493.ivb.features.workspace;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.swp493.ivb.common.artist.RepositoryArtist;
import com.swp493.ivb.common.relationship.RepositoryUserRelease;
import com.swp493.ivb.common.relationship.RepositoryUserTrack;
import com.swp493.ivb.common.release.DTOReleaseInfoUpload;
import com.swp493.ivb.common.release.DTOReleaseStatistic;
import com.swp493.ivb.common.release.DTOReleaseUpdate;
import com.swp493.ivb.common.release.EntityRelease;
import com.swp493.ivb.common.release.RepositoryRelease;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.track.DTOTrackStatistic;
import com.swp493.ivb.common.track.DTOTrackUpdate;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.common.track.RepositoryTrack;
import com.swp493.ivb.common.track.ServiceTrack;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.view.Paging;

import org.modelmapper.ModelMapper;
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
    RepositoryArtistStats artistStatsRepo;

    @Autowired
    RepositoryRelease releaseRepo;

    @Autowired
    RepositoryTrack trackRepo;

    @Autowired
    RepositoryUser userRepo;

    @Autowired
    RepositoryArtist artistRepo;

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
        Date date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        switch (type) {
            case "release":
                EntityRelease release = releaseRepo.getOne(id);
                release.setStreamCount(release.getStreamCount() + 1);
                break;
            case "track":
                EntityTrack track = trackRepo.getOne(id);
                EntityUser artist = userTrackRepo.findByTrackIdAndAction(id, "own").getUser();
                track.setStreamCount(track.getStreamCount() + 1);
                Optional<EntityPlayRecord> opUserPlayArtist = playRecordRepository.findByUserIdAndObjectIdAndTimestampAfter(userId,
                        artist.getId(),date);
                EntityPlayRecord userPlayArtist = opUserPlayArtist.map(upa -> {
                    upa.setCount(upa.getCount() + 1);
                    upa.setTimestamp(new Date());
                    return upa;
                }).orElse(newUserRecord(userId, "artist", artist.getId()));
                playRecordRepository.save(userPlayArtist);
                break;
            case "playlist":
                break;
            default:
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Optional<EntityPlayRecord> opUserPlay = playRecordRepository.findByUserIdAndObjectIdAndTimestampAfter(userId, id, date);
        EntityPlayRecord userPlay = opUserPlay.map(up -> {
            up.setCount(up.getCount() + 1);
            up.setTimestamp(new Date());
            return up;
        }).orElse(newUserRecord(userId, type, id));

        playRecordRepository.save(userPlay);
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
        if (user.getArtistStatus().equals("open")) {
            if (StringUtils.hasText(biography))
                userRepo.insertBiography(biography, userId);
            Optional<String> releaseId = releaseService.uploadRelease(userId, info, thumbnail, audioFiles);
            if (releaseId.isPresent()) {
                user.setArtistStatus("pending");
                userRepo.save(user);
            }
            return releaseId;
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid artist status");
    }

    @Override
    public boolean updateRelease(DTOReleaseUpdate data, String userId, String releaseId) {
        return releaseService.updateRelease(data, userId, releaseId);
    }

    @Override
    public String deleteRelease(String userId, String releaseId) {
        if (!userReleaseRepo.existsByUserIdAndReleaseIdAndAction(userId, releaseId, "own"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (userReleaseRepo.countByUserIdAndReleaseNotNullAndAction(userId, "own") <= 1)
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

    @Override
    public List<Long> yearStats(String userId, int year) {
        List<Long> counts = new ArrayList<>();  
        for(int i=1; i<13; i++){
            LocalDate date = LocalDate.of(year, i, 1);
            Date start = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(date.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            counts.add(playRecordRepository.getCountBetween(userId, start, end)); 
        }

        return counts;
    }

    @Override
    public Paging<DTOReleaseStatistic> releaseStats(String userId, int month, int year, int offset, int limit) {
        int total = userReleaseRepo.countByUserIdAndReleaseNotNullAndAction(userId, "own");
        Paging<DTOReleaseStatistic> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        Date date = Date.from(LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Map<String,Object>> list = artistStatsRepo.getReleaseIdAndCount(userId, date, paging.asPageable());
        ModelMapper mapper = new ModelMapper();
        List<DTOReleaseStatistic> items = list.stream().map(m ->{
            String id = (String) m.get("object_id");
            int count = ((BigInteger) m.get("count")).intValueExact();
            DTOReleaseStatistic rs = mapper.map(releaseService.getReleaseSimple(id, userId), DTOReleaseStatistic.class);
            rs.setStreamCountPerMonth(count);
            return rs;
        }).collect(Collectors.toList());
        
        paging.setItems(items);
        return paging;
    }

    @Override
    public Paging<DTOTrackStatistic> trackStats(String userId, int month, int year, int offset, int limit) {
        int total = userTrackRepo.countByUserIdAndTrackNotNullAndAction(userId, "own");
        Paging<DTOTrackStatistic> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        Date date = Date.from(LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Map<String, Object>> list = artistStatsRepo.getTrackIdAndCount(userId, date, paging.asPageable());
        ModelMapper mapper = new ModelMapper();
        List<DTOTrackStatistic> items = list.stream().map(m ->{
            String id = (String) m.get("object_id");
            int count = ((BigInteger) m.get("count")).intValueExact();
            DTOTrackStatistic rs = mapper.map(trackService.getTrackSimple(id, userId), DTOTrackStatistic.class);
            rs.setStreamCountPerMonth(count);
            return rs;
        }).collect(Collectors.toList());
        paging.setItems(items);
        return paging;
    }

    @Override
    public List<Long> streamStatsYear(int start, int end) {
        if(start > end) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        List<Long> list = new ArrayList<>();
        for(;start<=end;start++){
            list.add(artistStatsRepo.getSumYearStream(start));
        }
        return list;
    }

    @Override
    public List<Long> streamStatsMonth(int year) {
        LocalDate month = LocalDate.of(year, 1, 1);
        List<Long> list = new ArrayList<>();
        for(int i =1 ;i< 13;i++){
            Date date = Date.from(month.atStartOfDay(ZoneId.systemDefault()).toInstant());
            list.add(artistStatsRepo.getSumMonthStream(date));
            month = month.plusMonths(1);
        }
        return list;
    }

}
