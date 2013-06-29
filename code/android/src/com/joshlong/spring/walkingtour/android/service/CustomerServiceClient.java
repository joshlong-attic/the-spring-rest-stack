package com.joshlong.spring.walkingtour.android.service;

import android.util.Log;
import com.joshlong.spring.walkingtour.android.async.*;
import com.joshlong.spring.walkingtour.android.model.Customer;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

public class CustomerServiceClient implements CustomerService {

    private String baseServiceUrl;
    private RestTemplate restTemplate;
    private final String slash = "/";
    public CustomerServiceClient(String url, RestTemplate restTemplate) {
        setBaseServiceUrl(url);
        setRestTemplate(restTemplate);
    }

    public CustomerServiceClient(String url) {
        this(url, new RestTemplate());
    }

    public void setRestTemplate(RestTemplate rt) {
        assert rt != null : "you must provide a non-null value for a 'RestTemplate' instance!";
        this.restTemplate = rt;
        List<HttpMessageConverter<?>> messageConverterList = rt.getMessageConverters();
        if ((messageConverterList == null || messageConverterList.size() == 0) || !containsObjectOfType(rt.getMessageConverters(), MappingJacksonHttpMessageConverter.class))
            rt.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    }

    private boolean containsObjectOfType(Collection<?> tCollection, Class<?> tClass) {
        for (Object object : tCollection)
            if (object.getClass().isAssignableFrom(tClass))
                return true;
        return false;
    }

    protected void setBaseServiceUrl(String url) {
        if (!url.endsWith( slash))
            url = url + slash ;
        this.baseServiceUrl = url;
    }

    private String urlForPath(final String p) {
        String inputPath = p;
        if (inputPath.startsWith(slash))
            inputPath = inputPath.substring(1);
        return this.baseServiceUrl + inputPath;
    }

    private <T> T extractResponse(final ResponseEntity<T> responseEntity) {
        if (responseEntity != null && responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return responseEntity.getBody();
        }
        return null;
    }

    @RunOffUiThread
    @Override
    public void search(String searchQuery, AsyncCallback<List<Customer>> asyncCallback) {
        if (searchQuery.length() < 3) {
            LogFactory.getLog(getClass()).debug(String.format("it's recommended that you don't permit searches with < 3 characters, like %s", searchQuery + ""));
        }
        String url = urlForPath("customers");
        String uriWithVariables = UriComponentsBuilder.fromUriString(url)
                .queryParam("query", searchQuery)
                .build()
                .toUriString();
        Log.d(getClass().getName(), "the URI with variables is " + uriWithVariables);
        List<Customer> customers = this.restTemplate.getForObject(uriWithVariables, CustomerList.class);
        asyncCallback.methodInvocationCompleted(customers);
    }

    @Override
    @RunOffUiThread
    public void updateCustomer(long id, String fn, String ln, AsyncCallback<Customer> asyncCallback) {
        String urlForPath = urlForPath("customer/{customerId}");
        Customer customer = new Customer(id, fn, ln);
        ResponseEntity<Customer> customerResponseEntity =
                restTemplate.postForEntity(urlForPath, customer, Customer.class, java.util.Collections.singletonMap("customerId", id));
        asyncCallback.methodInvocationCompleted(extractResponse(customerResponseEntity));
    }

    @RunOffUiThread
    @Override
    public void getCustomerById(long id, AsyncCallback<Customer> customerAsyncCallback) {

        customerAsyncCallback.methodInvocationCompleted(
                extractResponse(restTemplate.getForEntity(
                        urlForPath("customer/{customerId}"), Customer.class, id))
        );
    }

    @RunOffUiThread
    @Override
    public void createCustomer(String fn, String ln, AsyncCallback<Customer> customerAsyncCallback) {
        customerAsyncCallback.methodInvocationCompleted(
                extractResponse(this.restTemplate.postForEntity(
                        urlForPath("customers"), new Customer(fn, ln), Customer.class)));
    }

    // allows us to bake in the generic type, `Customer`, so that
    // it's visible at runtime for the Jackson marshalling process.
    private static class CustomerList extends ArrayList<Customer> {
    }
}
