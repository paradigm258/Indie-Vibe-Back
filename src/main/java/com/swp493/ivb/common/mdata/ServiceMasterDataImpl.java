package com.swp493.ivb.common.mdata;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceMasterDataImpl implements ServiceMasterData {

    @Autowired
    private RepositoryMasterData masterDataRepo;

    @Override
    public List<DTOGenre> getGenreList() {
        ModelMapper mapper = new ModelMapper();
        List<DTOGenre> genreList = masterDataRepo
                .findByType("genre")
                .parallelStream()
                .map(genre -> mapper.map(genre, DTOGenre.class))
                .collect(Collectors.toList());

        return genreList;
    }

    @Override
    public List<DTOReleaseType> getReleaseTypeList() {
        ModelMapper mapper = new ModelMapper();
        List<DTOReleaseType> genreList = masterDataRepo
                .findByType("release")
                .parallelStream()
                .map(genre -> mapper.map(genre, DTOReleaseType.class))
                .collect(Collectors.toList());

        return genreList;
    }

    @Override
    public Optional<EntityMasterData> getReleaseTypeById(String releaseId) {
        return masterDataRepo.findByIdAndType(releaseId, "release");
    }

    @Override
    public Optional<EntityMasterData> getGenreById(String genreId) {
        return masterDataRepo.findByIdAndType(genreId, "genre");
    }
}
