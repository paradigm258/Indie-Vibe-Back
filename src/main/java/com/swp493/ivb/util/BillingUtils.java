package com.swp493.ivb.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.param.PlanListParams;
import com.swp493.ivb.common.user.EntityUser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Billing
 */
@Component
public class BillingUtils {

    private final String PLAN_MONTHLY = "plan_H0bEwXlH8JdG8l";

//    @Value(value = "${STRIPE_KEY}")
//    String key;

    public BillingUtils(@Value(value = "${STRIPE_KEY}") final String key) {
        Stripe.apiKey = key;
    }

    public Subscription createSubscription(String stripeToken, EntityUser user) throws StripeException {
        LocalDate date = LocalDate.now();
        List<Object> items = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("plan", PLAN_MONTHLY);
        items.add(item1);
        Map<String, Object> params = new HashMap<>();
        params.put("billing_cycle_anchor", date.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond());
        params.put("customer", createCustomer(stripeToken, user));
        params.put("items", items);
        user.setPlanDue(Date.from(date.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return Subscription.create(params);

    }

    public String createCustomer(String stripeToken, EntityUser user) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("email", user.getEmail());
        params.put("name", user.getId());
        params.put("source", stripeToken);
        Customer customer = Customer.create(params);
        return customer.getId();
    }

    public Charge createCharge(String type, String stripeToken, EntityUser user) throws StripeException {
        LocalDate date = LocalDate.now();
        Map<String, Object> chargeParams = new HashMap<>();
        int amount = 0;
        switch (type) {
            case "week":
                date = date.plusWeeks(1);
                amount = 10000;
                break;
            case "month":
                date = date.plusMonths(1);
                amount = 49000;
                break;
            case "month3":
                date = date.plusMonths(3);
                amount = 135000;
                break;
            case "month6":
                date.plusMonths(6);
                amount = 289000;
                break;
            case "year":
                date = date.plusYears(1);
                amount = 490000;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        user.setPlanDue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        chargeParams.put("amount", amount);
        chargeParams.put("currency", "vnd");
        chargeParams.put("source", stripeToken);
        return Charge.create(chargeParams);

    }

    public String checkSubscriptionStatus(String subId) throws StripeException {
        return Subscription.retrieve(subId).getStatus();
    }

    public String checkChargeStatus(String chargeId) throws StripeException {
        return Charge.retrieve(chargeId).getStatus();
    }
}