package com.swp493.ivb.features.report;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * ControllerReport
 */
@RestController
public class ControllerReport {

    @Autowired
    ServiceReport reportService;

    @PostMapping(value="/report/{artistId}")
    public ResponseEntity<?> reportArtist(
        @PathVariable String artistId, 
        @RequestAttribute EntityUser user,
        @RequestParam String type,
        @RequestParam String reason) {
        reportService.reportArtist(user.getId(), artistId, type, reason);
        return Payload.successResponse("Thank you! Your report will be reviewed");
    }
    
}