/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.service.adapter;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.order.service.bo.BillRequest;
import edu.bits.mtech.order.service.bo.BillResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bill adapter implementation
 */
@Service
public class BillAdapterImpl implements BillAdapter {

    private static final String SERVICE_URL = "http://" + BitsPocConstants.BILLING_SERVICE.toUpperCase() + "/rest/bill";

    private static final Logger logger = Logger.getLogger(BillAdapterImpl.class.getName());

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean createBill(BillRequest billRequest) {

        try {
            ResponseEntity<BillResponse> responseEntity =
                    restTemplate.postForEntity(SERVICE_URL, billRequest, BillResponse.class);

            return  (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to call billing service", e);
        }
        return false;
    }
}
