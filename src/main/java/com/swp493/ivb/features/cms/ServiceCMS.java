package com.swp493.ivb.features.cms;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.release.DTOReleasePending;
import com.swp493.ivb.common.user.DTOUserPublic;
import com.swp493.ivb.common.view.Paging;

public interface ServiceCMS {
    public Paging<DTOArtistFull> getRequests(String adminId, int offset, int limit);
    public DTOReleasePending getArtistRequest(String artistId, int offset, int limit);
    public boolean responseRequest(String userId, String action);
    public Paging<DTOUserPublic> listUserProfiles(String key, int offset, int limit);
    public void makeCurator(String userId);
}