package com.swp493.ivb.common.artist;

import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.user.ServiceUser;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceArtistImpl implements ServiceArtist {

    @Autowired
    RepositoryArtist artistRepo;

    @Autowired
    ServiceUser userService;

    @Override
    public DTOArtistFull getArtistFull(String userId, String artistId) {
        ModelMapper mapper = new ModelMapper();
        DTOUserPublic userPublic = userService.getUserPublic(artistId, userId);
        return artistRepo.findById(artistId).map(artist ->{
            DTOArtistFull artistFull = mapper.map(artist, DTOArtistFull.class);
            artistFull.setRelation(userPublic.getRelation());
            artistFull.setFollowersCount(userPublic.getFollowersCount());
            return artistFull;
        }).orElse(mapper.map(userPublic, DTOArtistFull.class));
        
    }

    @Override
    public DTOArtistSimple getArtistSimple(String userId, String artistId) {
        ModelMapper mapper = new ModelMapper();
        EntityArtist artist = artistRepo.findById(artistId).get();
        DTOUserPublic userPublic = userService.getUserPublic(artistId, userId);
        DTOArtistSimple artistSimple = mapper.map(artist, DTOArtistSimple.class);
        artistSimple.setRelation(userPublic.getRelation());
        return artistSimple;
    }
}
