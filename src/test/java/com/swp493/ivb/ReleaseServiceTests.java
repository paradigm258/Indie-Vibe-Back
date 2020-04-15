package com.swp493.ivb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import com.swp493.ivb.common.release.DTOReleaseFull;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.common.release.ServiceRelease;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
public class ReleaseServiceTests {

    @Autowired
    ServiceRelease releaseService;

    private final String validUserId = "";
    private final String noPermissionUserId = "";
    private final String releaseId = "";
    private final String unknownId = "unknown";

    @Test
    void notNullServiceTest(){
        assertNotNull(releaseService);
    }

    @Test
    @Transactional
    void releaseIdTest(){
        //Test unknown id
        assertThrows(NoSuchElementException.class, ()->releaseService.getReleaseSimple(unknownId, unknownId));
        assertThrows(NoSuchElementException.class, ()->releaseService.getReleaseSimple(releaseId, unknownId));
        assertThrows(NoSuchElementException.class, ()->releaseService.getReleaseSimple(unknownId, validUserId));
        assertThrows(NoSuchElementException.class, ()->releaseService.getReleaseSimple(unknownId, noPermissionUserId));

        //Test valid id
        assertDoesNotThrow(()->releaseService.getReleaseSimple(releaseId, validUserId));
    }

    @Test
    @Transactional
    void actionReleaseTest(){
        //Test action with invalid id
        assertThrows(NoSuchElementException.class, ()->releaseService.actionRelease(unknownId, unknownId, "favorite"));
        assertThrows(NoSuchElementException.class, ()->releaseService.actionRelease(releaseId, unknownId, "favorite"));
        assertThrows(NoSuchElementException.class, ()->releaseService.actionRelease(unknownId, validUserId, "favorite"));
        assertThrows(NoSuchElementException.class, ()->releaseService.actionRelease(unknownId, noPermissionUserId, "favorite"));

        //Test action with valid id
        //Valid user id with permission can favorite and unfavorite release
        assertDoesNotThrow(()->releaseService.actionRelease(releaseId, validUserId, "favorite"));
        assertDoesNotThrow(()->releaseService.actionRelease(releaseId, validUserId, "unfavorite"));
        assertFalse(()->releaseService.actionRelease(releaseId, validUserId, "unfavorite"));

        //User make release private
        assertDoesNotThrow(()->releaseService.actionRelease(releaseId, validUserId, "make-private"));

        //User can't favorite a private release
        assertThrows(ResponseStatusException.class, ()->releaseService.actionRelease(releaseId, noPermissionUserId, "favorite"));

        //User make release public
        assertDoesNotThrow(()->releaseService.actionRelease(releaseId, validUserId, "make-public"));

        //User can favorite or unfavorite public release
        assertDoesNotThrow(()->releaseService.actionRelease(releaseId, noPermissionUserId, "favorite"));
        assertDoesNotThrow(()->releaseService.actionRelease(releaseId, noPermissionUserId, "unfavorite"));
        assertFalse(()->releaseService.actionRelease(releaseId, noPermissionUserId, "unfavorite"));
    }

    @Test
    @Transactional
    void getDtoReleaseTest(){
        //User make release private
        assertDoesNotThrow(()->releaseService.actionRelease(releaseId, validUserId, "make-private"));
        DTOReleaseFull full = assertDoesNotThrow(()->releaseService.getReleaseFull(releaseId, validUserId, 0, 5).get());
        DTOReleaseSimple simple = assertDoesNotThrow(()->releaseService.getReleaseSimple(releaseId, validUserId));
        assertNotNull(full);
        assertNotNull(simple);
        
        //Getting release without permission
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,()->releaseService.getReleaseFull(releaseId, noPermissionUserId, 0, 5).get());
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus(), "Not right status");
        ex = assertThrows(ResponseStatusException.class,()->releaseService.getReleaseSimple(releaseId, noPermissionUserId));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus(), "Not right status");

        //User make release public
        assertDoesNotThrow(()->releaseService.actionRelease(releaseId, validUserId, "make-public"));

        full = assertDoesNotThrow(()->releaseService.getReleaseFull(releaseId, noPermissionUserId, 0, 5).get());
        simple = assertDoesNotThrow(()->releaseService.getReleaseSimple(releaseId, noPermissionUserId));
        assertNotNull(full);
        assertNotNull(simple);

        //Matching full and simple content
        assertEquals(full.getId(), simple.getId(), "Id does not match");
        assertEquals(full.getType(), simple.getType(), "Type does not match");
        assertEquals(full.getRelation(), simple.getRelation(), "Relation does not match");
    }
}