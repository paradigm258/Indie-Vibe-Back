package com.swp493.ivb.common.user;

import java.util.List;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.config.DTORegisterForm;
import com.swp493.ivb.config.DTORegisterFormFb;

public interface ServiceUser {

    int countFollowers(String userId);

    DTOUserPublic getUserPublic(String userId, String viewerId);

    List<DTOArtistFull> getFollowing(String userId, int offset, int limit);

    void register(DTORegisterForm userForm);

    void register(DTORegisterFormFb fbForm);

    boolean existsByEmail(String email);

    boolean existsByFbId(String fbId);
    
    void followUser(String followerId, String followedId);

    void unfollowUser(String followerId, String followedId);
}
