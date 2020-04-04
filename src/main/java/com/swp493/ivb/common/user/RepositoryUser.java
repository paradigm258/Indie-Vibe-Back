package com.swp493.ivb.common.user;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * IndieUserRepository
 */
public interface RepositoryUser extends JpaRepository<EntityUser, String> {
    public Optional<EntityUser> findByEmail(String email);

    public Optional<EntityUser> findByFbId(String fbId);

    public Boolean existsByEmail(String email);

    public Boolean existsByFbId(String fbId);

    public Boolean existsByIdAndFollowerUsersId(String followed,String follower);

    @Query(value = "SELECT CASE WHEN(COUNT(*)>0) THEN TRUE ELSE FALSE END FROM user_follow_user WHERE followed_id = :followed and follower_id = :follower", nativeQuery = true)
    public boolean isFollowing(String follower, String followed);

    @Query(value = "SELECT COUNT(follower_id) FROM user_follow_user WHERE followed_id=:userId", nativeQuery = true)
    public int countFollowers(@Param("userId") String userId);

    @Query(value = "select count(followed_id) from user_follow_user where follower_id=:userId", nativeQuery = true)
    public int countFollowing(@Param("userId") String userId);

    public List<IOnlyId> findAllByFollowerUsersId(String followerId, Pageable pageable);
    public List<IOnlyId> findAllByFollowingUsersId(String followerId, Pageable pageable);
    List<IOnlyId> findByDisplayNameIgnoreCaseContainingAndUserRoleIdIsNot(String key, String role, Pageable pageable);
    int countByDisplayNameIgnoreCaseContainingAndUserRoleIdIsNot(String key, String role);
    List<EntityUser> findByPlanDueLessThanEqual(Date date);
    int countByArtistStatus(String status);
    List<IOnlyId> findByArtistStatus(String status, Pageable pageable);
    int countByDisplayNameIgnoreCaseContainingAndUserRoleId(String key, String roleId);
    List<IOnlyId> findByDisplayNameIgnoreCaseContainingAndUserRoleId(String key, String roleId, Pageable pageable);
}