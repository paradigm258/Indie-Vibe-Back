package com.swp493.ivb.features.common.user;

import java.util.Optional;

import com.swp493.ivb.common.mdata.RepositoryMasterData;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * IndieUserService
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RepositoryMasterData masterDataRepo;

    @Autowired
    PasswordEncoder encoder;

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
    public boolean existsByFbId(String fbId){
        return userRepository.existsByFbId(fbId);
    }
    public void register(UserEntity user){

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
    public Optional<UserEntity> getUserForProcessing(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserPublicDTO> getUserPublic(String id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);

        return userEntity.map(user -> {
            ModelMapper mapper = new ModelMapper();
            UserPublicDTO result = mapper.map(user, UserPublicDTO.class);
            result.setFollowersCount(userRepository.countFollowers(id));
            return result;
        });
    }
}