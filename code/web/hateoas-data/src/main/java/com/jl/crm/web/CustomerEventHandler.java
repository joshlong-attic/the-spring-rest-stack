package com.jl.crm.web;

import com.jl.crm.services.Customer;
import com.jl.crm.services.CustomerWriteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class CustomerEventHandler extends AbstractRepositoryEventListener<Customer> {

    private Log logger = LogFactory.getLog(getClass());

    @Override
    protected void onBeforeCreate(Customer customer) {


        if (StringUtils.isEmpty(customer.getFirstName())
                || StringUtils.isEmpty(customer.getLastName())
                || customer.getUser() == null) {
            throw new CustomerWriteException(customer, new RuntimeException(
                    "you must specify a 'firstName' and "
                            + "a 'lastName' and a valid user reference."));
        }
        if (customer.getSignupDate() == null) {
            customer.setSignupDate(new java.util.Date());
        }
        logger.debug("handling before create for " + customer.toString());
    }

    @Override
    protected void onAfterSave(Customer entity) {
        logger.debug("saved customer #" + entity.getId());
    }

    @Override
    protected void onAfterDelete(Customer entity) {
        logger.debug("deleted customer #" + entity.getId());
    }

}
