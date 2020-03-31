package com.swp493.ivb.common.user;

import javax.validation.Valid;

import com.swp493.ivb.common.view.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;





@RestController
public class ControllerUser {

    @Autowired
    private ServiceUser userService;

    @GetMapping("/library/{id}/profile")
    public ResponseEntity<?> getSimple(@PathVariable String id, @RequestAttribute EntityUser user) {   
        return Payload.successResponse(userService.getUserPublic(id,user.getId()));
    }

    @PostMapping(value="/users/{userId}")
    public ResponseEntity<?> actionUser(@PathVariable String userId,@RequestAttribute EntityUser user,@RequestParam String action) {       
            switch (action) {
                case "favorite":
                    userService.followUser(user.getId(), userId);
                    break;
                case "unfavorite":
                    userService.unfollowUser(user.getId(), userId);
                    break;
                default:
                    break;
            }
            return Payload.successResponse("User "+action+"ed");
        
    }
    
    @PutMapping(value="/account")
    public ResponseEntity<?> updateAccount(@RequestAttribute EntityUser user, @Valid DTOUserUpdate update, BindingResult result) {
        if(result.hasErrors()){
            FieldError error = result.getFieldError();
            return Payload.failureResponse(error.getDefaultMessage() + " is invalid");
        }
        if(userService.userUpdate(update, user.getId())){
            return Payload.successResponse("Update successfully");
        }else{
            return Payload.failureResponse("Update failed");
        }
    }

    @GetMapping(value="/account")
    public ResponseEntity<?> getAccount(@RequestAttribute EntityUser user) {
        return Payload.successResponse(userService.getUserPrivate(user.getId()));
    }

    @PutMapping(value="/account/password")
    public ResponseEntity<?> updatePassword(@RequestAttribute EntityUser user,
        @Valid DTOUpdatePassword updatePassword, BindingResult result) {
        if (result.hasErrors()) {
            FieldError error = result.getFieldError();
            return Payload.failureResponse(error.getDefaultMessage() + " is invalid");
        }
        
        if(!updatePassword.getNewPwd().equals(updatePassword.getCfNewPwd())) return Payload.failureResponse("Password not match");
        
        if(userService.passwordUpdate(updatePassword.getPwd(), updatePassword.getNewPwd(), user.getId()))
            return Payload.successResponse("Password changed");

        return Payload.failureResponse("Wrong password");
    }
    
    @PostMapping(value="/purchase/monthly")
    public ResponseEntity<?> purchaseMonthly(@RequestAttribute EntityUser user, @RequestParam String stripeToken){
        String resp = userService.purchaseMonthly(stripeToken, user.getId());
        if(resp != null)
            return Payload.successResponse(resp);
        else
            return Payload.failureResponse("Payment failed");
    }

    @PostMapping(value="/purchase/fixed/{type}")
    public ResponseEntity<?> purchaseFixed(@RequestAttribute EntityUser user, @RequestParam String stripeToken, @PathVariable String type){
        String resp = userService.purchaseFixed(type, stripeToken, user.getId());
        if(resp!=null)
            return Payload.successResponse(resp);
        else
            return Payload.failureResponse("Payment failed");
    }
    
}
