package com.jl.crm.client;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.*;
import org.springframework.http.MediaType;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.*;


/**
 * TODO figure out the best way to integration test this.
 *
 * @author Josh Long
 */
//@RunWith (SpringJUnit4ClassRunner.class)
//@ContextConfiguration
public class TestCrmClientTemplate {

	private static String URI = "http://127.0.0.1:8080";
	private static long USER_ID = 21;
	private Resource resource = new ClassPathResource("/s2-logo.jpg");
	@Inject
	private CrmOperations crmOperations;

//	@Test                   // todo fix this
	public void testCreatingCustomerRecords() throws Throwable {
		User user = crmOperations.currentUser();
		String fn = "Michelle", ln = "Obama";
		Customer customer = crmOperations.createCustomer(fn, ln, new Date());
		assertEquals(customer.getFirstName(), fn);
		assertEquals(customer.getLastName(), ln);
	}


	public void testDeletingACustomerRecord() throws Throwable {
		crmOperations.removeCustomer(28L);
	}
//Throwable
//	@Test
	public void testLoadingAllUserCustomers() throws Throwable {
		assertTrue(crmOperations.loadAllUserCustomers().size() != 0);
	}


//	@Before
	public void begin () throws Throwable {
		assertTrue("the resource classpath:/s2-logo.png needs to exist for this to work.", resource.exists());
	}

	//@Test
	public void testUpdatingCustomer() throws Throwable {
		Long customerIdToUpdate = 26L;
		User user = crmOperations.currentUser();
		Customer customer = crmOperations.loadUserCustomer(customerIdToUpdate);
		String oldFn = customer.getFirstName(), oldLn = customer.getLastName();
		Customer newCustomer =
				crmOperations.updateCustomer(customerIdToUpdate, oldFn + "_updated", oldFn + "_updated");

		assertNotEquals(newCustomer.getFirstName(), oldFn);
		assertNotEquals(newCustomer.getLastName(), oldLn);
		assertEquals(newCustomer.getDatabaseId(), customer.getDatabaseId());

	}

	//@Test
	public void testLoadCustomer() throws Throwable {
		Long customerRecordId = 26L;
		User user = crmOperations.currentUser();
		Customer customer = crmOperations.loadUserCustomer(customerRecordId);
		assertEquals(customer.getDatabaseId(), customerRecordId);
	}

	//	@Test
	public void testLoadUser() {
		User theCurrentUserProfile = crmOperations.currentUser();
		assertEquals(theCurrentUserProfile.getDatabaseId(), (Long) USER_ID);
	}

	//@Test
	public void testObtainingUserProfilePhoto() throws Throwable {
		InputStream inputStream = resource.getInputStream();
		MediaType mediaType = MediaType.IMAGE_JPEG;
		byte[] localResourceBytes = IOUtils.toByteArray(inputStream);
		crmOperations.setUserProfilePhoto(localResourceBytes, mediaType);
		ProfilePhoto profileData = crmOperations.getUserProfilePhoto();
		byte[] bytesFromImage = profileData.getBytes();
		assertEquals(bytesFromImage.length, localResourceBytes.length);
		assertEquals(profileData.getMediaType().getSubtype(), mediaType.getSubtype());
	}

	/*@Configuration
	static class ClientConfiguration {
		@Bean
		CrmOperations crmClientOperations(RestTemplate restTemplate) throws Exception {
			return new CrmTemplate(URI, USER_ID, restTemplate);
		}

		// add the defaults but skip JAXB since we don't need
		// XML since we know our service produces more efficient JSON
		@Bean
		RestTemplate restTemplate() {
			RestTemplate rt = new RestTemplate();
			List<HttpMessageConverter<?>> mcs = new ArrayList<HttpMessageConverter<?>>();
			mcs.add(new ByteArrayHttpMessageConverter());
			mcs.add(new StringHttpMessageConverter());
			mcs.add(new ResourceHttpMessageConverter());
			mcs.add(new SourceHttpMessageConverter());
			mcs.add(new AllEncompassingFormHttpMessageConverter());
			mcs.add(new MappingJackson2HttpMessageConverter());
			rt.getMessageConverters().clear();
			rt.getMessageConverters().addAll(mcs);
			return rt;
		}

	}*/
}
