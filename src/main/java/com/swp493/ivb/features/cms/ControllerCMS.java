package com.swp493.ivb.features.cms;

import java.util.Optional;

import javax.mail.MessagingException;
import javax.validation.Valid;

import com.swp493.ivb.common.mdata.DTOGenreCreate;
import com.swp493.ivb.common.mdata.DTOGenreUpdate;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ControllerCMS {
    @Autowired
    ServiceCMS cmsService;

    @GetMapping(value = "/cms/requests")
    public ResponseEntity<?> getRequests(@RequestAttribute EntityUser user,
            @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(cmsService.getRequests(user.getId(), offset, limit));
    }

    @GetMapping(value = "/cms/request/{userId}")
    public ResponseEntity<?> getArtistRequest(@RequestAttribute EntityUser user, @PathVariable String userId,
            @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(cmsService.getArtistRequest(userId, offset, limit));
    }

    @PostMapping(value = "/cms/request/{userId}")
    public ResponseEntity<?> responseRequest(@PathVariable String userId, @RequestParam String action)
            throws MessagingException {
        if (cmsService.responseRequest(userId, action)) {
            return Payload.successResponse("Successfully " + action + " request");
        } else {
            return Payload.failureResponse("Failed to " + action + " request");
        }
    }

    @GetMapping(value = "/cms/profiles/{key}")
    public ResponseEntity<?> getUserProfiles(@PathVariable String key, @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(cmsService.listUserProfiles(key, offset, limit));
    }

    @PutMapping(value = "/cms/delegate")
    public ResponseEntity<?> makeCurator(@RequestParam String userId) {
        cmsService.makeCurator(userId);
        return Payload.successResponse("Success");
    }

    @PutMapping(value = "/cms/undelegate")
    public ResponseEntity<?> unmakeCurator(@RequestParam String userId) {
        cmsService.unmakeCurator(userId);
        return Payload.successResponse("Success");
    }

    @GetMapping(value = { "/cms/reports", "/cms/reports/{type}" })
    public ResponseEntity<?> getReports(@PathVariable(required = false) String type,
            @RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(
                cmsService.findReport(Optional.ofNullable(type), Optional.ofNullable(status), offset, limit));
    }

    @PutMapping(value = "/cms/reports/{id}")
    public ResponseEntity<?> reviewReport(@PathVariable String id, @RequestParam String action)
            throws MessagingException {
        cmsService.reviewReport(id, action);
        return Payload.successResponse("Successfully "+action);
    }
    
    @GetMapping(value="/cms/stream/yearly")
    public ResponseEntity<?> yearlyStats(@RequestParam int start, @RequestParam int end) {
        return Payload.successResponse(cmsService.yearlySumStream(start, end));
    }
    
    @GetMapping(value="/cms/stream/monthly")
    public ResponseEntity<?> monthlyStats(@RequestParam int year) {
        return Payload.successResponse(cmsService.monthlySumStream(year));
    }

    @GetMapping(value="/cms/revenue/yearly")
    public ResponseEntity<?> yearlyRevenue(@RequestParam int start, @RequestParam int end) {
        return Payload.successResponse(cmsService.yearlySumRevenue(start, end));
    }
    
    @GetMapping(value="/cms/revenue/monthly")
    public ResponseEntity<?> monthlyRevenue(@RequestParam int year) {
        return Payload.successResponse(cmsService.monthlySumRevenue(year));
    }

    @GetMapping(value="/cms/genres/{id}")
    public ResponseEntity<?> getGenre(@PathVariable String id) {
        return Payload.successResponse(cmsService.getGenre(id));
    }

    @PostMapping(value="/cms/genres")
    public ResponseEntity<?> addGenre(@Valid DTOGenreCreate data) {
        EntityMasterData genre = cmsService.addGenre(data);
        return Payload.successResponse(genre.getId());
    }
    
    @DeleteMapping(value = "/cms/genres/{id}")
    public ResponseEntity<?> deleteGenres(@PathVariable String id){
        cmsService.deleteGenre(id);
        return Payload.successResponse("Success");
    }
    @PutMapping(value="/cms/genres/{id}")
    public ResponseEntity<?> updateGenre(@PathVariable String id, @Valid DTOGenreUpdate data) {
        cmsService.updateGenre(id, data);
        return Payload.successResponse("Success");
    }
}