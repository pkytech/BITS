/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.payment.service.adapter;

import edu.bits.mtech.common.BitsPocConstants;
import edu.bits.mtech.payment.service.bo.AcquirerAuthorizeRequest;
import edu.bits.mtech.payment.service.bo.AcquirerAuthorizeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
    public AcquirerAuthorizeResponse authorize(AcquirerAuthorizeRequest acquirerAuthorizeRequest, boolean update) {
        logger.finest("Calling Acquirer service");
        ResponseEntity<AcquirerAuthorizeResponse> responseEntity = null;
        AcquirerAuthorizeResponse acquirerAuthorizeResponse = null;
        try {
            if (update) {
                HttpEntity<AcquirerAuthorizeRequest> entity = new HttpEntity<>(acquirerAuthorizeRequest);
                responseEntity = restTemplate.exchange(SERVICE_URL, HttpMethod.PUT, entity, AcquirerAuthorizeResponse.class);
            } else {
                responseEntity = restTemplate.postForEntity(SERVICE_URL, acquirerAuthorizeRequest, AcquirerAuthorizeResponse.class);
            }

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

    @Override
    public AcquirerAuthorizeResponse cancelAuthorize(String authorizeId) {
        logger.finest("Calling Acquirer cancel service");
        ResponseEntity<AcquirerAuthorizeResponse> responseEntity = null;
        AcquirerAuthorizeResponse acquirerAuthorizeResponse = new AcquirerAuthorizeResponse();
        try {
            HttpEntity entity = new HttpEntity<>(getHttpHeaders());
            responseEntity = restTemplate.exchange(SERVICE_URL+"/cancel/"+authorizeId, HttpMethod.DELETE, entity, AcquirerAuthorizeResponse.class);

            if (responseEntity == null) {
                acquirerAuthorizeResponse = null;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to execute remote call to acquirer", e);
        }

        return acquirerAuthorizeResponse;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
