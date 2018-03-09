/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.payment.service.adapter;

import edu.bits.mtech.payment.service.bo.AcquirerAuthorizeRequest;
import edu.bits.mtech.payment.service.bo.AcquirerAuthorizeResponse;
import org.springframework.http.ResponseEntity;

/**
 * Acquirer service adapter.
 *
 * @author Tushar Phadke
 */
public interface AcquirerServiceAdapter {

    AcquirerAuthorizeResponse authorize(AcquirerAuthorizeRequest acquirerAuthorizeRequest, boolean update);

    AcquirerAuthorizeResponse cancelAuthorize(String authorizeId);
}
