package com.swp493.ivb.common.artist;

import java.util.List;
import java.util.stream.Collectors;

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
    ServiceUser userService;

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
        return artistRepo.findById(artistId).map(artist ->{
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
        paging.setItems(list.stream().map(u ->getArtistFull(viewerId, u.getId())
        ).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public Paging<DTOArtistFull> findArtist(String key, String viewerId, int offset, int limit) {
        int total = artistRepo.countByDisplayNameIgnoreCaseContaining(key);
        Paging<DTOArtistFull> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        List<IOnlyId> list = artistRepo.findByDisplayNameIgnoreCaseContaining(key, paging.asPageable());
        paging.setItems(list.stream().map(a ->getArtistFull(viewerId, a.getId())).collect(Collectors.toList()));
        return paging;
    }

    
}
