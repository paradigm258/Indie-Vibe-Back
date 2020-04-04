package com.swp493.ivb.features.cms;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class ControllerCMS {
    @Autowired
    ServiceCMS cmsService;

    @GetMapping(value="/cms/requests")
    public ResponseEntity<?> getRequests(@RequestAttribute EntityUser user, @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(cmsService.getRequests(user.getId(), offset, limit));
    }

    @GetMapping(value="/cms/request/{userId}")
    public ResponseEntity<?> getArtistRequest(
    @RequestAttribute EntityUser user, 
    @PathVariable String userId, 
    @RequestParam(defaultValue = "0") int offset, 
    @RequestParam(defaultValue = "20") int limit) {
        return Payload.successResponse(cmsService.getArtistRequest(userId,offset, limit));
    }

    @PostMapping(value="/cms/request/{userId}")
    public ResponseEntity<?> responseRequest(@PathVariable String userId,@RequestParam String action ) {
        if(cmsService.responseRequest(userId, action)){
            return Payload.successResponse("Successfully "+action+" request");
        }else {
            return Payload.failureResponse("Failed to "+action+" request");
        }
    }
    
    
    
}