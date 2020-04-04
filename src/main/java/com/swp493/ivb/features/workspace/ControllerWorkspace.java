package com.swp493.ivb.features.workspace;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ControllerWorkspace {
    @Autowired
    ServiceWorkspace workspaceService;
    @PostMapping(value="/stream/count/{type}/{id}")
    public ResponseEntity<?> updateCount(@RequestAttribute EntityUser user,@PathVariable String type,@PathVariable String id) {
        workspaceService.updateCount(user.getId(),type, id);
        return Payload.successResponse("Success");
    }
    
}
