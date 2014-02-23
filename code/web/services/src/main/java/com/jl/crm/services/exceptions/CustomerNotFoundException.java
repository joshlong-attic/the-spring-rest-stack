package com.jl.crm.services.exceptions;

/**
 *
 */
public class CustomerNotFoundException
        extends RuntimeException {

    private long customerId;

    public CustomerNotFoundException(long cid) {
        super("customer#" + cid + " was not found");
        this.customerId = cid;
    }

    public long getCustomerId() {
        return customerId;
    }
}
