package com.swp493.ivb.common.user;

import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.config.DTORegisterForm;
import com.swp493.ivb.config.DTORegisterFormFb;

public interface ServiceUser {

    int countFollowers(String userId);

    int countFollowing(String userId);

    DTOUserPublic getUserPublic(String userId, String viewerId);

    DTOUserPrivate getUserPrivate(String userId);

    Paging<DTOUserPublic> getFollowings(String userId, String viewerId, int offset, int limit);

    Paging<DTOUserPublic> getFollowers(String userId, String viewerId, int offset, int limit);

    void register(DTORegisterForm userForm);

    void register(DTORegisterFormFb fbForm);

    boolean existsByEmail(String email);

    boolean existsByFbId(String fbId);

    boolean existsById(String userId);
    
    void followUser(String followerId, String followedId);

    void unfollowUser(String followerId, String followedId);

    Paging<DTOUserPublic> findProfile(String key, String userId, int offset, int limit);

    boolean userUpdate(DTOUserUpdate update, String userId);

    boolean passwordUpdate(String oldPassword, String newPassword, String userId);

    String purchaseMonthly(String stripeToken, EntityUser authUser, String token);

    String purchaseFixed(String type, String stripeToken, EntityUser authUser, String token);

    void updatePlan(); 

    void updateUserPlan(EntityUser user);

    void updateArtist(String userId, String action);

    Paging<DTOUserPublic> listUserProfiles(String key, int offset, int limit);

	void makeCurator(String userId);
}
