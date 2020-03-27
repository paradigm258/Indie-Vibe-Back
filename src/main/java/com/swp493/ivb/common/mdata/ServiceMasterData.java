package com.swp493.ivb.common.mdata;

import java.util.List;

import com.swp493.ivb.common.view.Paging;

public interface ServiceMasterData {

    List<DTOGenre> getGenreList();
    DTOGenre getGenre(String id);
    List<DTOReleaseType> getReleaseTypeList();
    Paging<DTOGenre> findGenre(String key, int offset, int limit);
}
