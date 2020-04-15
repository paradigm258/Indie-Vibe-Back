package com.swp493.ivb;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import com.swp493.ivb.common.artist.RepositoryArtist;
import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.common.playlist.RepositoryPlaylist;
import com.swp493.ivb.common.release.RepositoryRelease;
import com.swp493.ivb.common.report.RepositoryReport;
import com.swp493.ivb.common.track.RepositoryTrack;
import com.swp493.ivb.common.user.RepositoryUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepoTests {
    @Autowired
    RepositoryArtist artistRepo;
    @Autowired
    RepositoryMasterData masterDataRepo;
    @Autowired
    RepositoryPlaylist playlistRepo;
    @Autowired
    RepositoryRelease releaseRepo;
    @Autowired
    RepositoryReport reportRepo;
    @Autowired
    RepositoryTrack trackRepo;
    @Autowired
    RepositoryUser userRepo;

    @Test
    void nullRepoTest(){
        assertNotNull(artistRepo);
        assertNotNull(masterDataRepo);
        assertNotNull(playlistRepo);
        assertNotNull(releaseRepo);
        assertNotNull(reportRepo);
        assertNotNull(trackRepo);
        assertNotNull(userRepo);
    }
    @Test
    void invalidIdTest(){
        final String id = "invalid";
        assertThrows(NoSuchElementException.class,() -> artistRepo.findById(id).get());
        assertThrows(NoSuchElementException.class,() -> masterDataRepo.findById(id).get());
        assertThrows(NoSuchElementException.class,() -> playlistRepo.findById(id).get());
        assertThrows(NoSuchElementException.class,() -> releaseRepo.findById(id).get());
        assertThrows(NoSuchElementException.class,() -> reportRepo.findById(id).get());
        assertThrows(NoSuchElementException.class,() -> trackRepo.findById(id).get());
        assertThrows(NoSuchElementException.class,() -> userRepo.findById(id).get());
    }
 
}