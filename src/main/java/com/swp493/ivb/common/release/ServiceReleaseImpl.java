package com.swp493.ivb.common.release;

import java.io.IOException;
import java.sql.Timestamp;
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
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.mdata.ServiceMasterData;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.EntityUserRelease;
import com.swp493.ivb.common.user.EntityUserTrack;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.config.AWSConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServiceReleaseImpl implements ServiceRelease {

    private static Logger log = LoggerFactory.getLogger(ServiceReleaseImpl.class);

    @Autowired
    private RepositoryRelease releaseRepo;

    @Autowired
    private ServiceMasterData masterDataService;

    @Autowired
    private ServiceUser userService;

    @Autowired
    private AmazonS3 s3;

    @Override
    public Optional<String> uploadRelease(String artistId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
            MultipartFile[] audioFiles) throws NoSuchElementException {

        final EntityRelease release = new EntityRelease();
        release.setTitle(info.getTitle());
        release.setThumbnail("N/A");
        release.setDate(new Timestamp(new Date().getTime()));
        release.setStatus("public");
        release.setReleaseType(masterDataService.getReleaseTypeById(info.getTypeId()).get());

        EntityUser artist = userService.getUserForProcessing(artistId).get();

        // insert for track and release
        Set<EntityMasterData> releaseGenres = new HashSet<>();

        List<DTOTrackReleaseUpload> tracksInfo = info.getTracks();
        List<String> uploadKeyList = new LinkedList<>();
        List<EntityTrack> tracks = new LinkedList<>();
        try {
            //Iterate through input data to populate release's track list
            for (int i = 0; i < tracksInfo.size(); i++) {
                //Get respective info and audio file
                DTOTrackReleaseUpload trackInfo = tracksInfo.get(i);
                MultipartFile trackContent = audioFiles[i];

                //Populate a track entity
                EntityTrack track = new EntityTrack();
                track.setTitle(trackInfo.getTitle());
                track.setFileSize128((int) trackContent.getSize());
                track.setStatus("public");
                track.setProducer(trackInfo.getProducer());
                track.setRelease(release);

                track.setGenres(Arrays.stream(trackInfo.getGenres()).map(genreId -> {
                    EntityMasterData genre = masterDataService.getGenreById(genreId).get();
                    releaseGenres.add(genre);
                    return genre;
                }).collect(Collectors.toList()));

                track.setMp3320("320");
                track.setDuration128((int) trackContent.getSize() / 16);
                track.setDuration320(320);
                track.setFileSize320(320000);
                track.setMp3Offset(111111);

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(track.getFileSize128());
                String key = artist.getId() + "/" + track.getTitle();
                s3.putObject("indievibe-storage", key, trackContent.getInputStream(), metadata);
                uploadKeyList.add(key);
                track.setMp3128(AWSConfig.BUCKET_URL + key);

                EntityUserTrack artistTrack = new EntityUserTrack();
                artistTrack.setUser(artist);
                artistTrack.setTrack(track);
                artistTrack.setAction("own");
                track.getTrackUsers().add(artistTrack);

                tracks.add(track);
            }
        } catch (IOException e) {
            log.error("error trying to connect to s3", e);
            return Optional.empty();
        } catch (SdkClientException e) {
            log.error("s3 operation failed", e);
            deleteCancel(uploadKeyList);
            return Optional.empty();
        } catch (NoSuchElementException e){
            log.error("invalid id", e);
            deleteCancel(uploadKeyList);
            return Optional.empty();
        }
        release.setTracks(tracks);
        // release.setTracks(tracksInfo.stream().map(trackInfo -> {
        // EntityTrack track = new EntityTrack();
        // return track;
        // }).collect(Collectors.toList()));
        release.setGenres(releaseGenres);

        // insert into user_object for release and user
        EntityUserRelease artistRelease = new EntityUserRelease();
        artistRelease.setUser(artist);
        artistRelease.setRelease(release);
        artistRelease.setAction("own");
        release.getReleaseUsers().add(artistRelease);

        String releaseId = releaseRepo.save(release).getId();

        return Optional.of(releaseId);
    }

    @Override
    public Optional<String> deleteRelease(String releaseId) {
        Optional<EntityRelease> release = releaseRepo.findById(releaseId);
        return release.map(r -> {
            releaseRepo.delete(r);
            r.getTracks().stream().map(track -> {
                try {
                    s3.deleteObject(AWSConfig.BUCKET_NAME, track.getTitle());
                } catch (SdkClientException e) {
                    log.error("Failed to delete: " + track.getTitle());
                }
                return null;
            });
            return r.getId();
        });
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
}
