package edu.bits.mtech.payment;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Payment Information controller called from Eureka server.
 *
 * @author Tushar Phadke
 */
@RestController
public class PaymentInformation {


    @RequestMapping(method = RequestMethod.GET, value = "/info", produces = "text/plain")
    public String information() {
        return "Payment Service containing operations related to Payment";
    }
}
