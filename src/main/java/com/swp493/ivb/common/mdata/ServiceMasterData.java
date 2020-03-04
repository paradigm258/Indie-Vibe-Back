package com.swp493.ivb.common.mdata;

import java.util.List;
import java.util.Optional;

public interface ServiceMasterData {

    List<DTOGenre> getGenreList();

    List<DTOReleaseType> getReleaseTypeList();

    Optional<EntityMasterData> getReleaseTypeById(String id);

    Optional<EntityMasterData> getGenreById(String id);
}
