package com.jl.crm.client;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A client to the RESTful API.
 *
 * @author Josh Long
 */
public class CrmTemplate extends AbstractOAuth2ApiBinding implements CrmOperations {

    private final File rootFile = new File(System.getProperty("java.io.tmpdir"));
    private URI apiBaseUri;
    private Map<String, String> mapOfExtensions = new ConcurrentHashMap<String, String>();

    {
        mapOfExtensions.put("jpeg", "jpg");
        mapOfExtensions.put("jpg", "jpg");
        mapOfExtensions.put("gif", "gif");
        mapOfExtensions.put("png", "png");
    }

    private Map<String, MediaType> mapOfMediaTypesToExtensions = new ConcurrentHashMap<String, MediaType>();

    {
        mapOfMediaTypesToExtensions.put("png", MediaType.IMAGE_PNG);
        mapOfMediaTypesToExtensions.put("jpg", MediaType.IMAGE_JPEG);
        mapOfMediaTypesToExtensions.put("gif", MediaType.IMAGE_GIF);
    }

    public CrmTemplate(String accessToken, String apiUrl) {
        super(accessToken);
        try {
            this.apiBaseUri = new URI(apiUrl);

            SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            setRequestFactory(simpleClientHttpRequestFactory);

        } catch (Exception e) {
            throw new RuntimeException("could not initialize the " + CrmTemplate.class.getName(), e);
        }
    }

    private static Customer unwrapCustomer(Resource<Customer> tResource) {
        Customer customer = tResource.getContent();
        customer.setId(tResource.getId());
        return customer;
    }

    private static User unwrapUser(Resource<User> tResource) {
        User user = tResource.getContent();
        user.setId(tResource.getId().getHref());
        return user;
    }

