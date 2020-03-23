package com.swp493.ivb.common.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.config.DTORegisterForm;
import com.swp493.ivb.config.DTORegisterFormFb;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * IndieUserService
 */
@Service
public class ServiceUserImpl implements ServiceUser {

    @Autowired
    RepositoryUser userRepository;

    @Autowired
    ServiceArtist artistService;

    @Autowired
    RepositoryMasterData masterDataRepo;

    @Autowired
    PasswordEncoder encoder;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByFbId(String fbId) {
        return userRepository.existsByFbId(fbId);
    }

    public void register(EntityUser user) throws Exception {
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setUserRole(masterDataRepo.findByIdAndType("r-free", "role").get());
        userRepository.save(user);
    }

    @Override
    public int countFollowers(String userId) {
        return userRepository.countFollowers(userId);
    }

    @Override
    public DTOUserPublic getUserPublic(String userId, String viewerId) {
        EntityUser user = userRepository.findById(userId).get();
        ModelMapper mapper = new ModelMapper();
        DTOUserPublic result = mapper.map(user, DTOUserPublic.class);
        result.setFollowersCount(userRepository.countFollowers(userId));
        if (userRepository.existsByIdAndFollowerUsersId(userId, viewerId)) {
            result.getRelation().add("favorite");
        }
        return result;
    }

    @Override
    public void register(DTORegisterForm userForm) {
        EntityUser user = new EntityUser();
        user.setDisplayName(userForm.getDisplayName());
        user.setEmail(userForm.getEmail());
        user.setPassword(encoder.encode(userForm.getPassword()));
        user = userDefault(user);
        userRepository.save(user);
    }

    @Override
    public void register(DTORegisterFormFb fbForm) {
        EntityUser user = new EntityUser();
        user.setDisplayName(fbForm.getDisplayName());
        user.setEmail(fbForm.getEmail());
        user.setFbId(fbForm.getFbId());
        user.setThumbnail(fbForm.getThumbnail());
        user = userDefault(user);
        userRepository.save(user);
    }

    private EntityUser userDefault(EntityUser user) {
        user.setUserRole(masterDataRepo.findByIdAndType("r-free", "role").orElse(null));
        user.setUserCountry(masterDataRepo.findById("c-vnm").orElse(null));
        user.setUserPlan(masterDataRepo.findById("p-free").orElse(null));

        return user;
    }

    @Override
    public void followUser(String followerId, String followedId) {
        EntityUser follower = userRepository.findById(followerId).get();
        EntityUser followed = userRepository.findById(followedId).get();
        follower.getFollowingUsers().add(followed);
        userRepository.flush();
    }

    @Override
    public void unfollowUser(String followerId, String followedId) {
        EntityUser follower = userRepository.findById(followerId).get();
        EntityUser followed = userRepository.findById(followedId).get();
        follower.getFollowingUsers().remove(followed);
        userRepository.flush();
    }

    @Override
    public List<DTOArtistFull> getFollowing(String userId, int offset, int limit) {
        EntityUser user = userRepository.findById(userId).get();
        Set<EntityUser> following = user.getFollowingUsers();
        List<DTOArtistFull> result = new ArrayList<>();
        for (EntityUser entityUser : following) {
            DTOArtistFull artistFull = artistService.getArtistFull(userId, entityUser.getId());
            result.add(artistFull);
        }
        return result;
    }

    
}