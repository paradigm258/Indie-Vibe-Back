package com.swp493.ivb.common.release;

import java.io.File;
import java.io.FileInputStream;
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
import com.mpatric.mp3agic.Mp3File;
import com.swp493.ivb.common.artist.EntityArtist;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.mdata.ServiceMasterData;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.common.user.EntityUserRelease;
import com.swp493.ivb.common.user.EntityUserTrack;
import com.swp493.ivb.config.AWSConfig;

import org.modelmapper.ModelMapper;
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
    private ServiceArtist serviceArtist;

    @Autowired
    private AmazonS3 s3;

    @Override
    public Optional<String> uploadRelease(String artistId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
            MultipartFile[] audioFiles) throws NoSuchElementException {

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
            release.setReleaseType(masterDataService.getReleaseTypeById(info.getTypeId()).get());

            EntityArtist artist = serviceArtist.getArtist(artistId).get();
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
                    EntityMasterData genre = masterDataService.getGenreById(genreId).get();
                    releaseGenres.add(genre);
                    return genre;
                }).collect(Collectors.toList()));

                track.setDuration128(0);
                track.setDuration320(0);
                track.setFileSize128(0);
                track.setFileSize320(0);
                track.setMp3128("mp3128");
                track.setMp3320("mp3320");
                track.setMp3Offset(0);

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
                track.setDuration128(mp3128.getLengthInMilliseconds());
                track.setMp3Offset(mp3128.getStartOffset());
                ObjectMetadata metadata128 = new ObjectMetadata();
                metadata128.setContentLength(track.getFileSize128());
                String key = track.getId() + "/128";
                s3.putObject(AWSConfig.BUCKET_NAME, key, new FileInputStream(file), metadata128);
                uploadKeyList.add(key);
                track.setMp3128(AWSConfig.BUCKET_URL + key);

                writeInputToOutput(trackContent320.getInputStream(), new FileOutputStream(file));
                Mp3File mp3320 = new Mp3File(file);
                track.setDuration320(mp3320.getLengthInMilliseconds());

                ObjectMetadata metadata320 = new ObjectMetadata();
                metadata320.setContentLength(track.getFileSize320());
                key = track.getId() + "/320";
                s3.putObject(AWSConfig.BUCKET_NAME, key, new FileInputStream(file), metadata320);
                uploadKeyList.add(key);
                track.setMp3320(AWSConfig.BUCKET_URL + key);
            }

            release.setTracks(tracks);
            releaseRepo.save(release);

            return Optional.of(release.getId());
        } catch (Exception e) {
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
    public Optional<String> deleteRelease(String releaseId, String artistId) {
        Optional<EntityRelease> release = releaseRepo.findById(releaseId);
        return release.map(r -> {
            if(!r.getArtist().isPresent() || !r.getArtist().get().getId().equals(artistId)) return "";
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

    @Override
    public Optional<EntityRelease> getRelease(String releaseId) {
        return releaseRepo.findById(releaseId);
    }

    @Override
    public Optional<List<DTOReleaseSimple>> getOwnRelease(String artistId) {
        Optional<EntityArtist> artist = serviceArtist.getArtist(artistId);
        return artist.map(a ->{
            ModelMapper mapper = new ModelMapper();
            List<DTOReleaseSimple> list = a.getOwnReleases().stream().map(r ->{
                return mapper.map(r, DTOReleaseSimple.class);
            }).collect(Collectors.toList());
            return list;
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

}
