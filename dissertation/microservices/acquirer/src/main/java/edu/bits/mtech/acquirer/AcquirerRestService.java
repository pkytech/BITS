/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.acquirer;

import edu.bits.mtech.acquirer.bo.AuthorizeRequest;
import edu.bits.mtech.acquirer.bo.AuthorizeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Rest controller for Acquirer. This is just a service wihout any logic
 * and is out of scope of POC.
 *
 * @author Tushar Phadke
 */
@RestController
public class AcquirerRestService {

    private static final Logger logger = Logger.getLogger(AcquirerRestService.class.getName());

    /**
     * Authorize from acquirer.
     *
     * @param authorizeRequest authorize request
     * @return a status of authorization
     */
    @RequestMapping(method = RequestMethod.POST, value = "/rest/authorize", consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<AuthorizeResponse> authorize(@RequestBody @Valid @Validated @NotNull AuthorizeRequest authorizeRequest) {

        AuthorizeResponse authorizeResponse = new AuthorizeResponse();
        authorizeResponse.setAuthorizeId(UUID.randomUUID().toString());
        authorizeResponse.setAuthorizeAmount(authorizeRequest.getAuthorizeAmount());

        logger.info("Acquirer Authorize Successful: " + authorizeResponse);

        return ResponseEntity.accepted().body(authorizeResponse);
    }
}
