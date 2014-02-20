package com.jl.crm.web;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.core.io.Resource;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class UserProfilePhotoControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private RestTemplate restTemplate;
    private int userId = 5;
    private MockRestServiceServer mockServer;
    private MockMvc mockMvc;
    private MediaType pngMediaType = MediaType.IMAGE_PNG;

    @Value("classpath:/spring-dog-3.png")
    private Resource spring3DogPng;

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
    public void testPostingAProfilePhoto() throws Exception {
        byte[] pngBytes = IOUtils.toByteArray(this.spring3DogPng.getInputStream());
        String uri = "/users/{user}/photo";
        mockMvc.perform(post(uri, userId).content(pngBytes).contentType(MediaType.MULTIPART_FORM_DATA)).andReturn();



        mockMvc.perform(get(uri, userId).accept(pngMediaType)).andExpect(content().contentType(pngMediaType)).andReturn();
    }


}
