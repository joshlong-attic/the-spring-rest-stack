package com.jl.crm.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.util.Assert;
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

    private Map<String, String> mapOfExtensions =
            new ConcurrentHashMap<String, String>() {
                {
                    put("jpeg", "jpg");
                    put("jpg", "jpg");
                    put("gif", "gif");
                    put("png", "png");
                }
            };

    private Map<String, MediaType> mapOfMediaTypesToExtensions =
            new ConcurrentHashMap<String, MediaType>() {
                {
                    put("png", MediaType.IMAGE_PNG);
                    put("jpg", MediaType.IMAGE_JPEG);
                    put("gif", MediaType.IMAGE_GIF);
                }
            };

    public CrmTemplate(String accessToken, String apiUrl) {
        super(accessToken);
        try {
            this.apiBaseUri = new URI(apiUrl);

            SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            setRequestFactory(simpleClientHttpRequestFactory);

        } catch (Exception e) {
            throw new RuntimeException(
                    "could not initialize the " +
                            CrmTemplate.class.getName(), e);
        }
    }

    @Override
    public Customer loadUserCustomer(Long id) {
        Long currentUser = this.currentUser().getId();
        URI uri = uriFrom("/users/" + currentUser + "/customers/" + id);
        return customer(uri);
    }

    private Map<String, Object> customerMap(Customer customer) {
        Map<String, Object> mapOfUserData = null;
        if (customer.getUser() != null) {
            mapOfUserData = new HashMap<String, Object>();
            mapOfUserData.put("id", customer.getUser().getId());
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

    @Override
    public User user(Long id) {
        //ResponseEntity<Map> mapResponseEntity = getRestTemplate().getForEntity(uriFrom("/users/" + id), Map.class);
        return getRestTemplate().getForEntity(uriFrom("/users/" + id), User.class).getBody();
    }

    @Override
    public User currentUser() {
        // /session/user
        ResponseEntity<Map<String, Object>> responseEntity =
                getRestTemplate().exchange(uriFrom("/session"), HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
                });
        Assert.isTrue(responseEntity.getStatusCode().equals(HttpStatus.OK), "invalid response status code");
        Map<String, Object> body = responseEntity.getBody();
        Number number = (Number) body.get("userId");
        long userId = number.longValue();
        return user(userId);
    }

    @Override
    public Customer createCustomer(String firstName, String lastName, Date signupDate) {
        //Customer customer = new Customer( null, null, firstName, lastName, signupDate);
        Map<String,Object> customerMap = new HashMap<String, Object>() ;
        customerMap.put("firstName", firstName) ;
        customerMap.put("lastName" ,lastName) ;
        customerMap.put("signupDate", signupDate);

        ResponseEntity<Object> responseEntity = getRestTemplate().postForEntity(uriFrom("/customers"), customerMap, Object.class);

        URI uriOfNewCustomer = responseEntity.getHeaders().getLocation();



        // HttpHeaders httpHeaders = new HttpHeaders();
        //   httpHeaders.setContentType(MediaType.APPLICATION_JSON);

/*        // build up a representation of the domain model and transmit it
        // as JSON no need to use the actual objects. this is simpler and more predictable.

        Map<String, Object> mapOfCutomerData = customerMap(customer);
        HttpEntity<Map<String, Object>> customerHttpEntity = new HttpEntity<Map<String, Object>>(mapOfCutomerData, httpHeaders);*/

        //  ResponseEntity<?> responseEntity = getRestTemplate().postForEntity(uriFrom("/customers"), customerHttpEntity, ResponseEntity.class);
//        URI newLocation = responseEntity.getHeaders().getLocation();

        URI uriOfUser = uriFrom("/users/" + currentUser().getId());

        // now we need to  POST the URI of the user tot he customer's /customers/$X/user property

        URI customerUserProperty = this.uriFrom("/customers/" + customer(uriOfNewCustomer).getId() + "/user");

        System.out.println("customer USEr property. " + customerUserProperty.toString());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON);

        HttpEntity <?> updateRequest =new HttpEntity< String>( uriOfUser.toString() , httpHeaders);

        ResponseEntity<Map> response = this.getRestTemplate().postForEntity(customerUserProperty, updateRequest, Map.class);


        return null;
        //return customer(newLocation);
    }

    private Customer customer(URI uri) {
        ResponseEntity<Customer> customerResponseEntity =
                getRestTemplate().getForEntity(uri, Customer.class);

        return customerResponseEntity.getBody();
    }

    @Override
    public Collection<Customer> loadAllUserCustomers() {
        return null;
    }

    @Override
    public void removeCustomer(Long id) {

    }

    @Override
    public void setProfilePhoto(byte[] bytesOfImage, MediaType mediaType) {

    }

    @Override
    public Customer updateCustomer(Long id, String firstName, String lastName) {
        return null;
    }

    @Override
    public ProfilePhoto getUserProfilePhoto() {
        return null;
    }

    @Override
    public Collection<Customer> search(String token) {
        return null;
    }

    /*
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
            public void setProfilePhoto(byte[] bytesOfImage, final MediaType mediaType) {
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
            public ProfilePhoto getUserProfilePhoto() {
                ResponseEntity<byte[]> profilePhotoData = this.getRestTemplate().getForEntity(uriFrom("/users/" + currentUser().getDatabaseId() + "/photo").toString(), byte[].class);
                MediaType mediaType = profilePhotoData.getHeaders().getContentType();
                return new ProfilePhoto(profilePhotoData.getBody(), mediaType);
            }

        */
    @Override
    protected FormHttpMessageConverter getFormMessageConverter() {
        FormHttpMessageConverter formHttpMessageConverter = super.getFormMessageConverter();
        List<HttpMessageConverter<?>> partConverters;
        try {

            Field partConvertersField = field(FormHttpMessageConverter.class, "partConverters");
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
    protected List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(getFormMessageConverter());
        messageConverters.add(mappingJackson2HttpMessageConverter());
        messageConverters.add(getByteArrayMessageConverter());
        return messageConverters;
    }


    //Prefer this mapping message converter over the Jackson1 since Spring HATEOAS only requires Jackson 2 AFAICT        
    protected MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }


    private URI uriFrom(String subUrl) {
        return this.uriFrom(subUrl, Collections.<String, String>emptyMap());
    }

    private URI uriFrom(String subUrl, Map<String, ?> params) {
        return UriComponentsBuilder.fromUri(this.apiBaseUri).path(subUrl).buildAndExpand(params).toUri();
    }

   /* public static class CustomerList extends Resources<Resource<Customer>> {
    }

    public static class UserResource extends Resource<User> {
        UserResource (){
        super( new User());
        }
        public UserResource(User content, Link... links) {
            super(content, links);
        }
    }

    public static class CustomerResource extends Resource<Customer> {
        public CustomerResource(Customer content, Link... links) {
            super(content, links);
        }
    }*/

    private static Field field(Class<?> cl, String fieldName) {
        Field field = null;
        try {
            field = cl.getDeclaredField(fieldName);
            if (null != field) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return field;
    }

    @Override
    protected ByteArrayHttpMessageConverter getByteArrayMessageConverter() {
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_OCTET_STREAM, MediaType.IMAGE_JPEG, MediaType.IMAGE_GIF, MediaType.IMAGE_PNG));
        return converter;
    }

    /**
     * The {@link ResourceHttpMessageConverter} ultimately defaults to using the
     * <A href= "http://www.oracle.com/technetwork/java/javase/downloads/index-135046.html">Java Activation Framework (JAF)</A> to guess
     * the mime type (content-type) of the uploaded image. Because JAF does not exist on Android, we instead use a few heuristics
     * to determine the mime type from the extension of known, relevant-to-our-application file types on Android.
     */
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
