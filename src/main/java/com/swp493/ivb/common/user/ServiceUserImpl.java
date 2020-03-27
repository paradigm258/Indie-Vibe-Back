package com.swp493.ivb.common.user;

import java.util.List;
import java.util.stream.Collectors;

import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.config.DTORegisterForm;
import com.swp493.ivb.config.DTORegisterFormFb;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

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
        user.setDob(userForm.getDob());
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
        if(followedId.equals(followerId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
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
    public Paging<DTOUserPublic> getFollowings(String userId, String viewerId, int offset, int limit) {
        int total = userRepository.countFollowing(userId);
        Paging<DTOUserPublic> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = userRepository.findAllByFollowerUsersId(userId, paging.asPageable());
        paging.setItems(list.stream().map(u -> getUserPublic(u.getId(), viewerId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public Paging<DTOUserPublic> getFollowers(String userId, String viewerId, int offset, int limit){
        int total = userRepository.countFollowers(userId);
        Paging<DTOUserPublic> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = userRepository.findAllByFollowingUsersId(userId, paging.asPageable());
        paging.setItems(list.stream().map(u -> getUserPublic(u.getId(), viewerId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public int countFollowing(String userId) {
        return userRepository.countFollowing(userId);
    }

    @Override
    public Paging<DTOUserPublic> findProfile(String key, String userId, int offset, int limit) {
        int total = userRepository.countByDisplayNameIgnoreCaseContainingAndUserRoleIdIsNot(key,"r-artist");
        Paging<DTOUserPublic> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = userRepository.findByDisplayNameIgnoreCaseContainingAndUserRoleIdIsNot(key,"r-artist", paging.asPageable());
        paging.setItems(list.stream().map(a ->getUserPublic(a.getId(),userId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public DTOUserPrivate getUserPrivate(String userId){
        EntityUser user = userRepository.findById(userId).get();
        ModelMapper mapper = new ModelMapper();
        DTOUserPrivate result = mapper.map(user, DTOUserPrivate.class);
        result.setFollowersCount(userRepository.countFollowers(userId));
        
        return result;
    }

    @Override
    public boolean userUpdate(DTOUserUpdate update, String userId) {
        EntityUser user = userRepository.getOne(userId);
        if(StringUtils.hasLength(update.displayName)){
            user.setDisplayName(update.displayName);
        }
        if(StringUtils.hasLength(update.getEmail())){
            user.setEmail(update.getEmail());
        }
        user.setDob(update.getDob());
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean existsById(String userId) {
        return userRepository.existsById(userId);
    }
}