package com.swp493.ivb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import com.swp493.ivb.common.playlist.DTOPlaylistFull;
import com.swp493.ivb.common.playlist.DTOPlaylistSimple;
import com.swp493.ivb.common.playlist.ServicePlaylist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
public class PlaylistServiceTests {

    private final String validUserId = "a98db973kwl8xp1lz94k";
    private final String noPermissionUserId = "9IwDw6tHJMAR90UGvy0o";
    private final String playlistId = "3Cw7ymglKgUPNbNtoSgd";
    private final String unknownId = "unknown";

    @Autowired
    ServicePlaylist playlistService;

    @Test
    void serviceNotNullTests(){
        assertNotNull(playlistService);    
    }

    @Test
    @Transactional
    void playlistIdTest(){
        //Test unknown id
        assertThrows(NoSuchElementException.class, ()->playlistService.getPlaylistSimple(unknownId, unknownId));
        assertThrows(NoSuchElementException.class, ()->playlistService.getPlaylistSimple(playlistId, unknownId));
        assertThrows(NoSuchElementException.class, ()->playlistService.getPlaylistSimple(unknownId, validUserId));
        assertThrows(NoSuchElementException.class, ()->playlistService.getPlaylistSimple(unknownId, noPermissionUserId));

        //Test valid id
        assertDoesNotThrow(()->playlistService.getPlaylistSimple(playlistId, validUserId));
    }

    @Test
    @Transactional
    void actionPlaylistTest(){
        //Test action with invalid id
        assertThrows(NoSuchElementException.class, ()->playlistService.actionPlaylist(unknownId, unknownId, "favorite"));
        assertThrows(NoSuchElementException.class, ()->playlistService.actionPlaylist(playlistId, unknownId, "favorite"));
        assertThrows(NoSuchElementException.class, ()->playlistService.actionPlaylist(unknownId, validUserId, "favorite"));
        assertThrows(NoSuchElementException.class, ()->playlistService.actionPlaylist(unknownId, noPermissionUserId, "favorite"));

        //Test action with valid id
        //Valid user id with permission can favorite and unfavorite release
        assertDoesNotThrow(()->playlistService.actionPlaylist(playlistId, validUserId, "favorite"));
        assertDoesNotThrow(()->playlistService.actionPlaylist(playlistId, validUserId, "unfavorite"));
        assertFalse(()->playlistService.actionPlaylist(playlistId, validUserId, "unfavorite"));

        //User make release private
        assertDoesNotThrow(()->playlistService.actionPlaylist(playlistId, validUserId, "make-private"));

        //User can't favorite a private release
        assertThrows(ResponseStatusException.class, ()->playlistService.actionPlaylist(playlistId, noPermissionUserId, "favorite"));

        //User make release public
        assertDoesNotThrow(()->playlistService.actionPlaylist(playlistId, validUserId, "make-public"));

        //User can favorite or unfavorite public release
        assertDoesNotThrow(()->playlistService.actionPlaylist(playlistId, noPermissionUserId, "favorite"));
        assertDoesNotThrow(()->playlistService.actionPlaylist(playlistId, noPermissionUserId, "unfavorite"));
        assertFalse(()->playlistService.actionPlaylist(playlistId, noPermissionUserId, "unfavorite"));
    }

    @Test
    @Transactional
    void getDtoPlaylistTest(){
        //User make playlist private
        assertDoesNotThrow(()->playlistService.actionPlaylist(playlistId, validUserId, "make-private"));
        DTOPlaylistFull full = assertDoesNotThrow(()->playlistService.getPlaylistFull(playlistId, validUserId, 0, 5));
        DTOPlaylistSimple simple = assertDoesNotThrow(()->playlistService.getPlaylistSimple(playlistId, validUserId));
        assertNotNull(full);
        assertNotNull(simple);
        
        //Getting playlist without permission
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,()->playlistService.getPlaylistFull(playlistId, noPermissionUserId, 0, 5));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus(), "Not right status");
        ex = assertThrows(ResponseStatusException.class,()->playlistService.getPlaylistSimple(playlistId, noPermissionUserId));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus(), "Not right status");

        //User make playlist public
        assertDoesNotThrow(()->playlistService.actionPlaylist(playlistId, validUserId, "make-public"));

        full = assertDoesNotThrow(()->playlistService.getPlaylistFull(playlistId, noPermissionUserId, 0, 5));
        simple = assertDoesNotThrow(()->playlistService.getPlaylistSimple(playlistId, noPermissionUserId));
        assertNotNull(full);
        assertNotNull(simple);

        //Matching full and simple content
        assertEquals(full.getId(), simple.getId(), "Id does not match");
        assertEquals(full.getType(), simple.getType(), "Type does not match");
        assertEquals(full.getRelation(), simple.getRelation(), "Relation does not match");
    }
}