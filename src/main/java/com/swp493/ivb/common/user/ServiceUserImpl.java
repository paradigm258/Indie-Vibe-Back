package com.swp493.ivb.common.user;

import java.util.Optional;

import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.config.DTORegisterForm;

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
        user.setUserRole(masterDataRepo.findByIdAndType("r-free", "role").get());
        user.setUserCountry(masterDataRepo.findById("c-vnm").get());
        user.setUserPlan(masterDataRepo.findById("p-free").get());
        userRepository.save(user);
        return true;
    }
}