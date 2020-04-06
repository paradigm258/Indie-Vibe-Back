package com.swp493.ivb.common.track;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.common.relationship.EntityUserRelease;
import com.swp493.ivb.common.relationship.EntityUserTrack;
import com.swp493.ivb.common.relationship.RepositoryUserTrack;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.common.release.EntityRelease;
import com.swp493.ivb.common.release.RepositoryRelease;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.IOnlyId;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.config.AWSConfig;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServiceTrackImpl implements ServiceTrack {

    private static Logger log = LoggerFactory.getLogger(ServiceTrackImpl.class);

    @Autowired
    private RepositoryTrack trackRepo;

    @Autowired
    private RepositoryMasterData masterDataRepo;

    @Autowired
    private RepositoryUser userRepo;

    @Autowired
    private RepositoryRelease releaseRepo;

    @Autowired
    private RepositoryUserTrack userTrackRepo;

    @Autowired
    private ServiceArtist artistService;

    @Autowired
    private AmazonS3 s3;

    @Override
    public DTOTrackStreamInfo getTrackStreamInfo(String id, int bitrate, String userId) {
        EntityTrack track = trackRepo.findById(id).get();
        String key = null;
        switch (bitrate) {
            case 320:
                key = track.getMp3320();
                break;
            case 128:
                key = track.getMp3128();
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid bitrate");
        }
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        DTOTrackFull info = getTrackFullFromEntity(track, userId);
        DTOTrackStreamInfo trackStreamInfo = mapper.map(track, DTOTrackStreamInfo.class);
        trackStreamInfo.setInfo(info);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(AWSConfig.BUCKET_NAME,
                key).withMethod(HttpMethod.GET).withExpiration(AWSConfig.presignExpiration());
        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
        trackStreamInfo.setUrl(url.toString());
        return trackStreamInfo;
    }

    @Override
    public boolean favoriteTrack(String userId, String trackId) {
        return actionTrack(userId, trackId, "favorite");
    }

    @Override
    public boolean unfavoriteTrack(String userId, String trackId) {
        return actionTrack(userId, trackId, "unfavorite");
    }

    private boolean actionTrack(String userId, String trackId, String action) {
        EntityUser user = userRepo.findById(userId).get();
        EntityTrack track = trackRepo.findById(trackId).get();
        switch (action) {
            case "unfavorite":
                if (user.unfavoriteTracks(track)) {
                    trackRepo.flush();
                }
                break;
            case "favorite":
                if (user.favoriteTracks(track)) {
                    trackRepo.flush();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public Paging<DTOTrackFull> getUserTracks(String userId, String viewerId, int offset, int limit, String type) {
        boolean privateView = userId.equals(viewerId);

        int total = privateView ? userTrackRepo.countByUserIdAndTrackNotNullAndAction(userId, type)
                : userTrackRepo.countByUserIdAndTrackStatusAndAction(userId, "public", type);

        Paging<DTOTrackFull> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        Pageable pageable = paging.asPageable();
        List<EntityUserTrack> list = privateView
                ? userTrackRepo.findAllByUserIdAndTrackNotNullAndAction(userId, type, pageable)
                : userTrackRepo.findAllByUserIdAndTrackStatusAndAction(userId, "public", type, pageable);

        paging.setItems(
                list.stream().map(ut -> getTrackFullFromEntity(ut.getTrack(), viewerId)).collect(Collectors.toList()));

        return paging;
    }

    @Override
    public DTOTrackFull getTrackById(String id, String userId) {
        if (!hasTrackAccessPermission(id, userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return getTrackFullFromEntity(trackRepo.findById(id).get(), userId);
    }

    public DTOTrackFull getTrackFullFromEntity(EntityTrack track, String userId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        DTOTrackFull res = mapper.map(getTrackSimple(track, userId), DTOTrackFull.class);

        // set artist who own the release (that the track belongs to)
        Optional<EntityUserRelease> releaseOwner = Optional.of(track.getRelease().getArtistRelease().get(0));
        res.setRelease(releaseOwner.map(ro -> {
            DTOReleaseSimple resRelease = mapper.map(ro.getRelease(), DTOReleaseSimple.class);
            resRelease.setArtist(artistService.getArtistSimple(userId, ro.getRelease().getArtist().getId()));
            return resRelease;
        }).orElse(null));

        return res;
    }

    @Override
    public boolean hasTrackAccessPermission(String trackId, String userId) {
        return trackRepo.existsByIdAndStatus(trackId, "public")
                || userTrackRepo.existsByTrackIdAndUserIdAndAction(trackId, userId, "own");
    }

    @Override
    public List<String> streamFavorite(String userId) {
        return trackRepo.getFavIdList(userId);
    }

    @Override
    public Paging<DTOTrackFull> findTrack(String userId, String key, int offset, int limit) {
        int total = trackRepo.countByTitleIgnoreCaseContainingAndStatus(key, "public");
        Paging<DTOTrackFull> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = trackRepo.findByTitleIgnoreCaseContainingAndStatus(key, "public", paging.asPageable());
        paging.setItems(list.stream().map(t -> getTrackById(t.getId(), userId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public DTOTrackSimpleWithLink getTrackSimpleWithLink(String trackId) {
        ModelMapper mapper = new ModelMapper();
        EntityTrack track = trackRepo.findById(trackId).get();
        DTOTrackSimpleWithLink dto = mapper.map(track, DTOTrackSimpleWithLink.class);
        return dto;
    }

    @Override
    public String deleteTrack(String userId, String trackId) {
        if (!userTrackRepo.existsByTrackIdAndUserIdAndAction(trackId, userId, "own"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        EntityTrack track = trackRepo.findById(trackId).get();
        EntityRelease release = track.getRelease();
        List<EntityTrack> tracks = release.getTracks();
        if (tracks.size() <= 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't delete last track in a release");
        tracks.remove(track);
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
        Set<EntityMasterData> newGenres = new HashSet<>();
        tracks.stream().forEach(reTr -> {
            newGenres.addAll(reTr.getGenres());
        });
        release.setGenres(newGenres);
        trackRepo.delete(track);
        releaseRepo.save(release);
        return trackId;
    }

    @Override
    public String updateTrack(String userId, String trackId, DTOTrackUpdate data) {
        EntityTrack track = trackRepo.getOne(trackId);
        if (!userTrackRepo.existsByTrackIdAndUserIdAndAction(trackId, userId, "own"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (StringUtils.hasText(data.getTitle())) {
            track.setTitle(data.getTitle());
        }
        if (StringUtils.hasText(data.getProducer())) {
            track.setProducer(data.getProducer());
        }
        if (data.getGenres() != null) {
            track.setGenres(Arrays.stream(data.getGenres())
                    .map(genreId -> masterDataRepo.findByIdAndType(genreId, "genre").get())
                    .collect(Collectors.toSet()));
        }
        List<String> uploadKeyList = new ArrayList<>();
        File file = null;
        try {
            file = File.createTempFile(trackId, null);
            MultipartFile mp3128 = data.getMp3128();
            if (mp3128 != null) {
                writeInputToOutput(mp3128.getInputStream(), new FileOutputStream(file));
                Mp3File fmp3 = new Mp3File(file);
                track.setDuration(fmp3.getLengthInMilliseconds());
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(track.getFileSize128());
                String key = track.getId() + "/128";
                s3.putObject(AWSConfig.BUCKET_NAME, key, new FileInputStream(file), metadata);
                uploadKeyList.add(key);
                track.setMp3128(key);
            }
            MultipartFile mp3320 = data.getMp3320();
            if (mp3320 != null) {
                
                writeInputToOutput(mp3320.getInputStream(), new FileOutputStream(file));
                Mp3File fmp3 = new Mp3File(file);
                track.setDuration(fmp3.getLengthInMilliseconds());
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(track.getFileSize320());
                String key = track.getId() + "/320";
                s3.putObject(AWSConfig.BUCKET_NAME, key, new FileInputStream(file), metadata);
                uploadKeyList.add(key);
                track.setMp3320(key);
            }
        } catch (Exception e) {
            for (String key : uploadKeyList) {
                try{
                    s3.deleteObject(AWSConfig.BUCKET_NAME, key);
                }catch(Exception ex){
                    log.error("Error delete "+key, e);
                }
            }
            if(e instanceof UnsupportedTagException || e instanceof InvalidDataException)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type");
            else
                throw new RuntimeException(e);
        } finally{
            if(file != null && file.exists()) file.delete();
        }
        trackRepo.save(track);
        return trackId;
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
    public DTOTrackSimple getTrackSimple(String trackId, String userId) {
        EntityTrack track = trackRepo.findById(trackId).get();
        if (!hasTrackAccessPermission(trackId, userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return getTrackSimple(track, userId);
    }

    DTOTrackSimple getTrackSimple(EntityTrack track, String userId) {
        ModelMapper mapper = new ModelMapper();
        DTOTrackSimple res = mapper.map(track, DTOTrackFull.class);
        res.setRelation(userTrackRepo.getRelation(userId, track.getId()));

        // set artists who own or featured the track
        Set<EntityUserTrack> trackArtists = track.getArtist();
        res.setArtists(trackArtists.stream().map(ut -> artistService.getArtistSimple(userId, ut.getUser().getId()))
                .collect(Collectors.toSet()));
        return res;
    }
}
