package com.swp493.ivb.common.mdata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp493.ivb.common.view.Payload;

@RestController
public class ControllerMasterData {

    @Autowired
    private ServiceMasterData masterDataService;

    @GetMapping(value = "/genres")
    public ResponseEntity<Payload<List<DTOGenre>>> getGenres() {
        List<DTOGenre> genres = masterDataService.getGenreList();
        if (genres.size() > 0) {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Payload<List<DTOGenre>>()
                            .success(genres));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Payload<List<DTOGenre>>()
                            .fail(null));
        }
    }

    @GetMapping(value = "/release-types")
    public ResponseEntity<Payload<List<DTOReleaseType>>> getReleaseTypes() {
        List<DTOReleaseType> releaseTypes = masterDataService.getReleaseTypeList();
        if (releaseTypes.size() > 0) {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Payload<List<DTOReleaseType>>()
                            .success(releaseTypes));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Payload<List<DTOReleaseType>>()
                            .fail(null));
        }
    }
}
