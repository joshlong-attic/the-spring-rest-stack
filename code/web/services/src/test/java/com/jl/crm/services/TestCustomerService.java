package com.jl.crm.services;

import java.util.Collection;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServiceConfiguration.class)
@Transactional
@TransactionConfiguration
public class TestCustomerService {

	private User joshlong;

	@Inject private CrmService crmService;

	@Before
	public void begin() throws Throwable {
		String joshlongUserName = "joshlong";
		joshlong = crmService.findUserByUsername(joshlongUserName);
		Assert.assertNotNull(joshlong);
	}

	@Test
	public void testCreateUser() throws Throwable {
		Assert.assertNotNull(this.joshlong);
	}

	@Test
	public void testCustomerSearch() throws Throwable {

		Collection<Customer> customerCollection = crmService.loadCustomerAccounts(joshlong.getId());
		Assert.assertEquals("there should be 4 customers for user josh", 4, customerCollection.size());
	}

	@Test
	public void testCustomerSearchForSpecificUsernamePattern() throws Throwable {

		Collection<Customer> customerCollection = crmService.search(joshlong.getId(), "josh");
		Assert.assertEquals("there should be 1 customer with username like 'josh' for user josh", 1,
				customerCollection.size());
	}

	@Test
	public void testCreatingCustomer() throws Throwable {

		Customer janeDoe = this.crmService.addAccount(joshlong.getId(), "Jane", "Doe");
		Assert.assertNotNull(janeDoe);

		Customer johnDoe = this.crmService.addAccount(joshlong.getId(), "John", "Doe");
		Assert.assertNotNull(johnDoe);

		Collection<Customer> customerCollection = crmService.loadCustomerAccounts(joshlong.getId());
		Assert.assertEquals(6, customerCollection.size());
	}
}
