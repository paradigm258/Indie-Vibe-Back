package com.swp493.ivb.common.artist;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceArtistImpl implements ServiceArtist {

    @Autowired
    RepositoryArtist artistRepo;

	@Override
	public Optional<EntityArtist> getArtist(String id) {
		return artistRepo.findById(id);
	}
    
}
