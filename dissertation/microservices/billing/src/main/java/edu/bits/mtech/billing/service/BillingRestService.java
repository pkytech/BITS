/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.service;

import edu.bits.mtech.billing.db.bo.Bill;
import edu.bits.mtech.billing.db.bo.Order;
import edu.bits.mtech.billing.db.bo.Payment;
import edu.bits.mtech.billing.db.repository.BillRepository;
import edu.bits.mtech.billing.service.bo.BillRequest;
import edu.bits.mtech.billing.service.bo.BillResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Billing Rest Service.
 *
 * @author Tushar Phadke
 */
@RestController
public class BillingRestService {

    private static final Logger logger = Logger.getLogger(BillingRestService.class.getName());

    @Autowired
    private BillRepository billRepository;

    @RequestMapping(value = "/rest/bill/{billId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BillResponse> getBill(@PathVariable("billId") String billId) {

        logger.fine("GetBill called with Id: " + billId);

        Bill bill = billRepository.findBillByKey(billId);
        if (bill == null) {
            return ResponseEntity.notFound().build();
        }

        BillResponse response = new BillResponse();
        response.setBillId(billId);
        if (bill.getOrder() != null) {
            response.setOrderId(bill.getOrder().getOrderId());
            response.setOrderStatus(bill.getOrder().getStatus());
        }
        if (bill.getPayment() != null) {
            response.setPaymentId(bill.getPayment().getPaymentId());
            response.setPaymentStatus(bill.getPayment().getStatus());
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/rest/bill", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BillResponse> createBill(@RequestBody
                                                     @NotNull(message = "Bill should not be null")
                                                         @Valid @Validated BillRequest orderRequest) {

        logger.info("BillCreate [POST]: " + orderRequest);
        BillResponse orderResponse = new BillResponse();
        Bill bill = populateBillEntity(orderRequest);

        try {
            billRepository.save(bill);

            orderResponse.setBillId(bill.getBillId());
            if (bill.getOrder() != null) {
                orderResponse.setOrderId(bill.getOrder().getOrderId());
                orderResponse.setOrderStatus(bill.getOrder().getStatus());
            }
            if (bill.getPayment() != null) {
                orderResponse.setPaymentId(bill.getPayment().getPaymentId());
                orderResponse.setPaymentStatus(bill.getPayment().getStatus());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to save Bill", e);
        }
        return ResponseEntity.accepted().body(orderResponse);
    }

    private Bill populateBillEntity(BillRequest billRequest) {

        Bill bill = new Bill();
        bill.setBillId(billRequest.getBillId());

        if (billRequest.getPaymentInformation() != null) {
            Payment payment = new Payment();
            payment.setPaymentId(billRequest.getPaymentInformation().getPaymentId());
            payment.setStatus(billRequest.getPaymentInformation().getStatus());
            payment.setAuthorizeAmount(billRequest.getPaymentInformation().getPaymentAmt());
            payment.setBill(bill);
            bill.setPayment(payment);
        }

        if (billRequest.getOrderId() != null) {
            Order order = new Order();
            order.setOrderId(billRequest.getOrderId());
            order.setStatus(billRequest.getOrderStatus());
            order.setOrderAmount(billRequest.getOrderAmt());
            order.setBill(bill);
            bill.setOrder(order);
        }
        return bill;
    }
}


