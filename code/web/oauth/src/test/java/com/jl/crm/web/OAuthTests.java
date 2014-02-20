package com.jl.crm.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Demonstrates how to test the OAuth flow itself
 *
 * @author Rob Winch
 * @author Josh Long
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class OAuthTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mvc;
    private RestTemplate restTemplate;

    private MediaType jsonMediaType =
            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();

        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new StringHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());

        this.restTemplate = new RestTemplate();
        this.restTemplate.setMessageConverters(converters);
    }

    @Test
    public void testAndroidCrmAuthenticationAndRestApiAccess() throws Exception {
        // setup
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
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
                        .accept(jsonMediaType)
                        .param("client_id", clientId)
                        .param("password", password)
                        .param("response_type", "token")
                        .param("client_secret", clientSecret)
                        .param("username", username)
                        .param("grant_type", "password")
                        .param("scope", "read,write")
                        .session(session);


        MockHttpServletResponse response = mvc.perform(tokenRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonMediaType)).andReturn().getResponse();

        String bodyContent = response.getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();

        Map map = objectMapper.readValue(bodyContent, Map.class);

        Assert.assertTrue(map.containsKey("access_token"));
        String at = (String) map.get("access_token");
        Assert.assertNotNull(at);

        System.out.println("access_token: " + at);

        //curl http://localhost:8080/users/5 -H "Authorization: Bearer bc2e9d2b-2d44-45cc-8e5b-6c15918d0132"


        String bodyOfResult = mvc.perform(get("/users/5/customers")
                .accept(jsonMediaType)
                .header("Authorization", "Bearer " + at))
                .andExpect(jsonPath("$._embedded.customerList", hasSize(5)))
                .andReturn().getResponse().getContentAsString();

        System.out.println("body of response: " + bodyOfResult);
        // verify we got joshlong


    }

}