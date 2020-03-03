package com.swp493.ivb.common.release;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.mdata.ServiceMasterData;
import com.swp493.ivb.common.track.EntityTrack;
import com.swp493.ivb.features.common.user.EntityUserRelease;
import com.swp493.ivb.features.common.user.EntityUserTrack;
import com.swp493.ivb.features.common.user.UserEntity;
import com.swp493.ivb.features.common.user.UserService;

@Service
public class ServiceReleaseImpl implements ServiceRelease {

    private static Logger log = LoggerFactory.getLogger(ServiceReleaseImpl.class);

    @Autowired
    private RepositoryRelease releaseRepo;

    @Autowired
    private ServiceMasterData masterDataService;

    @Autowired
    private UserService userService;

    @Override
    public Optional<String> uploadRelease(String artistId, DTOReleaseInfoUpload info, MultipartFile thumbnail,
            MultipartFile[] audioFiles) {

        final EntityRelease release = new EntityRelease();
        release.setTitle(info.getTitle());
        release.setThumbnail("N/A");
        release.setDate(new Timestamp(new Date().getTime()));
        release.setStatus("public");
        release.setReleaseType(masterDataService.getReleaseTypeById(info.getTypeId()).get());

        UserEntity artist = userService.getUserForProcessing(artistId).get();

        // insert into user_object for release and user
        EntityUserRelease artistRelease = new EntityUserRelease();
        artistRelease.setUser(artist);
        artistRelease.setRelease(release);
        artistRelease.setAction("own");
        release.getReleaseUsers().add(artistRelease);

        // insert for track and release
        Set<EntityMasterData> releaseGenres = new HashSet<>();
        release.setTracks(info.getTracks().stream().map(trackInfo -> {
            EntityTrack track = new EntityTrack();
            track.setTitle(trackInfo.getTitle());
            track.setMp3128("128");
            track.setMp3320("320");
            track.setDuration128(128);
            track.setDuration320(320);
            track.setFileSize128(128000);
            track.setFileSize320(320000);
            track.setMp3Offset(111111);
            track.setStatus("public");
            track.setProducer(trackInfo.getProducer());
            track.setRelease(release);
            
            EntityUserTrack artistTrack = new EntityUserTrack();
            artistTrack.setUser(artist);
            artistTrack.setTrack(track);
            artistTrack.setAction("own");
            track.getTrackUsers().add(artistTrack);

            track.setGenres(Arrays.stream(trackInfo.getGenres())
                .map(genreId -> {
                    EntityMasterData genre = masterDataService.getGenreById(genreId).get();
                    releaseGenres.add(genre);
                    return genre;
                })
                .collect(Collectors.toList()));
            
            return track;
        }).collect(Collectors.toList()));
        release.setGenres(releaseGenres);

        String releaseId = releaseRepo.save(release).getId();

        return Optional.of(releaseId);
    }

    @Override
    public Optional<String> deleteRelease(String releaseId) {
        Optional<EntityRelease> release = releaseRepo.findById(releaseId);
        return release.map(r -> {
            releaseRepo.delete(r);
            return r.getId();
        });
    }
}
