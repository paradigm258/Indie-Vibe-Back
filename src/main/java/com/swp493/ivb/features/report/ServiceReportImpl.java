package com.swp493.ivb.features.report;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.swp493.ivb.common.artist.EntityArtist;
import com.swp493.ivb.common.artist.RepositoryArtist;
import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.DTOReportType;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.mdata.RepositoryMasterData;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.common.view.Paging;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServiceReportImpl implements ServiceReport {

    @Autowired
    RepositoryReport reportRepo;

    @Autowired
    RepositoryMasterData masterDataRepo;

    @Autowired
    RepositoryUser userRepo;

    @Autowired
    RepositoryArtist artistRepo;

    @Autowired
    ServiceUser userService;

    @Autowired
    ServiceArtist artistSerice;

    @Override
    public void reportArtist(String userId, String artistId, String type, String reason) {
        EntityReport report = new EntityReport();
        EntityUser reporter = userRepo.findById(userId).get();
        EntityArtist artist = artistRepo.findById(artistId).get();
        EntityMasterData reportType = masterDataRepo.findById(type).get();
        report.setType(reportType);
        report.setDate(new Date());
        report.setReason(reason);
        report.setReporter(reporter);
        report.setArtist(artist);
        reportRepo.save(report);
    }

    @Override
    public void reviewReport(String id, String action) {
        if(!action.equals("proceed") && !action.equals("reject")) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid action");
        EntityReport report = reportRepo.getOne(id);
        report.setStatus(action+"ed");
        reportRepo.save(report);
    }

    @Override
    public Paging<DTOReport> findReport(Optional<String> type, int offset, int limit) {
        int total;
        List<EntityReport> reports;
        Paging<DTOReport> paging = new Paging<>();
        if(type.isPresent()){
            EntityMasterData reportType = masterDataRepo.findById(type.get()).get();
            total = reportRepo.countByType(reportType);
            paging.setPageInfo(total, limit, offset);
            reports = reportRepo.findByType(reportType, paging.asPageable());
        }else{
            total = (int)reportRepo.count();
            paging.setPageInfo(total, limit, offset);
            reports = reportRepo.findAll(paging.asPageable()).getContent();
        }
        paging.setItems(reports.stream().map(r -> getReport(r)).collect(Collectors.toList()));
        return paging;
    }

    @Override
    public DTOReport getReport(String id) {
        EntityReport report = reportRepo.findById(id).get();
        return getReport(report);
    }

    public DTOReport getReport(EntityReport report){
        ModelMapper mapper = new ModelMapper();
        DTOReport dto = mapper.map(report, DTOReport.class);
        dto.setType(mapper.map(report.getType(), DTOReportType.class));
        String id = report.getReporter().getId();
        dto.setReporter(userService.getUserPublic(id,id));
        id = report.getArtist().getId();
        dto.setArtist(artistSerice.getArtistFull(id, id));
        return dto;
    }

}