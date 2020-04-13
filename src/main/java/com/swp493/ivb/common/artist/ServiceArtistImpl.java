package com.swp493.ivb.common.artist;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.swp493.ivb.common.mdata.DTOReleaseType;
import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.common.mdata.ServiceMasterData;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.user.IOnlyId;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.view.Paging;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceArtistImpl implements ServiceArtist {

    @Autowired
    RepositoryArtist artistRepo;

    @Autowired
    RepositoryUser userRepo;

    @Autowired
    RepositoryMasterData masterDataRepo;

    @Autowired
    ServiceUser userService;

    @Autowired
    ServiceMasterData masterDataService;

    @Autowired
    ServiceRelease releaseService;
    

    @Override
    public DTOArtistFull getArtistFull(String userId, String artistId) {
        ModelMapper mapper = new ModelMapper();
        DTOUserPublic userPublic = userService.getUserPublic(artistId, userId);
        return artistRepo.findById(artistId).map(artist -> {
            DTOArtistFull artistFull = mapper.map(artist, DTOArtistFull.class);
            artistFull.setRelation(userPublic.getRelation());
            artistFull.setFollowersCount(userPublic.getFollowersCount());
            return artistFull;
        }).orElse(mapper.map(userPublic, DTOArtistFull.class));
    }

    @Override
    public DTOArtistSimple getArtistSimple(String userId, String artistId) {
        ModelMapper mapper = new ModelMapper();
        DTOUserPublic userPublic = userService.getUserPublic(artistId, userId);
        return artistRepo.findById(artistId).map(artist -> {
            DTOArtistSimple artistSimple = mapper.map(artist, DTOArtistSimple.class);
            artistSimple.setRelation(userPublic.getRelation());
            return artistSimple;
        }).orElse(mapper.map(userPublic, DTOArtistSimple.class));
    }

    @Override
    public Paging<DTOArtistFull> getArtists(String userId, String viewerId, int offset, int limit) {
        int total = artistRepo.countByFollowerUsersId(userId);
        Paging<DTOArtistFull> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = artistRepo.findAllByFollowerUsersId(userId, paging.asPageable());
        paging.setItems(list.stream().map(u -> getArtistFull(viewerId, u.getId())).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public Paging<DTOArtistFull> findArtist(String key, String viewerId, int offset, int limit) {
        int total = artistRepo.countByDisplayNameIgnoreCaseContaining(key);
        Paging<DTOArtistFull> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = artistRepo.findByDisplayNameIgnoreCaseContaining(key, paging.asPageable());
        paging.setItems(list.stream().map(a -> getArtistFull(viewerId, a.getId())).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public Map<String, Object> getArtistReleaseByType(String artistId, String userId, String releaseTypeId, int offset,
            int limit) {
        DTOReleaseType releaseType = masterDataService.getReleaseType(releaseTypeId);
        Paging<DTOReleaseSimple> list = releaseService.getArtistReleaseByType(artistId, userId, releaseTypeId, offset, limit);
        Map<String, Object> result = new HashMap<>();
        result.put("releaseType", releaseType);
        result.put("releases", list);
        return result;
    }

    @Override
    public Paging<DTOArtistFull> getArtistsRequestProfile(String adminId, int offset, int limit) {
        int total = userRepo.countByArtistStatus("pending");
        Paging<DTOArtistFull> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = userRepo.findByArtistStatus("pending", paging.asPageable());
        paging.setItems(list.stream().map(user -> getArtistFull(adminId, user.getId())).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public List<String> streamArtist(String artistId) {
        Optional<EntityArtist> artist = artistRepo.findById(artistId);

        return artist.map(a -> {
            return a.getUserOwnTracks()
                .stream()
                .map(uot -> uot.getTrack().getId())
                .collect(Collectors.toList());
        }).orElse(Collections.<String>emptyList());
    }
}
