package com.jl.crm.services;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (classes = ServiceConfiguration.class)
@Transactional
@TransactionConfiguration
public class TestCustomerService {

	private CrmService crmService;
	private User joshlong;

	@Inject
	public void setCrmService(CrmService crmService) {
		this.crmService = crmService;
	}

	@Before
	public void begin() throws Throwable {
		String joshlongUserName = "joshlong";
		joshlong = crmService.findUserByUsername(joshlongUserName);
		if (null == joshlong){
			joshlong = crmService.createUser(joshlongUserName, "cowbell", "josh", "Long");
		}
		Assert.assertNotNull(joshlong);

		Collection<Customer> customersSet = crmService.loadCustomerAccounts(this.joshlong.getId());
		int sizeOfCustomersForUser = customersSet.size();
		for (Customer customer : customersSet) {
			crmService.removeAccount(this.joshlong.getId(), customer.getId());
			sizeOfCustomersForUser = sizeOfCustomersForUser - 1;
			Assert.assertEquals(crmService.loadCustomerAccounts(this.joshlong.getId()).size(), sizeOfCustomersForUser);
		}
	}

	@Test
	public void testCreateUser() throws Throwable {
		Assert.assertNotNull(this.joshlong);
	}

	@Test
	public void testCreatingCustomer() throws Throwable {

		long joshlongUserId = this.joshlong.getId();

		Collection<Customer> customerCollection;

		Customer janeDoe = this.crmService.addAccount(joshlongUserId, "Jane", "Doe");
		Assert.assertNotNull(janeDoe);

		Customer johnDoe = this.crmService.addAccount(joshlongUserId, "John", "Doe");
		Assert.assertNotNull(johnDoe);

		customerCollection = crmService.loadCustomerAccounts(this.joshlong.getId());
		Assert.assertEquals(customerCollection.size(), 2);

	}
}
