package com.swp493.ivb.common.mdata;

import java.util.List;

import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerMasterData {

    

    @Autowired
    private ServiceMasterData masterDataService;

    @GetMapping(value = "/genres")
    public ResponseEntity<?> getGenres() {
        List<DTOGenre> genres = masterDataService.getGenreList();
        if (genres.size() > 0) {
            return Payload.successResponse(genres);
        } else {
            return Payload.failureResponse("Can't get genres list");
        }
    }

    @GetMapping(value = "/release-types")
    public ResponseEntity<?> getReleaseTypes() {
        
            List<DTOReleaseType> releaseTypes = masterDataService.getReleaseTypeList();
            if (releaseTypes.size() > 0) {
                return Payload.successResponse(releaseTypes);
            } else {
                return Payload.failureResponse("Can't get releaseTypes");
            }
        
        
    }
}
