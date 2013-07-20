/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jl.crm.web;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jl.crm.services.ServiceConfiguration;

/**
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {ServiceConfiguration.class, RepositoryRestMvcConfiguration.class, WebMvcConfiguration.class, SecurityConfiguration.class})
@WebAppConfiguration
public class OAuthTest {

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain chain;

    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
    }

    @Test
    public void formLoginForContentAll()  throws Exception {
        request.addHeader("Accept", MediaType.ALL_VALUE);

        springSecurityFilterChain.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());
    }

    @Test
    public void oauthLoginForJson()  throws Exception {
        request.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);

        springSecurityFilterChain.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Bearer realm=\"oauth\", error=\"unauthorized\", error_description=\"Full authentication is required to access this resource\"",response.getHeader("WWW-Authenticate"));
    }
}
