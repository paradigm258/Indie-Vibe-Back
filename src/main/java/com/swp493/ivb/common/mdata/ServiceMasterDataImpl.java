package com.swp493.ivb.common.mdata;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.swp493.ivb.common.view.Paging;
import com.swp493.ivb.config.AWSConfig;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServiceMasterDataImpl implements ServiceMasterData {

    @Autowired
    private RepositoryMasterData masterDataRepo;

    @Autowired
    private AmazonS3 s3;

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

    @Override
    public EntityMasterData addGenre(DTOGenreCreate data) {
        EntityMasterData genre = new EntityMasterData();
        genre.setType("genre");
        genre.setName(data.getName());
        if(StringUtils.hasText(data.getDescription())){
            genre.setDescription(data.getDescription());
        }
        genre = masterDataRepo.save(genre);
        MultipartFile thumbnail = data.getThumbnail();
        if (thumbnail != null) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(thumbnail.getSize());
            String key = "genre/"+genre.getId();
            try {
                s3.putObject(new PutObjectRequest(AWSConfig.BUCKET_NAME, key, thumbnail.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                genre.setThumbnail(AWSConfig.BUCKET_URL + key);
            } catch (IOException e) {
                throw new RuntimeException("Error getting input stream for thumbnail", e);
            }
        }
        return masterDataRepo.save(genre);
    }

    @Override
    public void deleteGenre(String id) {
        EntityMasterData genre = masterDataRepo.findByIdAndType(id, "genre").get();
        masterDataRepo.delete(genre);
        s3.deleteObject(AWSConfig.BUCKET_NAME, "genres/"+genre.getId());
    }

    @Override
    public void updateGenre(String id, DTOGenreUpdate data) {
        EntityMasterData genre = masterDataRepo.findByIdAndType(id, "genre").get();
        if(StringUtils.hasText(data.getName())){
            genre.setName(data.getName());
        }
        if(StringUtils.hasText(data.getDescription())){
            genre.setDescription(data.getDescription());
        }
        MultipartFile thumbnail = data.getThumbnail();
        if (thumbnail != null) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(thumbnail.getSize());
            String key = "genres/"+genre.getId();
            try {
                s3.putObject(new PutObjectRequest(AWSConfig.BUCKET_NAME, key, thumbnail.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                genre.setThumbnail(AWSConfig.BUCKET_URL + key);
            } catch (IOException e) {
                throw new RuntimeException("Error getting input stream for thumbnail", e);
            }
        }
        masterDataRepo.save(genre);

    }
}
