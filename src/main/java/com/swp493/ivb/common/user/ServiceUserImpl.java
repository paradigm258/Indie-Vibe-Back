package com.swp493.ivb.common.user;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Subscription;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.common.relationship.EntityUserRelease;
import com.swp493.ivb.common.relationship.RepositoryUserRelease;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.config.AWSConfig;
import com.swp493.ivb.config.DTORegisterForm;
import com.swp493.ivb.config.DTORegisterFormFb;
import com.swp493.ivb.config.IndieUserPrincipal;
import com.swp493.ivb.features.cms.ServiceCMS;
import com.swp493.ivb.util.BillingUtils;
import com.swp493.ivb.util.EmailUtils;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * IndieUserService
 */
@Service
public class ServiceUserImpl implements ServiceUser {

    private static Logger log = LoggerFactory.getLogger(ServiceUserImpl.class);

    @Autowired
    AmazonS3 s3;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    RepositoryUser userRepository;

    @Autowired
    ServiceArtist artistService;

    @Autowired
    RepositoryMasterData masterDataRepo;

    @Autowired
    RepositoryUserRelease userReleaseRepo;

    @Autowired
    ServiceRelease releaseService;

    @Autowired
    ServiceCMS cmsService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    BillingUtils billingUtils;

    @Autowired
    TokenStore tokenStore;

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
        user.setGender(userForm.getGender());
        user.setActivated(false);
        user = userDefault(user);
        sendActivateEmail(userRepository.save(user));
    }

    @Override
    public void register(DTORegisterFormFb fbForm) {
        EntityUser user = new EntityUser();
        user.setDisplayName(fbForm.getDisplayName());
        user.setEmail(fbForm.getEmail());
        user.setFbId(fbForm.getFbId());
        user.setThumbnail(fbForm.getThumbnail());
        user.setDob(fbForm.getDob());
        user.setActivated(true);
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
        if (followedId.equals(followerId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't follow yourself");
        EntityUser follower = userRepository.findById(followerId).get();
        EntityUser followed = userRepository.findById(followedId).get();
        follower.getFollowingUsers().add(followed);
        followed.getFollowerUsers().add(follower);
        userRepository.flush();
    }

    @Override
    public void unfollowUser(String followerId, String followedId) {
        EntityUser follower = userRepository.findById(followerId).get();
        EntityUser followed = userRepository.findById(followedId).get();
        follower.getFollowingUsers().remove(followed);
        followed.getFollowerUsers().remove(follower);
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
    public Paging<DTOUserPublic> getFollowers(String userId, String viewerId, int offset, int limit) {
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
        int total = userRepository.countByDisplayNameIgnoreCaseContainingAndUserRoleIdIsNot(key, "r-artist");
        Paging<DTOUserPublic> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = userRepository.findByDisplayNameIgnoreCaseContainingAndUserRoleIdIsNot(key, "r-artist",
                paging.asPageable());
        paging.setItems(list.stream().map(a -> getUserPublic(a.getId(), userId)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public DTOUserPrivate getUserPrivate(String userId) {
        EntityUser user = userRepository.findById(userId).get();
        ModelMapper mapper = new ModelMapper();
        DTOUserPrivate result = mapper.map(user, DTOUserPrivate.class);
        result.setFollowersCount(userRepository.countFollowers(userId));

        return result;
    }

    @Override
    public boolean userUpdate(DTOUserUpdate update, String userId) {
        EntityUser user = userRepository.getOne(userId);
        if (StringUtils.hasText(update.getDisplayName())) {
            user.setDisplayName(update.getDisplayName());
        }
        if (StringUtils.hasText(update.getEmail())) {
            user.setEmail(update.getEmail());
        }
        if (update.getDob() != null) {
            try {
                user.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(update.getDob()));
            } catch (ParseException e) {
                return false;
            }
        }
        user.setGender(update.getGender());
        MultipartFile thumbnail = update.getThumbnail();
        if (thumbnail != null) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(thumbnail.getSize());
            String key = userId;
            try {
                s3.putObject(new PutObjectRequest(AWSConfig.BUCKET_NAME, key, thumbnail.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                user.setThumbnail(AWSConfig.BUCKET_URL + key);
            } catch (IOException e) {
                throw new RuntimeException("Error getting input stream for thumbnail", e);
            }
        }
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean existsById(String userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean passwordUpdate(String oldPassword, String newPassword, String userId) {
        EntityUser user = userRepository.findById(userId).get();
        String hash = user.getPassword();
        if (!encoder.matches(oldPassword, hash))
            return false;
        if (oldPassword.equals(newPassword))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password can't be the same as old password");
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public String purchaseMonthly(String stripeToken, EntityUser authUser, String token) {
        try {
            IndieUserPrincipal principal = (IndieUserPrincipal) tokenStore.readAuthentication(token).getPrincipal();
            EntityUser user = userRepository.findById(authUser.getId()).get();
            Date plandue = user.getPlanDue();
            if (plandue == null || plandue.before(new Date())) {
                Subscription subscription = billingUtils.createSubscription(stripeToken, user);
                String status = subscription.getStatus();
                user.setBilling(subscription.getId());
                switch (status) {
                    case "active":
                        user.setPlanStatus("active");
                        EntityMasterData role = null;
                        if (user.getArtistStatus().equals("approved")) {
                            role = masterDataRepo.findByIdAndType("r-artist", "role").get();
                        } else {
                            role = masterDataRepo.findByIdAndType("r-premium", "role").get();
                        }
                        user.setUserRole(role);
                        user.setUserPlan(masterDataRepo.findByIdAndType("p-monthly", "plan").get());
                        user.setPlanDue(Date
                                .from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        cmsService.recordPurchase(30000L, "p-monthly");
                        user = userRepository.save(user);
                        principal.setUser(user);
                        return "Payment success";
                    case "incomplete":
                        user.setUserPlan(masterDataRepo.findByIdAndType("p-monthly", "plan").get());
                        user.setPlanStatus("pending");
                        user = userRepository.save(user);
                        principal.setUser(user);
                        return "Your payment is being processed";
                    default:
                        throw new RuntimeException("Unknown payment status: " + status);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your current plan is not expired");
            }
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String purchaseFixed(String type, String stripeToken, EntityUser authUser, String token) {
        EntityUser user = userRepository.findById(authUser.getId()).get();
        IndieUserPrincipal principal = (IndieUserPrincipal) tokenStore.readAuthentication(token).getPrincipal();
        Date plandue = user.getPlanDue();
        if (plandue == null || plandue.before(new Date())) {
            try {
                Charge charge = billingUtils.createCharge(type, stripeToken, user);
                String status = charge.getStatus();
                switch (status) {
                    case "succeeded":
                        user.setBilling(charge.getId());
                        user.setUserPlan(masterDataRepo.findByIdAndType("p-fixed", "plan").get());
                        EntityMasterData role = null;
                        if (user.getArtistStatus().equals("approved")) {
                            role = masterDataRepo.findByIdAndType("r-artist", "role").get();
                        } else {
                            role = masterDataRepo.findByIdAndType("r-premium", "role").get();
                        }
                        cmsService.recordPurchase(charge.getAmount(), "p-fixed");
                        user.setUserRole(role);
                        user.setPlanStatus("active");
                        user = userRepository.save(user);
                        principal.setUser(user);
                        return "Purchase success";
                    case "pending":
                        user.setBilling(charge.getId());
                        user.setUserPlan(masterDataRepo.findByIdAndType("p-fixed", "plan").get());
                        user.setPlanStatus("pending");
                        user = userRepository.save(user);
                        principal.setUser(user);
                        return "Your purchase is being processed";
                    case "failed":
                        return null;
                    default:
                        throw new RuntimeException("Unknown payment status: " + status);
                }

            } catch (StripeException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your current plan is not expired");
        }
    }

    @Override
    public void updatePlan() {
        LocalDate date = LocalDate.now().plusMonths(1);
        List<EntityUser> users = userRepository.findByPlanDueLessThanEqual(new Date());

        for (EntityUser user : users) {
            try {
                if (user.getUserPlan().getId().equals("p-monthly")) {
                    String status = billingUtils.checkSubscriptionStatus(user.getBilling());
                    switch (status) {
                        case "active":
                            cmsService.recordPurchase(30000L, "p-monthly");
                            user.setPlanDue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            user.setPlanStatus("active");
                            break;
                        default:
                            user.setPlanStatus("pending");
                            break;
                    }
                } else {
                    user.setBilling(null);
                    if ("r-premium".equals(user.getUserRole().getId()))
                        user.setUserRole(masterDataRepo.findByIdAndType("r-free", "role").get());
                    user.setUserPlan(masterDataRepo.findByIdAndType("p-free", "plan").get());
                    user.setPlanStatus("expired");
                }
                user = userRepository.save(user);
            } catch (StripeException e) {
                log.error("Error checking billing id", e);
            }
        }

    }

    private void activatePlan(EntityUser user) {
        user.setPlanStatus("active");
        EntityMasterData role = null;
        if (user.getArtistStatus().equals("approved")) {
            role = masterDataRepo.findByIdAndType("r-artist", "role").get();
        } else {
            role = masterDataRepo.findByIdAndType("r-premium", "role").get();
        }
        user.setUserRole(role);
        EntityUser userUpdate = userRepository.getOne(user.getId());
        userUpdate.setPlanStatus("active");
        userUpdate.setUserRole(role);
        userRepository.save(userUpdate);
    }

    @Override
    public void updateUserPlan(EntityUser user) {
        if ("pending".equals(user.getPlanStatus())) {
            String status;
            Charge charge = null;
            if (user.getUserPlan().getId().equals("p-monthly")) {
                try {
                    status = billingUtils.checkSubscriptionStatus(user.getBilling());
                } catch (StripeException e) {
                    log.error("Error connecting to stripe", e);
                    status = "pending";
                }
                switch (status) {
                    case "active":
                        activatePlan(user);
                        break;
                    default:
                        break;
                }
            } else {
                try {
                    charge = billingUtils.checkChargeStatus(user.getBilling());
                    status = charge.getStatus();
                } catch (StripeException e) {
                    log.error("Error connecting to stripe", e);
                    status = "pending";
                }
                switch (status) {
                    case "succeeded":
                        cmsService.recordPurchase(charge.getAmount(), "p-fixed");
                        activatePlan(user);
                        break;
                    case "failed":

                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void updateArtist(String userId, String action) {
        EntityUser user = userRepository.getOne(userId);
        if ("approve".equals(action)) {
            user.setArtistStatus("approved");
            user.setUserRole(masterDataRepo.findById("r-artist").get());
        } else {
            user.setArtistStatus("open");
            userRepository.deleteBiography(userId);
            List<EntityUserRelease> releases = userReleaseRepo.findByUserIdAndReleaseNotNullAndAction(userId, "own",
                    PageRequest.of(0, 1));
            releases.stream().forEach(ur -> {
                String releaseId = ur.getRelease().getId();
                releaseService.deleteRelease(releaseId, userId);
            });
        }
        userRepository.save(user);
    }

    @Override
    public Paging<DTOUserPublic> listUserProfiles(String key, int offset, int limit) {
        List<String> roles = Arrays.asList("r-free", "r-premium", "r-curator");
        int total = userRepository.countByDisplayNameIgnoreCaseContainingAndUserRoleIdIn(key, roles);
        Paging<DTOUserPublic> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = userRepository.findByDisplayNameIgnoreCaseContainingAndUserRoleIdIn(key, roles,
                paging.asPageable());
        paging.setItems(list.stream().map(a -> getUserPublic(a.getId(), a.getId())).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public void makeCurator(String userId) {
        EntityUser user = userRepository.getOne(userId);
        String roleId = user.getUserRole().getId();
        if (roleId.equals("r-free") || roleId.equals("r-premium")) {
            user.setUserRole(masterDataRepo.findById("r-curator").get());
            userRepository.save(user);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User role not compatible");
    }

    @Override
    public void unmakeCurator(String userId) {
        EntityUser user = userRepository.getOne(userId);
        String roleId = user.getUserRole().getId();
        if ("r-curator".equals(roleId)) {
            String currPlan = user.getUserPlan().getId();
            if ("p-free".equals(currPlan)) {
                user.setUserRole(masterDataRepo.findById("r-free").get());
            } else {
                user.setUserRole(masterDataRepo.findById("r-premium").get());
            }
            userRepository.save(user);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User role not compatible");
    }

    @Override
    public void cancelSubsciption(String userId) {
        EntityUser user = userRepository.findById(userId).get();
        if ("p-monthly".equals(user.getUserPlan().getId())) {
            try {
                long refund = billingUtils.cancelSubscription(user.getBilling());
                cmsService.recordPurchase(refund, "p-monthly");
                user.setBilling(null);
                user.setPlanDue(null);
                user.setPlanStatus("canceled");
                user.setUserPlan(masterDataRepo.findByIdAndType("p-free", "plan").get());
                if (!user.getUserRole().getId().equals("r-curator"))
                    user.setUserRole(masterDataRepo.findByIdAndType("r-free", "role").get());
                userRepository.save(user);
            } catch (StripeException e) {
                log.error("Can't cancel stripe subscription", e);
                throw new RuntimeException(e);
            }
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid plan canceling");
    }

    @Override
    public void activateUser(String userId) {
        EntityUser user = userRepository.getOne(userId);
        if (!user.isActivated()) {
            user.setActivated(true);
            userRepository.save(user);
        }
    }

    @Override
    public void sendActivateEmail(EntityUser user) {
        try {
            emailUtils.sendConfirmEmail(user);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | NoSuchPaddingException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resendActivateEmail(String email, String password) {
        EntityUser user = userRepository.findByEmail(email).get();
        if (!encoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Incorrect password");
        }
        sendActivateEmail(user);
    }

    @Override
    public void resetPassword(String email) {
        try {
            EntityUser user = userRepository.findByEmail(email).get();
            String plaintext = new RandomValueStringGenerator(10).generate();
            user.setPassword(encoder.encode(plaintext));
            userRepository.save(user);
            emailUtils.sendResetPassword(user,plaintext);
        } catch (NoSuchElementException e) {
        } catch (MessagingException e) {
            log.error("Error sending email", e);
            throw new RuntimeException(e);
        }
    }

}