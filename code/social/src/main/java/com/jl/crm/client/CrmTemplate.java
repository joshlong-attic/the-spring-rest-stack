package com.jl.crm.client;

import org.apache.commons.lang.SystemUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.*;
import org.springframework.http.*;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A client to the RESTful API.
 *
 * @author Josh Long
 */
public class CrmTemplate extends AbstractOAuth2ApiBinding implements CrmOperations, InitializingBean {

	private final Map<String, Object> emptyMap = new ConcurrentHashMap<String, Object>();
	private final File rootFile = SystemUtils.getJavaIoTmpDir();
	private URI apiBaseUri;

	public CrmTemplate(String accessToken, String apiUrl) {

		super(accessToken);
		try {
			this.apiBaseUri = new URI(apiUrl);
			setRequestFactory(ClientHttpRequestFactorySelector.bufferRequests(getRestTemplate().getRequestFactory()));
		}
		catch (Exception e) {
			throw new RuntimeException("could not initialize the " + getClass().getName(), e);
		}
	}

	private static String missingDependency(String dep) {
		return String.format("you must provide a valid '%s'", dep);
	}

	private static Customer unwrapCustomer(Resource<Customer> tResource) {
		Customer customer = tResource.getContent();
		customer.setId(tResource.getId());
		return customer;
	}

	private static User unwrapUser(Resource<User> tResource) {
		User user = tResource.getContent();
		user.setId(tResource.getId());
		return user;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.apiBaseUri, missingDependency("apiBaseUri"));
		Assert.notNull(this.getRestTemplate(), missingDependency(RestTemplate.class.getName()));
	}

	@Override
	public User currentUser() {
		ResponseEntity<UserResource> userResponse = this.getRestTemplate().getForEntity(uriFrom("/user"), UserResource.class);
		UserResource userResource = userResponse.getBody();
		return unwrapUser(userResource);
	}

	@Override
	public Customer createCustomer(String firstName, String lastName, Date signupDate) {

		Customer customer = new Customer(currentUser(), null, firstName, lastName, signupDate);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		// build up a representation of the domain model and transmit it
		// as JSON no need to use the actual objects. this is simpler and more predictable.

		Map<String, Object> mapOfCutomerData = customerMap(customer);
		HttpEntity<Map<String, Object>> customerHttpEntity = new HttpEntity<Map<String, Object>>(mapOfCutomerData, httpHeaders);

		ResponseEntity<?> responseEntity = this.getRestTemplate().postForEntity(uriFrom("/customers"), customerHttpEntity, ResponseEntity.class);
		URI newLocation = responseEntity.getHeaders().getLocation();
		return customer(newLocation);
	}

	private Map<String, Object> customerMap(Customer customer) {
		Map<String, Object> mapOfUserData = null;
		if (customer.getUser() != null){
			mapOfUserData = new HashMap<String, Object>();
			mapOfUserData.put("id", customer.getUser().getDatabaseId());
		}
		Map<String, Object> mapOfCutomerData = new HashMap<String, Object>();
		mapOfCutomerData.put("firstName", customer.getFirstName());
		mapOfCutomerData.put("lastName", customer.getLastName());
		if (customer.getSignupDate() != null){
			// optional
			mapOfCutomerData.put("signupDate", customer.getSignupDate());
		}
		if (mapOfUserData != null){
			mapOfCutomerData.put("user", mapOfUserData);
		}

		return mapOfCutomerData;
	}

	@Override
	public Collection<Customer> loadAllUserCustomers() {
		User currentUser = currentUser();
		Long dbId = currentUser.getDatabaseId();
		URI uri = this.uriFrom("/users/" + dbId + "/customers");
		ResponseEntity<CustomerList> resources = this.getRestTemplate().getForEntity(uri, CustomerList.class);
		Resources<Resource<Customer>> customerResources = resources.getBody();
		Collection<Customer> customerCollection = new ArrayList<>();
		for (Resource<Customer> customerResource : customerResources) {
			customerCollection.add(unwrapCustomer(customerResource));
		}
		return customerCollection;
	}

	@Override
	public void removeCustomer(Long customer) {
		URI uri = uriFrom("/customers/" + Long.toString(customer));
		this.getRestTemplate().delete(uri);
	}

	private Customer customer(URI uri) {
		ResponseEntity<CustomerResource> customerResourceResponseEntity = getRestTemplate().getForEntity(uri, CustomerResource.class);
		Resource <Customer> customerResource = customerResourceResponseEntity.getBody();
		return unwrapCustomer(customerResource);
	}

	@Override
	public Customer loadUserCustomer(Long id) {
		Long currentUser = this.currentUser().getDatabaseId();
		URI uri = uriFrom("/users/" + currentUser + "/customers/" + id);
		return customer(uri);
	}

	@Override
	public URI setUserProfilePhoto(byte[] bytesOfImage, final MediaType mediaType) {
		ByteArrayResource byteArrayResource = new ByteArrayResource(bytesOfImage) {
			@Override
			public String getFilename() {
				return new File(rootFile, "profile-image." + mediaType.getSubtype()).getAbsolutePath();
			}
		};

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.set("file", byteArrayResource);

		return this.getRestTemplate().postForLocation(uriFrom("/users/" + currentUser().getDatabaseId() + "/photo").toString(), parts, ResponseEntity.class);
	}

	@Override
	public Customer updateCustomer(Long id, String firstName, String lastName) {
		Customer customer = this.loadUserCustomer(id);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		// build up a representation of the domain model and transmit it
		// as JSON no need to use the actual objects. this is simpler and more predictable.

		Map<String, Object> mapOfCutomerData = customerMap(customer);
		mapOfCutomerData.put("firstName", firstName);
		mapOfCutomerData.put("lastName", lastName);
		HttpEntity<Map<String, Object>> customerHttpEntity = new HttpEntity<Map<String, Object>>(mapOfCutomerData, httpHeaders);

		URI uri = uriFrom("/customers/" + id);
		this.getRestTemplate().put(uri.toString(), customerHttpEntity, ResponseEntity.class);

		return customer(uri);
	}

	@Override
	public ProfilePhoto getUserProfilePhoto() {
		ResponseEntity<byte[]> profilePhotoData = this.getRestTemplate().getForEntity(uriFrom("/users/" + currentUser().getDatabaseId() + "/photo").toString(), byte[].class);
		MediaType mediaType = profilePhotoData.getHeaders().getContentType();
		return new ProfilePhoto(profilePhotoData.getBody(), mediaType);
	}

	private URI uriFrom(String subUrl) {
		return this.uriFrom(subUrl, this.emptyMap);
	}

	private URI uriFrom(String subUrl, Map<String, ?> params) {
		return UriComponentsBuilder.fromUri(this.apiBaseUri).path(subUrl).buildAndExpand(params).toUri();
	}

	// types that we need simply to lock in the generic information so that
	// it's available at runtime to things like Jackson

	// todo simplify all this url string building with methods that generate the URLs for you
	URI customerUri(long customerId) {
		return uriFrom("/customers/" + customerId);
	}

	static class CustomerList extends Resources<Resource<Customer>> {
	}

	static class UserResource extends Resource<User> {
	}

	static class CustomerResource extends Resource<Customer> {
	}
}
