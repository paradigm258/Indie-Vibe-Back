package com.swp493.ivb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import com.swp493.ivb.common.user.DTOUserPrivate;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.user.ServiceUserSecurityImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
public class UserServiceTests {

    private final String userId1 = "9IwDw6tHJMAR90UGvy0o";
    private final String userId2 = "9s2vQcIMmojuYEbg1Swu";

    @Autowired
    ServiceUser userService;
    @Autowired
    ServiceUserSecurityImpl userDetailsService;
    @Autowired
    RepositoryUser userRepo;
    @Test
    void serviceNotNullTests(){
        assertNotNull(userDetailsService);
        assertNotNull(userService);
    }

    @Test
    void unknownInputTests(){
        final String unknown = "unknown";
        assertThrows(UsernameNotFoundException.class, ()->userDetailsService.loadUserByFbId(unknown));
        assertThrows(UsernameNotFoundException.class, ()->userDetailsService.loadUserByUsername(unknown));
    }

    @Test
    void unknownIdTests(){
        String unknown = "unknown";
        assertThrows(NoSuchElementException.class, ()->userService.getUserPrivate(unknown));
    }

    @Test
    @Transactional
    void userFollowTests(){
        EntityUser user1 = userRepo.findById(userId1).get();
        EntityUser user2 = userRepo.findById(userId2).get();
        
        //Follow self
        assertThrows(ResponseStatusException.class, ()->userService.followUser(userId1, userId1));
        
        //Follow user
        assertDoesNotThrow(()->userService.followUser(userId1, userId2));

        //Followed appears in following list
        assertTrue(user1.getFollowingUsers().contains(user2),"User following not updated");

        //Follower appears in follower list
        assertTrue(user2.getFollowerUsers().contains(user1),"User follower not updated");

        //Unfollow user
        assertDoesNotThrow(()->userService.unfollowUser(userId1, userId2));

        user1 = userRepo.findById(userId1).get();
        user2 = userRepo.findById(userId2).get();
        //Followed removed from following list
        assertFalse(user1.getFollowingUsers().contains(user2),"User following not updated");
        //Follower removed front follower list
        assertFalse(user2.getFollowerUsers().contains(user1),"User follower not updated");

    }

    @Test
    @Transactional
    void getDtoTests(){
        //Get public dto
        DTOUserPublic publicProfile = assertDoesNotThrow(()->userService.getUserPublic(userId1, userId2));
        assertNotNull(publicProfile);
        //Get get private dto
        DTOUserPrivate privateProfile = assertDoesNotThrow(()->userService.getUserPrivate(userId1));
        assertNotNull(privateProfile);

        assertEquals(publicProfile.getId(), privateProfile.getId());
        assertEquals(publicProfile.getDisplayName(), privateProfile.getDisplayName());
        assertEquals(publicProfile.getFollowersCount(), privateProfile.getFollowersCount());
        assertEquals(publicProfile.getRole().getId(), privateProfile.getRole().getId());
        assertEquals(publicProfile.getThumbnail(), privateProfile.getThumbnail());
    }
}