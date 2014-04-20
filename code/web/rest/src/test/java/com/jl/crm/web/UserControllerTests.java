package com.jl.crm.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration (classes = Application.class )
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class UserControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private RestTemplate restTemplate;
    private int userId = 5;
    private String jsonDateFormatPattern = "yyyy-MM-dd HH:mm:ss";
    private MockRestServiceServer mockServer;
    private MockMvc mockMvc;
    private MediaType applicationJsonMediaType =
            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    private MediaType vndErrorMediaType = MediaType.parseMediaType("application/vnd.error");

    @Before
    public void setup() {
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new StringHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());

        this.restTemplate = new RestTemplate();
        this.restTemplate.setMessageConverters(converters);

        this.mockServer = MockRestServiceServer.createServer(this.restTemplate);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testLoadingUserCustomers() throws Exception {
        this.mockMvc.perform(get("/users/" + userId + "/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(applicationJsonMediaType))
                .andExpect(jsonPath("$", hasSize(5))) // how many customers are seeded in the schema.sql file? 5.
                .andExpect(jsonPath("$[0].firstName", is("Rossen")));
    }

    @Test
    public void testDeletingAUser() throws Exception {

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCustomer() throws Exception {

        long now = System.currentTimeMillis();
        String f = "Joe", l = "Doe";

        String jsonOfJoeDoe = "{ \"signupDate\":" + now + ",\"firstName\":\"" + f +
                "\",\"lastName\":\"" + l + "\"}";

        MvcResult mvcResult = mockMvc.perform(post("/users/{userId}/customers", userId)
                .accept(applicationJsonMediaType)
                .content(jsonOfJoeDoe)
                .contentType(this.applicationJsonMediaType))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(this.applicationJsonMediaType))
                .andReturn();

        mockServer.verify();

        String locationUri = mvcResult.getResponse().getHeader("Location");
        Assert.assertTrue(locationUri.contains("/users/" + userId + "/customers/"));
    }

    @Test
    public void testLoadingACustomerThatDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/users/" + 5 + "/customers/" + 24022)
                .accept(this.applicationJsonMediaType))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(this.vndErrorMediaType));

    }

    @Test
    public void testLoadingAUserThatDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/users/" + 400)
                .accept(this.applicationJsonMediaType))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(this.vndErrorMediaType));

    }

    @Test
    public void testLoadingAUser() throws Exception {

        DateFormat dateFormat = new SimpleDateFormat(jsonDateFormatPattern);
        int userId = 5;
        Date date = dateFormat.parse("2013-06-02 15:33:51");
        this.mockMvc.perform(get("/users/" + userId)
                .accept(this.applicationJsonMediaType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(applicationJsonMediaType))
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.firstName", is("Josh")))
                .andExpect(jsonPath("$.password", is("cowbell")))
                .andExpect(jsonPath("$.signupDate", is(date.getTime())))
                .andExpect(jsonPath("$.lastName", is("Long")));

    }
}
