package com.swp493.ivb.common.release;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mpatric.mp3agic.Mp3File;
import com.swp493.ivb.common.artist.DTOArtistSimple;
import com.swp493.ivb.common.artist.EntityArtist;
import com.swp493.ivb.common.artist.RepositoryArtist;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.common.relationship.EntityUserRelease;
import com.swp493.ivb.common.relationship.EntityUserTrack;
import com.swp493.ivb.common.relationship.RepositoryUserRelease;
import com.swp493.ivb.common.relationship.RepositoryUserTrack;
import com.swp493.ivb.common.track.DTOTrackSimple;
import com.swp493.ivb.common.track.DTOTrackSimpleWithLink;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.common.track.RepositoryTrack;
import com.swp493.ivb.common.track.ServiceTrack;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.IOnlyId;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.config.AWSConfig;
import com.swp493.ivb.util.PopularTracking;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServiceReleaseImpl implements ServiceRelease {

    private static Logger log = LoggerFactory.getLogger(ServiceReleaseImpl.class);

    @Autowired
    private RepositoryRelease releaseRepo;

    @Autowired
    private RepositoryMasterData masterDataRepo;

    @Autowired
    private RepositoryTrack trackRepo;

    @Autowired
    private RepositoryArtist artistRepo;

    @Autowired
    private RepositoryUser userRepo;

    @Autowired
    private RepositoryUserRelease userReleaseRepo;

    @Autowired
    private RepositoryUserTrack userTrackRepo;

    @Autowired
    private ServiceArtist artistService;

    @Autowired
    private ServiceTrack trackService;
    
    @Autowired
    private PopularTracking popularTracking;

    @Autowired
    private AmazonS3 s3;

    @Override
    public Optional<String> uploadRelease(String artistId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
            MultipartFile[] audioFiles) {

        List<DTOTrackReleaseUpload> tracksInfo = info.getTracks();
        List<String> uploadKeyList = new LinkedList<>();
        File file = null;
        String releaseId = "";
        try {
            EntityRelease release = new EntityRelease();
            release.setTitle(info.getTitle());
            release.setDate(new Timestamp(new Date().getTime()));
            release.setStatus("public");
            release.setThumbnail("N/A");
            release.setReleaseType(masterDataRepo.findByIdAndType(info.getTypeId(), "release").get());

            Optional<EntityArtist> oArtist = artistRepo.findById(artistId);
            EntityUser artist;
            if(oArtist.isPresent()){
                artist = oArtist.get();
            }else{
                artist = userRepo.findById(artistId).get();
                if(!artist.getArtistStatus().equals("open")){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid artist status");
                }
            }
                
            // insert for track and release
            Set<EntityMasterData> releaseGenres = new HashSet<>();

            // map list of dto to list of entity
            List<EntityTrack> tracks = new LinkedList<>();
            for (DTOTrackReleaseUpload trackInfo : tracksInfo) {
                EntityTrack track = new EntityTrack();
                track.setTitle(trackInfo.getTitle());

                track.setStatus("public");
                track.setProducer(trackInfo.getProducer());
                track.setRelease(release);

                track.setGenres(Arrays.stream(trackInfo.getGenres()).map(genreId -> {
                    EntityMasterData genre = masterDataRepo.findByIdAndType(genreId, "genre").get();
                    releaseGenres.add(genre);
                    return genre;
                }).collect(Collectors.toSet()));

                track.setDuration(0L);
                track.setFileSize128(0L);
                track.setFileSize320(0L);
                track.setMp3128("mp3128");
                track.setMp3320("mp3320");

                EntityUserTrack artistTrack = new EntityUserTrack();
                artistTrack.setUser(artist);
                artistTrack.setTrack(track);
                artistTrack.setAction("own");
                track.getTrackUsers().add(artistTrack);

                tracks.add(track);
            }

            release.setTracks(tracks);
            release.setGenres(releaseGenres);

            // insert into user_object for release and user
            EntityUserRelease artistRelease = new EntityUserRelease();
            artistRelease.setUser(artist);
            artistRelease.setRelease(release);
            artistRelease.setAction("own");
            release.getReleaseUsers().add(artistRelease);

            release = releaseRepo.save(release);
            tracks = release.getTracks();
            releaseId = release.getId();

            // upload tracks and thumbnails
            if (thumbnail != null) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(thumbnail.getSize());
                String key = release.getId();
                s3.putObject(new PutObjectRequest(AWSConfig.BUCKET_NAME, key, thumbnail.getInputStream(), metadata)
                       .withCannedAcl(CannedAccessControlList.PublicRead));
                uploadKeyList.add(key);
                release.setThumbnail(AWSConfig.BUCKET_URL + key);
            }

            for (int i = 0; i < tracks.size(); i++) {
                EntityTrack track = tracks.get(i);
                MultipartFile trackContent128 = audioFiles[i * 2];
                MultipartFile trackContent320 = audioFiles[i * 2 + 1];
                track.setFileSize128(trackContent128.getSize());
                track.setFileSize320(trackContent320.getSize());

                file = File.createTempFile(release.getId(), null);

                writeInputToOutput(trackContent128.getInputStream(), new FileOutputStream(file));
                Mp3File mp3128 = new Mp3File(file);
                track.setDuration(mp3128.getLengthInMilliseconds());
                ObjectMetadata metadata128 = new ObjectMetadata();
                metadata128.setContentLength(track.getFileSize128());
                String key = track.getId() + "/128";
                s3.putObject(AWSConfig.BUCKET_NAME, key, new FileInputStream(file), metadata128);
                uploadKeyList.add(key);
                track.setMp3128(key);

                writeInputToOutput(trackContent320.getInputStream(), new FileOutputStream(file));
                Mp3File mp3320 = new Mp3File(file);
                track.setDuration(mp3320.getLengthInMilliseconds());

                ObjectMetadata metadata320 = new ObjectMetadata();
                metadata320.setContentLength(track.getFileSize320());
                key = track.getId() + "/320";
                s3.putObject(AWSConfig.BUCKET_NAME, key, new FileInputStream(file), metadata320);
                uploadKeyList.add(key);
                track.setMp3320(key);
            }

            release.setTracks(tracks);
            releaseRepo.save(release);

            return Optional.of(release.getId());
        }catch(NoSuchElementException e){
            throw e;
        }catch (Exception e) {
            log.error("Track upload failed", e);
            if (!releaseId.isEmpty()) {
                releaseRepo.deleteById(releaseId);
            }
            deleteCancel(uploadKeyList);
            return Optional.empty();
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    @Override
    public String deleteRelease(String releaseId, String artistId) {
        EntityRelease release = releaseRepo.findById(releaseId).get();
        if (!release.getArtist().getId().equals(artistId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        releaseRepo.delete(release);
        try {
            s3.deleteObject(AWSConfig.BUCKET_NAME, release.getId());
        } catch (SdkClientException e) {
            log.error("Failed to delete: " + release.getTitle(), e);
        }
        release.getTracks().stream().forEach(track -> {
            try {
                s3.deleteObject(AWSConfig.BUCKET_NAME, track.getId() + "/128");
            } catch (SdkClientException e) {
                log.error("Failed to delete: " + track.getTitle(), e);
            }
            try {
                s3.deleteObject(AWSConfig.BUCKET_NAME, track.getId() + "/320");
            } catch (SdkClientException e) {
                log.error("Failed to delete: " + track.getTitle(), e);
            }
        });
        return releaseId;
    }  

    private void deleteCancel(List<String> keyList) {
        for (String keyToDelete : keyList) {
            try {
                s3.deleteObject(AWSConfig.BUCKET_NAME, keyToDelete);
            } catch (SdkClientException e) {
                log.error("Failed to delete: " + keyToDelete, e);
            }
        }
    }

    private void writeInputToOutput(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
        in.close();
        out.close();
    }

    @Override
    public List<String> streamRelease(String releaseId, String userId) {
        if(!hasReleaseAccessPermission(releaseId, userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return trackRepo.findAllByReleaseId(releaseId).stream()
                    .map(t -> t.getId())
                    .collect(Collectors.toList()
                );
    }

    private boolean hasReleaseAccessPermission(String releaseId, String userId){
        return  releaseRepo.existsByIdAndStatus(releaseId, "public")
            ||  userReleaseRepo.existsByUserIdAndReleaseIdAndAction(userId, releaseId, "own");
    }

    @Override
    public Optional<DTOReleaseFull> getReleaseFull(String releaseId, String userId, int offset, int limit) {
        ModelMapper mapper = new ModelMapper();
        Optional<EntityRelease> release = releaseRepo.findById(releaseId);
        return release.map(r -> {
            DTOReleaseFull releaseFull = mapper.map(r, DTOReleaseFull.class);
            
            DTOArtistSimple artistSimple =  artistService.getArtistSimple(userId, r.getArtist().getId());
                    
            releaseFull.setArtist(artistSimple);
            releaseFull.setRelation(userReleaseRepo.getRelation(userId, releaseId));
            releaseFull.setArtist(artistService.getArtistSimple(userId, r.getArtist().getId()));

            Paging<DTOTrackSimple> paging = new Paging<>();
            paging.setPageInfo(trackRepo.countByReleaseId(releaseId), limit, offset);
            Pageable pageable = paging.asPageable();
            paging.setItems(trackRepo.findAllByReleaseId(releaseId, pageable).stream()
            .map(t ->{
                DTOTrackSimple trackSimple;
                if(trackService.hasTrackAccessPermission(t.getId(), userId)){
                    trackSimple = mapper.map(t,DTOTrackSimple.class);
                    trackSimple.setArtists(t.getArtist().stream().map(at->artistService.getArtistSimple(userId, at.getUser().getId())).collect(Collectors.toSet()));
                    trackSimple.setRelation(userTrackRepo.getRelation(userId, t.getId()));
                }else{
                    trackSimple = new DTOTrackSimple();
                }
                return trackSimple;
            }).collect(Collectors.toList()));
            releaseFull.setTracks(paging);
            return releaseFull;
        });
    }

    @Override
    public boolean actionRelease(String releaseId, String userId, String action) {
        EntityRelease release = releaseRepo.findById(releaseId).get();
        EntityUser user = userRepo.findById(userId).get();

        if(!hasReleaseAccessPermission(releaseId, userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        boolean success = false;
        switch (action) {
            case "favorite":
                success = user.favoriteRelease(release);
                break;
            case "unfavorite":
                success = user.unfavoriteRelease(release);
                break;
            case "make-public":
                if(userReleaseRepo.existsByUserIdAndReleaseIdAndAction(userId, releaseId, "own")){
                    release.setStatus("public");
                    success = true;
                } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                break;
            case "make-private":
                if(userReleaseRepo.existsByUserIdAndReleaseIdAndAction(userId, releaseId, "own")){
                    release.setStatus("private");
                    success = true;
                } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                break;
            default:
                break;
        }
        if(success){
            releaseRepo.flush();
        }
        return success;
    }

    public DTOReleaseSimple getReleaseSimple(String releaseId, String userId){
        if(!hasReleaseAccessPermission(releaseId, userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return getReleaseSimple(releaseRepo.findById(releaseId).get(), userId);
    }

    public DTOReleaseSimple getReleaseSimple(EntityRelease release, String userId){
        ModelMapper mapper = new ModelMapper();
        DTOReleaseSimple releaseSimple = mapper.map(release, DTOReleaseSimple.class);

        releaseSimple.setArtist(artistService.getArtistSimple(userId, release.getArtist().getId()));
        releaseSimple.setRelation(userReleaseRepo.getRelation(userId, release.getId()));

        return releaseSimple;
    }

    @Override
    public Paging<DTOReleaseSimple> getReleases(String userId, String viewerId, int offset, int limit, String type) {
                             
        boolean privateView = userId.equals(viewerId);
        
        int total = privateView ? userReleaseRepo.countByUserIdAndReleaseNotNullAndAction(userId, type)
                                : userReleaseRepo.countByUserIdAndReleaseStatusAndAction(userId, "public", type);

        Paging<DTOReleaseSimple> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        Pageable pageable = paging.asPageable();

        List<EntityUserRelease> list = privateView  ? userReleaseRepo.findByUserIdAndReleaseNotNullAndAction(userId, type, pageable)
                                                    : userReleaseRepo.findByReleaseStatusAndUserIdAndAction("public", userId, type, pageable);
        
        paging.setItems(list.stream().map(ur -> getReleaseSimple(ur.getRelease(),viewerId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public Paging<DTOReleaseSimple> findRelease(String key, String userId, int offset, int limit) {
        int total = releaseRepo.countByTitleIgnoreCaseContainingAndStatus(key,"public");
        Paging<DTOReleaseSimple> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = releaseRepo.findByTitleIgnoreCaseContainingAndStatus(key,"public", paging.asPageable());
        paging.setItems(list.stream().map(t -> getReleaseSimple(t.getId(), userId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public Paging<DTOReleaseSimple> getReleaseGenre(String genreId, String userId, int offset, int limit) {
        Date date = Date.from(LocalDate.now().minusWeeks(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        int total = releaseRepo.countByGenresIdAndStatusAndDateAfter(genreId, "public", date);
        Paging<DTOReleaseSimple> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        Pageable pageable = PageRequest.of(paging.getOffset()/paging.getLimit(), paging.getLimit(), Direction.ASC, "date");
        List<IOnlyId> list = releaseRepo.findByGenresIdAndStatusAndDateAfter(genreId, "public", date, pageable);
        paging.setItems(list.stream().map(id -> getReleaseSimple(id.getId(), userId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public List<DTOReleaseSimple> getLastest(String userId) {
        List<String> list = popularTracking.getNewRelease();
        return list.stream().map(release -> getReleaseSimple(release, userId)).collect(Collectors.toList());
    }

    @Override
    public Paging<DTOReleaseSimple> getArtistReleaseByType(String artistId, String userId, String releaseType, int offset, int limit) {
        boolean privateView = userId.equals(artistId);
        
        int total = privateView ? releaseRepo.countByArtistReleaseUserIdAndReleaseTypeId(artistId, releaseType)
                                : releaseRepo.countByArtistReleaseUserIdAndReleaseTypeIdAndStatus(artistId, releaseType, "public");

        Paging<DTOReleaseSimple> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = privateView ? releaseRepo.findByArtistReleaseUserIdAndReleaseTypeId(artistId, releaseType, paging.asPageable())
                                         : releaseRepo.findByArtistReleaseUserIdAndReleaseTypeIdAndStatus(artistId, releaseType, "public", paging.asPageable());
        paging.setItems(list.stream().map(id ->getReleaseSimple(id.getId(), userId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public DTOReleasePending getPendingRelease(String userId, int offset, int limit) {
        ModelMapper mapper = new ModelMapper();
        String releaseId = userReleaseRepo.findFirstByUserIdAndReleaseNotNullAndAction(userId, "own").getRelease().getId();
        int total = trackRepo.countByReleaseId(releaseId);
        Paging<DTOTrackSimpleWithLink> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = trackRepo.findByReleaseId(releaseId, paging.asPageable());
        paging.setItems(list.stream().map(track -> trackService.getTrackSimpleWithLink(track.getId())).collect(Collectors.toList()));
        DTOReleasePending res = mapper.map(getReleaseSimple(releaseId, userId), DTOReleasePending.class);
        res.setTracks(paging);
        return res;
    }

    @Override
    public List<DTOReleaseSimple> getPopular(String userId) {
        List<String> list = popularTracking.getPopular();
        return list.stream().map(release -> getReleaseSimple(release, userId)).collect(Collectors.toList());
    }

    @Override
    public boolean updateRelease(DTOReleaseUpdate data, String userId, String releaseId) {
        if(!releaseRepo.existsByIdAndReleaseUsersUserIdAndReleaseUsersAction(releaseId, userId, "own"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        EntityRelease release = releaseRepo.getOne(releaseId);
        if(StringUtils.hasText(data.getTitle())){
            release.setTitle(data.getTitle());
        }
        if(StringUtils.hasText(data.getType())){
            EntityMasterData releaseType = masterDataRepo.findByIdAndType(data.getType(), "release").get();
            release.setReleaseType(releaseType);
        }
        MultipartFile thumbnail = data.getThumbnail();
        if(thumbnail != null){
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(thumbnail.getSize());
            String key = releaseId;
            try {
                s3.putObject(new PutObjectRequest(AWSConfig.BUCKET_NAME, key, thumbnail.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                release.setThumbnail(AWSConfig.BUCKET_URL+key);
            } catch (IOException e) {
                throw new RuntimeException("Error getting input stream for thumbnail",e);
            }
        }
        releaseRepo.save(release);
        return true;
    }

    
}
