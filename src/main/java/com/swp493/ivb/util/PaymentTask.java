package com.swp493.ivb.util;

import com.swp493.ivb.common.user.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * SchedulePayment
 */
@Component
public class PaymentTask {

    @Autowired
    ServiceUser userService;

    @Scheduled(cron = "0 0 0 * * ?")
    void updatePayment(){
        userService.updatePlan();
    }
    
}