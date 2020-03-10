package com.swp493.ivb.common.release;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
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
        release.setDate(new Timestamp(new Date().getTime()));
        release.setStatus("public");
        release.setReleaseType(masterDataService.getReleaseTypeById(info.getTypeId()).get());

        EntityUser artist = userService.getUserForProcessing(artistId).get();

        // insert for track and release
        Set<EntityMasterData> releaseGenres = new HashSet<>();

        List<DTOTrackReleaseUpload> tracksInfo = info.getTracks();
        List<String> uploadKeyList = new LinkedList<>();
        List<EntityTrack> tracks = new LinkedList<>();
        File file = null;
        try {
            // Upload thumbnail
            if (thumbnail != null) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(thumbnail.getSize());
                String key = release.getId();
                s3.putObject(new PutObjectRequest(AWSConfig.BUCKET_NAME, key, thumbnail.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                uploadKeyList.add(key);
                release.setThumbnail(AWSConfig.BUCKET_URL + key);
            }

            // Iterate through input data to populate release's track list
            for (int i = 0; i < tracksInfo.size(); i++) {
                // Get respective info and audio file
                DTOTrackReleaseUpload trackInfo = tracksInfo.get(i);
                MultipartFile trackContent128 = audioFiles[i * 2];
                MultipartFile trackContent320 = audioFiles[i * 2 + 1];

                // Populate a track entity
                EntityTrack track = new EntityTrack();
                track.setTitle(trackInfo.getTitle());
                track.setFileSize128(trackContent128.getSize());
                track.setFileSize320(trackContent320.getSize());

                track.setStatus("public");
                track.setProducer(trackInfo.getProducer());
                track.setRelease(release);

                track.setGenres(Arrays.stream(trackInfo.getGenres()).map(genreId -> {
                    EntityMasterData genre = masterDataService.getGenreById(genreId).get();
                    releaseGenres.add(genre);
                    return genre;
                }).collect(Collectors.toList()));

                file = File.createTempFile(release.getId(), null);
                {
                    writeInputToOutput(trackContent128.getInputStream(), new FileOutputStream(file));
                    Mp3File mp3128 = new Mp3File(file);
                    track.setDuration128(mp3128.getLengthInSeconds());
                    track.setMp3Offset(mp3128.getStartOffset());
                }
                ObjectMetadata metadata128 = new ObjectMetadata();
                metadata128.setContentLength(track.getFileSize128());
                String key = track.getId() + "/128";
                s3.putObject(AWSConfig.BUCKET_NAME, key, trackContent128.getInputStream(), metadata128);
                uploadKeyList.add(key);
                track.setMp3128(AWSConfig.BUCKET_URL + key);

                {
                    writeInputToOutput(trackContent320.getInputStream(), new FileOutputStream(file));
                    Mp3File mp3320 = new Mp3File(file);
                    track.setDuration320(mp3320.getLengthInSeconds());
                }
                ObjectMetadata metadata320 = new ObjectMetadata();
                metadata320.setContentLength(track.getFileSize320());
                key = track.getId() + "/320";
                s3.putObject(AWSConfig.BUCKET_NAME, key, trackContent320.getInputStream(), metadata320);
                uploadKeyList.add(key);
                track.setMp3320(AWSConfig.BUCKET_URL + key);

                EntityUserTrack artistTrack = new EntityUserTrack();
                artistTrack.setUser(artist);
                artistTrack.setTrack(track);
                artistTrack.setAction("own");
                track.getTrackUsers().add(artistTrack);

                tracks.add(track);
            }
        } catch (IOException | SdkClientException | NoSuchElementException | UnsupportedTagException
                | InvalidDataException e) {
            log.error("Track upload failed", e);
            deleteCancel(uploadKeyList);
            return Optional.empty();
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }

        release.setTracks(tracks);
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
            try {
                s3.deleteObject(AWSConfig.BUCKET_NAME, r.getId());
            } catch (SdkClientException e) {
                log.error("Failed to delete: " + r.getTitle(), e);
            }
            r.getTracks().stream().map(track -> {
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

    private void writeInputToOutput(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
    }

    @Override
    public Optional<EntityRelease> getRelease(String releaseId) {
        return releaseRepo.findById(releaseId);
    }
}
