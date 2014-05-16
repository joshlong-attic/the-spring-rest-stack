package com.jl.crm.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Demonstrates how to test the OAuth flow itself
 *
 * @author Rob Winch
 * @author Josh Long
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class OAuthSecuredUserControllerTests {

    private String jsonDateFormatPattern = "yyyy-MM-dd HH:mm:ss";

    private MediaType applicationJsonMediaType = MediaType.APPLICATION_JSON;
//            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private MediaType vndErrorMediaType = MediaType.parseMediaType("application/vnd.error");

    private String accessToken;

    private long userId = 5;

    @Autowired
    private WebApplicationContext context;

    @Autowired//(required = false)
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;


    @Before
    public void setup() throws Exception {


        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain).build();


        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new StringHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());

        this.restTemplate = new RestTemplate();
        this.restTemplate.setMessageConverters(converters);
        this.mockServer = MockRestServiceServer.createServer(this.restTemplate);


        // setup
        MockHttpSession session = new MockHttpSession() {
            // avoid session fixation protection issues
            public void invalidate() {
            }
        };

        String clientId = "android-crm",
                clientSecret = "123456",
                username = "joshlong",
                password = "cowbell";


        // get a token
        RequestBuilder tokenRequest =
                get("/oauth/token")
                        .header("Authorization", "Basic "+Base64.getEncoder().encodeToString("android-crm:123456".getBytes("UTF-8")))
                        .accept(applicationJsonMediaType)
                        .param("client_id", clientId)
                        .param("password", password)
//                        .param("response_type", "token")
                        .param("client_secret", clientSecret)
                        .param("username", username)
                        .param("grant_type", "password")
                        .param("scope", "write")
                        .session(session);


        /*
        *
        * curl -X POST -vu android-crm:123456 http://localhost:8080/oauth/token -H "Accept: application/json"
         *     -d "password=cowbell&username=joshlong&grant_type=password&scope=write&client_secret=123456&client_id=android-crm
        */
        MockHttpServletResponse response = mockMvc.perform(tokenRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(applicationJsonMediaType)).andReturn().getResponse();

        String bodyContent = response.getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();

        Map map = objectMapper.readValue(bodyContent, Map.class);

        Assert.assertTrue(map.containsKey("access_token"));
        accessToken = (String) map.get("access_token");
        Assert.assertNotNull(accessToken);

        log("access_token: " + accessToken);

        Assert.assertNotNull("the accessToken must not be null!",
                this.accessToken);
    }


    @Test
    public void testLoadingUserCustomers() throws Exception {
        this.mockMvc.perform(
            this.encodeAuthorizationAccessToken(get("/users/" + userId + "/customers")))
                .andExpect(status().isOk()) ;
              /*  .andExpect(content().contentType(applicationJsonMediaType))
                .andExpect(jsonPath("$._embedded.customerList", hasSize(5))) // how many customers are seeded in the schema.sql file? 5.
                .andExpect(jsonPath("$._embedded.customerList[0].firstName", is("Rossen")));*/
    }

    @Test
    public void testDeletingAUser() throws Exception {

        mockMvc.perform(encodeAuthorizationAccessToken(delete("/users/{userId}", userId)))
                .andExpect(status().isNotFound());

        mockMvc.perform(encodeAuthorizationAccessToken(get("/users/{userId}", userId)))
                .andExpect(status().isNotFound());
    }

    protected MockHttpServletRequestBuilder encodeAuthorizationAccessToken(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    @Test
    public void testLoadingAUserThatDoesNotExist() throws Exception {
        this.mockMvc.perform(encodeAuthorizationAccessToken(get("/users/" + 400)
                .accept(this.applicationJsonMediaType)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(this.vndErrorMediaType));

    }


    @Test
    public void testLoadingAUser() throws Exception {

        DateFormat dateFormat = new SimpleDateFormat(jsonDateFormatPattern);
        int userId = 5;
        Date date = dateFormat.parse("2013-06-02 15:33:51");
        this.mockMvc.perform(encodeAuthorizationAccessToken(get("/users/" + userId)
                .accept(this.applicationJsonMediaType)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(applicationJsonMediaType))
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.firstName", is("Josh")))
                .andExpect(jsonPath("$.password", isEmptyOrNullString()))
                .andExpect(jsonPath("$.signupDate", is(date.getTime())))
                .andExpect(jsonPath("$.lastName", is("Long")))
                .andExpect(jsonPath("$._links.photo.href", containsString("/users/" + userId + "/photo")))
                .andExpect(jsonPath("$._links.customers.href", containsString("/users/" + userId + "/customers")))
                .andExpect(jsonPath("$._links.self.href", containsString("/users/" + userId)));

    }

    @Test
    public void testCreateCustomer() throws Exception {

        long now = System.currentTimeMillis();
        String f = "Joe", l = "Doe";

        String jsonOfJoeDoe = "{ \"signupDate\":" + now + ",\"firstName\":\"" + f +
                "\",\"lastName\":\"" + l + "\"}";

        MvcResult mvcResult = mockMvc.perform(encodeAuthorizationAccessToken(post("/users/{userId}/customers", userId)
                .accept(applicationJsonMediaType))
                .content(jsonOfJoeDoe)
                .contentType(this.applicationJsonMediaType))
                .andExpect(status().isCreated())
                .andReturn();

        mockServer.verify();

        String locationUri = mvcResult.getResponse().getHeader("Location");
        Assert.assertTrue(locationUri.contains("/users/" + userId + "/customers/"));
    }

    @Test
    public void testLoadingACustomerThatDoesNotExist() throws Exception {
        this.mockMvc.perform(encodeAuthorizationAccessToken(get("/users/" + 5 + "/customers/" + 24022)
                .accept(this.applicationJsonMediaType)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(this.vndErrorMediaType));

    }


    protected void log(String msg, Object... p) {
        LogFactory.getLog(getClass()).info(String.format(msg, p));
    }


}