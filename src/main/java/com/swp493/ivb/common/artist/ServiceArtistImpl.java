package com.swp493.ivb.common.artist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceArtistImpl implements ServiceArtist {

    @Autowired
    RepositoryArtist artistRepo;
}
