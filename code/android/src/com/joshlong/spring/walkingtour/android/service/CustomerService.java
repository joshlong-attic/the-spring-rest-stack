package com.joshlong.spring.walkingtour.android.service;

import com.joshlong.spring.walkingtour.android.async.AsyncCallback;
import com.joshlong.spring.walkingtour.android.model.Customer;

import java.util.List;

// a client side representation of the server side interface 
public interface CustomerService {
    void updateCustomer(long id, String fn, String ln , AsyncCallback<Customer> asyncCallback);

    void getCustomerById(long id, AsyncCallback<Customer> asyncCallback);

    void createCustomer(String fn, String ln, AsyncCallback<Customer> asyncCallback);

    void search(String newString, AsyncCallback<List<Customer>> asyncUiCallback);

}
