/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.payment.service.adapter;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.payment.service.bo.AcquirerAuthorizeRequest;
import edu.bits.mtech.payment.service.bo.AcquirerAuthorizeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Acquiere adapter for calling remote service.
 *
 * @author Tushar Phadke
 */
@Service
public class AcquirerServiceAdapterImpl implements AcquirerServiceAdapter {

    private static final Logger logger = Logger.getLogger(AcquirerServiceAdapterImpl.class.getName());

    @Autowired
    private RestTemplate restTemplate;

    private static final String SERVICE_URL = "http://"+ BitsPocConstants.ACQUIRER_SERVICE.toUpperCase()+"/rest/authorize";

    @Override
    public AcquirerAuthorizeResponse authorize(AcquirerAuthorizeRequest acquirerAuthorizeRequest) {
        logger.finest("Calling Acquirer service");
        ResponseEntity<AcquirerAuthorizeResponse> responseEntity = null;
        AcquirerAuthorizeResponse acquirerAuthorizeResponse = null;
        try {
            responseEntity = restTemplate.postForEntity(SERVICE_URL, acquirerAuthorizeRequest, AcquirerAuthorizeResponse.class);

            if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.ACCEPTED) {
                acquirerAuthorizeResponse = responseEntity.getBody();
            } else {
                logger.warning("Response Entity is null or status is not accepted");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to execute remote call to acquirer", e);
        }
        return acquirerAuthorizeResponse;
    }
}
