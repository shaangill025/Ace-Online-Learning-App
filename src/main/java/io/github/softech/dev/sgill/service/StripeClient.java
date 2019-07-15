package io.github.softech.dev.sgill.service;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import io.github.softech.dev.sgill.web.rest.CartResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.util.HashMap;
import java.util.Map;

@Component
public class StripeClient {
    private final Logger log = LoggerFactory.getLogger(CartResource.class);

    @Autowired
    StripeClient() {
        Stripe.apiKey = "sk_test_pmPOakMWYLUuoWDzMo4RJ5rf00YxjAptCb";
    }

    public Customer createCustomer(String token, String email) throws Exception {
        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("email", email);
        customerParams.put("source", token);
        return Customer.create(customerParams);
    }

    private Customer getCustomer(String id) throws Exception {
        return Customer.retrieve(id);
    }

    public Charge chargeNewCard(String token, double amount, String currency) throws Exception {
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", (int)(amount));
        // chargeParams.put("amount", (int)(amount * 100));
        chargeParams.put("currency", currency);
        chargeParams.put("source", token);
        Charge reqdCharge =  Charge.create(chargeParams);
        //return reqdCharge.getStatus();
        // JSON reqdJson = (JSON) reqdCharge.toJson();
        //return reqdCharge.toJson();
        return reqdCharge;
    }

    public Charge chargeCustomerCard(String customerId, double amount) throws Exception {
        String sourceCard = getCustomer(customerId).getDefaultSource();
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", amount);
        chargeParams.put("currency", "USD");
        chargeParams.put("customer", customerId);
        chargeParams.put("source", sourceCard);
        Charge charge = Charge.create(chargeParams);
        return charge;
    }
}
