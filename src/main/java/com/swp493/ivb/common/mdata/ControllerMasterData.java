package com.swp493.ivb.common.mdata;

import java.util.List;

import com.swp493.ivb.common.view.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerMasterData {

    private static Logger log = LoggerFactory.getLogger(ControllerMasterData.class);

    @Autowired
    private ServiceMasterData masterDataService;

    @GetMapping(value = "/genres")
    public ResponseEntity<?> getGenres() {
        try {
            List<DTOGenre> genres = masterDataService.getGenreList();
            if (genres.size() > 0) {
                return Payload.successResponse(genres);
            } else {
                return Payload.failureResponse("Can't get genres list");
            }
        } catch (Exception e) {
            log.error("Error getting genres", e);
            return Payload.internalError();
        }
    }

    @GetMapping(value = "/release-types")
    public ResponseEntity<?> getReleaseTypes() {
        try {
            List<DTOReleaseType> releaseTypes = masterDataService.getReleaseTypeList();
            if (releaseTypes.size() > 0) {
                return Payload.successResponse(releaseTypes);
            } else {
                return Payload.failureResponse("Can't get releaseTypes");
            }
        } catch (Exception e) {
            log.error("Error getting release types", e);
            return Payload.internalError();
        }
        
    }
}
