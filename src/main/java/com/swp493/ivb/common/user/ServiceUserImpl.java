package com.swp493.ivb.common.user;

import java.util.Optional;

import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.config.DTORegisterForm;
import com.swp493.ivb.config.DTORegisterFormFb;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * IndieUserService
 */
@Service
public class ServiceUserImpl implements ServiceUser {

    private static final Logger logger = LoggerFactory.getLogger(ServiceUserImpl.class);

    @Autowired
    RepositoryUser userRepository;

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

    public void register(EntityUser user) {

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
    public Optional<EntityUser> getUserForProcessing(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<DTOUserPublic> getUserPublic(String id) {
        Optional<EntityUser> userEntity = userRepository.findById(id);

        return userEntity.map(user -> {
            ModelMapper mapper = new ModelMapper();
            DTOUserPublic result = mapper.map(user, DTOUserPublic.class);
            result.setFollowersCount(userRepository.countFollowers(id));
            return result;
        });
    }

    @Override
    public boolean register(DTORegisterForm userForm) {
        EntityUser user = new EntityUser();
        user.setDisplayName(userForm.getDisplayName());
        user.setEmail(userForm.getEmail());
        user.setPassword(encoder.encode(userForm.getPassword()));
        user = userDefault(user);
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            logger.error("failed to save user", e);
            return false;
        }

    }

    @Override
    public Optional<EntityUser> findByFbId(String fbId) {
        return userRepository.findByFbId(fbId);
    }

    @Override
    public boolean register(DTORegisterFormFb fbForm) {
        EntityUser user = new EntityUser();
        user.setDisplayName(fbForm.getDisplayName());
        user.setEmail(fbForm.getEmail());
        user.setFbId(fbForm.getFbId());
        user.setThumbnail(fbForm.getThumbnail());
        user = userDefault(user);
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            logger.error("failed to save user", e);
            return false;
        }
    }

    private EntityUser userDefault(EntityUser user){
        user.setUserRole(masterDataRepo.findByIdAndType("r-free", "role").orElse(null));
        user.setUserCountry(masterDataRepo.findById("c-vnm").orElse(null));
        user.setUserPlan(masterDataRepo.findById("p-free").orElse(null));
        
        return user;
    }
}