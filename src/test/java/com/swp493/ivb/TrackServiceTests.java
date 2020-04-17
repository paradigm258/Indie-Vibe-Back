package com.swp493.ivb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import com.swp493.ivb.common.track.DTOTrackFull;
import com.swp493.ivb.common.track.DTOTrackSimple;
import com.swp493.ivb.common.track.ServiceTrack;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class TrackServiceTests {
    private final String validUserId = "9IwDw6tHJMAR90UGvy0o";
    private final String trackId = "0vzGxupmTVNSepe0CFk9";
    private final String unknownId = "unknown";

    @Autowired
    ServiceTrack trackService;

    @Test
    void serviceNotNullTrack(){
        assertNotNull(trackService);
    }

    @Test
    @Transactional
    void trackIdTest(){
        //Test unknown id
        assertThrows(NoSuchElementException.class, ()->trackService.getTrackById(unknownId, validUserId));
        assertThrows(NoSuchElementException.class, ()->trackService.getTrackById(unknownId, unknownId));
        //Test valid id
        assertDoesNotThrow(() ->trackService.getTrackById(trackId, validUserId));
    }

    @Test
    @Transactional
    void favoriteTrackTest(){
        //Test unknownId
        assertThrows(NoSuchElementException.class, ()-> trackService.favoriteTrack(validUserId, unknownId));
        assertThrows(NoSuchElementException.class, ()-> trackService.favoriteTrack(unknownId, trackId));
        assertThrows(NoSuchElementException.class, ()-> trackService.favoriteTrack(unknownId, unknownId));
        //Test favorite track with valid id
        assertTrue(trackService.favoriteTrack(validUserId, trackId),"Fail to favorite track");
        //Test unfavorite track with valid id
        assertTrue(trackService.unfavoriteTrack(validUserId, trackId),"Fail to unfavorite track");
    }

    @Test
    @Transactional
    void getDtoTrackTest(){

        final DTOTrackFull full = assertDoesNotThrow(() -> trackService.getTrackById(trackId, validUserId),
                "Error with getting full dto");
        final DTOTrackSimple simple = assertDoesNotThrow(() -> trackService.getTrackSimple(trackId, validUserId),
                "Error with getting simple dto");

        assertEquals(full.getId(), simple.getId(), "Id does not match");
        assertEquals(full.getType(), simple.getType(), "Type does not match");
        assertEquals(full.getGenres(), simple.getGenres(), "Genres do not match");
    }

}