    private static Field field(Class<?> cl, String fieldName) {
        Field field = null;
        try {
            field =  cl.getDeclaredField (fieldName);
            if (!field.isAccessible())
                field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return field;
    }

    @Override
    protected ByteArrayHttpMessageConverter getByteArrayMessageConverter() {
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.IMAGE_JPEG, MediaType.IMAGE_GIF, MediaType.IMAGE_PNG));
        return converter;
    }

    @Override
    public Collection<Customer> search(String token) {
        User currentUser = currentUser();
        Long dbId = currentUser.getDatabaseId();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", (Long.toString(dbId)));
        params.put("q", ("%" + token + "%"));

        UriComponentsBuilder uriToSearch = UriComponentsBuilder.fromUri(this.apiBaseUri).path("/customers/search/search");
        for (String k : params.keySet()) {
            uriToSearch.queryParam(k, params.get(k));
        }

        ResponseEntity<CustomerList> resources = this.getRestTemplate().getForEntity(uriToSearch.build().toUri(), CustomerList.class);
        Resources<Resource<Customer>> customerResources = resources.getBody();
        Collection<Customer> customerCollection = new ArrayList<Customer>();
        for (Resource<Customer> customerResource : customerResources) {
            customerCollection.add(unwrapCustomer(customerResource));
        }
        return customerCollection;
    }

    private Map<String, Object> customerMap(Customer customer) {
        Map<String, Object> mapOfUserData = null;
        if (customer.getUser() != null) {
            mapOfUserData = new HashMap<String, Object>();
            mapOfUserData.put("id", customer.getUser().getDatabaseId());
        }
        Map<String, Object> customerDataMap = new HashMap<String, Object>();
        customerDataMap.put("firstName", customer.getFirstName());
        customerDataMap.put("lastName", customer.getLastName());
        if (customer.getSignupDate() != null) {
            customerDataMap.put("signupDate", customer.getSignupDate());
        }
        if (mapOfUserData != null) {
            customerDataMap.put("user", mapOfUserData);
        }
        return customerDataMap;
    }

    private Customer customer(URI uri) {
        ResponseEntity<CustomerResource> customerResourceResponseEntity = getRestTemplate().getForEntity(uri, CustomerResource.class);
        Resource<Customer> customerResource = customerResourceResponseEntity.getBody();
        return unwrapCustomer(customerResource);
    }

    @Override
    public User currentUser() {
        ResponseEntity<UserResource> userResponse = this.getRestTemplate().getForEntity(uriFrom("/user"), UserResource.class);
        Resource<User> userResource = userResponse.getBody();
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

    @Override
    public Collection<Customer> loadAllUserCustomers() {
        User currentUser = currentUser();
        Long dbId = currentUser.getDatabaseId();
        URI uri = this.uriFrom("/users/" + dbId + "/customers");
        ResponseEntity<CustomerList> resources = this.getRestTemplate().getForEntity(uri, CustomerList.class);
        Resources<Resource<Customer>> customerResources = resources.getBody();
        Collection<Customer> customerCollection = new ArrayList<Customer>();
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

    @Override
    public Customer loadUserCustomer(Long id) {
        Long currentUser = this.currentUser().getDatabaseId();
        URI uri = uriFrom("/users/" + currentUser + "/customers/" + id);
        return customer(uri);
    }

    @Override
    public void setUserProfilePhoto(byte[] bytesOfImage, final MediaType mediaType) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytesOfImage) {
            @Override
            public String getFilename() {
                String ext = mapOfExtensions.get(mediaType.getSubtype());
                return new File(rootFile, "profile-image." + ext).getAbsolutePath();
            }
        };
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        parts.set("file", byteArrayResource);
        String photoUri = uriFrom("/users/" + currentUser().getDatabaseId() + "/photo").toString();
        ResponseEntity<?> responseEntity = this.getRestTemplate().postForEntity(photoUri, parts, ResponseEntity.class);
        HttpStatus.Series series = responseEntity.getStatusCode().series();
        if (!series.equals(HttpStatus.Series.SUCCESSFUL)) {
            throw new RuntimeException("couldn't write the profile photo!");
        }
    }

    @Override
    protected FormHttpMessageConverter getFormMessageConverter() {
        FormHttpMessageConverter formHttpMessageConverter = super.getFormMessageConverter();
        List<HttpMessageConverter<?>> partConverters;
        try {
            // todo fix FieldUtils doesnt exist on Android

            Field partConvertersField =  field(FormHttpMessageConverter.class, "partConverters" );
            partConverters = (List<HttpMessageConverter<?>>) partConvertersField.get(formHttpMessageConverter);
            ResourceHttpMessageConverter remove = null;
            for (HttpMessageConverter<?> hmc : partConverters) {
                if (hmc instanceof ResourceHttpMessageConverter) {
                    remove = (ResourceHttpMessageConverter) hmc;
                }
            }
            if (null != remove) {
                partConverters.remove(remove);
            }

            partConverters.add(new DefaultContentTypeGuessingResourceHttpMessageConverter());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return formHttpMessageConverter;

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
        return this.uriFrom(subUrl, Collections.<String, String>emptyMap());
    }

    private URI uriFrom(String subUrl, Map<String, ?> params) {
        return UriComponentsBuilder.fromUri(this.apiBaseUri).path(subUrl).buildAndExpand(params).toUri();
    }

    public static class CustomerList extends Resources<Resource<Customer>> {
    }

    public static class UserResource extends Resource<User> {
    }

    public static class CustomerResource extends Resource<Customer> {
    }

    public class DefaultContentTypeGuessingResourceHttpMessageConverter extends ResourceHttpMessageConverter {

        @Override
        protected MediaType getDefaultContentType(org.springframework.core.io.Resource resource) {
            try {
                MediaType ifAllElseFails = super.getDefaultContentType(resource);
                String fileName = resource.getFilename();
                int lastPeriod;
                if (fileName != null && (fileName = fileName.toLowerCase()) != null && ((lastPeriod = fileName.lastIndexOf(".")) != -1)) {
                    String ext = fileName.substring(lastPeriod + 1);
                    if (mapOfExtensions.containsKey(ext)) {
                        String canonicalExt = mapOfExtensions.get(ext);
                        return mapOfMediaTypesToExtensions.get(canonicalExt);
                    }
                }
                return ifAllElseFails;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}
