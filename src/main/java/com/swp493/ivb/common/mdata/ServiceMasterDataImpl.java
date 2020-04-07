package com.swp493.ivb.common.mdata;

import java.util.List;
import java.util.stream.Collectors;

import com.swp493.ivb.common.view.Paging;

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
    public List<DTOReportType> getReportTypeList() {
        ModelMapper mapper = new ModelMapper();
        List<DTOReportType> reportTypeList = masterDataRepo
                .findByType("report")
                .parallelStream()
                .map(genre -> mapper.map(genre, DTOReportType.class))
                .collect(Collectors.toList());

        return reportTypeList;
    }

    @Override
    public Paging<DTOGenre> findGenre(String key, int offset, int limit) {
        int total = masterDataRepo.countByNameIgnoreCaseContainingAndType(key, "genre");
        Paging<DTOGenre> paging = new Paging<>();
        paging.setPageInfo(total, limit, offset);
        ModelMapper mapper = new ModelMapper();
        List<EntityMasterData> list = masterDataRepo.findByNameIgnoreCaseContainingAndType(key, "genre", paging.asPageable());
        paging.setItems(list.stream().map(genre -> mapper.map(genre, DTOGenre.class)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public DTOGenre getGenre(String id) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(masterDataRepo.findByIdAndType(id,"genre").get(), DTOGenre.class);
       
    }

    public DTOReleaseType getReleaseType(String id){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(masterDataRepo.findByIdAndType(id,"release").get(), DTOReleaseType.class);
    }
}
