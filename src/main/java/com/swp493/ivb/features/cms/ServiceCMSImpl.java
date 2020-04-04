package com.swp493.ivb.features.cms;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.release.DTOReleasePending;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.view.Paging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceCMSImpl implements ServiceCMS {

    @Autowired
    ServiceArtist artistService;

    @Autowired
    ServiceUser userService;

    @Autowired
    ServiceRelease releaseService;

    @Override
    public Paging<DTOArtistFull> getRequests(String adminId, int offset, int limit) {
        return artistService.getArtistsRequestProfile(adminId, offset, limit);
    }

    @Override
    public DTOReleasePending getArtistRequest(String artistId, int offset, int limit) {
        return releaseService.getPendingRelease(artistId, offset, limit);
    }

    @Override
    public boolean responseRequest(String userId, String action) {
        userService.updateArtist(userId, action);
        return true;
    }

}