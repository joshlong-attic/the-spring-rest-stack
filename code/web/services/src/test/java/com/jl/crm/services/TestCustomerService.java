package com.jl.crm.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestCustomerService.TestCustomerServiceConfiguration.class)
@Transactional
@TransactionConfiguration
public class TestCustomerService {


    @Configuration
    @EnableAutoConfiguration
    @Import(ServiceConfiguration.class)
    static class TestCustomerServiceConfiguration {
    }


    User joshlong;

    @Autowired
    CrmService crmService;

    @Autowired
    CustomerRepository customerRepository;

    @Before
    public void begin() throws Throwable {
        String joshlongUserName = "joshlong";
        joshlong = crmService.findUserByUsername(joshlongUserName);
        if (null == joshlong) {
            joshlong = crmService.createUser(joshlongUserName, "cowbell", "josh", "Long");
        }
        Assert.assertNotNull(joshlong);

        Collection<Customer> customersSet = crmService.loadCustomerAccounts(this.joshlong.getId());
        int sizeOfCustomersForUser = customersSet.size();
        Assert.assertTrue(sizeOfCustomersForUser > 0);
        for (Customer customer : customersSet) {
            crmService.removeCustomer(this.joshlong.getId(), customer.getId());
            sizeOfCustomersForUser = sizeOfCustomersForUser - 1;
            Assert.assertEquals(crmService.loadCustomerAccounts(this.joshlong.getId()).size(), sizeOfCustomersForUser);
        }
    }

    @Test
    public void testCreateUser() throws Throwable {
        Assert.assertNotNull(this.joshlong);
    }

    @Test
    public void testCustomerSearch() throws Throwable {
        long joshlongUserId = this.joshlong.getId();

        crmService.addCustomer(joshlongUserId, "josh", "long");

        Collection<Customer> customerCollection = crmService.search(joshlongUserId, "josh");
        Assert.assertTrue("there should be at least", customerCollection.size() > 0);
    }

    @Test
    public void testCreatingCustomer() throws Throwable {

        long joshlongUserId = this.joshlong.getId();

        Collection<Customer> customerCollection;

        Customer janeDoe = this.crmService.addCustomer(joshlongUserId, "Jane", "Doe");
        Assert.assertNotNull(janeDoe);

        Customer johnDoe = this.crmService.addCustomer(joshlongUserId, "John", "Doe");
        Assert.assertNotNull(johnDoe);

        customerCollection = crmService.loadCustomerAccounts(this.joshlong.getId());
        Assert.assertTrue(customerCollection.size() >= 2);

    }
}
