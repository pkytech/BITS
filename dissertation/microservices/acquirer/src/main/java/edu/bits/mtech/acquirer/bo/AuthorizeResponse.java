/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.acquirer.bo;

public class AuthorizeResponse {

    private String authorizeId;
    private double authorizeAmount;
    private String errorMessage;

    public String getAuthorizeId() {
        return authorizeId;
    }

    public void setAuthorizeId(String authorizeId) {
        this.authorizeId = authorizeId;
    }

    public double getAuthorizeAmount() {
        return authorizeAmount;
    }

    public void setAuthorizeAmount(double authorizeAmount) {
        this.authorizeAmount = authorizeAmount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "AuthorizeResponse{" +
                "authorizeId='" + authorizeId + '\'' +
                ", authorizeAmount=" + authorizeAmount +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
