package com.joshlong.spring.walkingtour.android.model;

import java.util.*;

/**
 * wrapper object for a collection of {@link Customer} entities
 */

public class CustomerList {

    private List<Customer> customers = new ArrayList<Customer>();

    /**
     * no-op for serialization
     */
    public CustomerList() {
    }

    public CustomerList(Collection<Customer> c) {
        setCustomers(c);
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Collection<Customer> c) {
        if (this.customers != null) {
            this.customers.clear();
        } else {
            this.customers = new ArrayList<Customer>();
        }

        if (c != null) {
            this.customers.addAll(c);
        }

    }

}
