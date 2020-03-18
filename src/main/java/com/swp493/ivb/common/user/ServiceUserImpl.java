package com.swp493.ivb.common.user;

import java.util.Optional;

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
    RepositoryMasterData masterDataRepo;

    @Autowired
    PasswordEncoder encoder;

    public boolean existsByEmail(String email) throws Exception{
        return userRepository.existsByEmail(email);
    }

    public boolean existsByFbId(String fbId) throws Exception{
        return userRepository.existsByFbId(fbId);
    }

    public void register(EntityUser user) throws Exception{
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setUserRole(masterDataRepo.findByIdAndType("r-free", "role").get());
        userRepository.save(user);
    }

    @Override
    public int countFollowers(String userId) throws Exception{
        return userRepository.countFollowers(userId);
    }

    @Override
    public Optional<DTOUserPublic> getUserPublic(String id) throws Exception{
        Optional<EntityUser> userEntity = userRepository.findById(id);

        return userEntity.map(user -> {
            ModelMapper mapper = new ModelMapper();
            DTOUserPublic result = mapper.map(user, DTOUserPublic.class);
            result.setFollowersCount(userRepository.countFollowers(id));
            return result;
        });
    }

    @Override
    public void register(DTORegisterForm userForm) throws Exception{
        EntityUser user = new EntityUser();
        user.setDisplayName(userForm.getDisplayName());
        user.setEmail(userForm.getEmail());
        user.setPassword(encoder.encode(userForm.getPassword()));
        user = userDefault(user);
        userRepository.save(user);
    }

    @Override
    public Optional<EntityUser> findByFbId(String fbId) throws Exception{
        return userRepository.findByFbId(fbId);
    }

    @Override
    public void register(DTORegisterFormFb fbForm) throws Exception {
        EntityUser user = new EntityUser();
        user.setDisplayName(fbForm.getDisplayName());
        user.setEmail(fbForm.getEmail());
        user.setFbId(fbForm.getFbId());
        user.setThumbnail(fbForm.getThumbnail());
        user = userDefault(user);
        userRepository.save(user);
    }

    private EntityUser userDefault(EntityUser user){
        user.setUserRole(masterDataRepo.findByIdAndType("r-free", "role").orElse(null));
        user.setUserCountry(masterDataRepo.findById("c-vnm").orElse(null));
        user.setUserPlan(masterDataRepo.findById("p-free").orElse(null));
        
        return user;
    }

    @Override
    public void followUser(String followerId, String followedId) {
        EntityUser follower = userRepository.findById(followerId).get();
        EntityUser followed = userRepository.findById(followedId).get();
        follower.getFollowedUsers().add(followed);
        userRepository.flush();
    }

    @Override
    public void unfolloweUser(String followerId, String followedId) {
        EntityUser follower = userRepository.findById(followerId).get();
        EntityUser followed = userRepository.findById(followedId).get();
        follower.getFollowedUsers().remove(followed);
        userRepository.flush();
    }
}