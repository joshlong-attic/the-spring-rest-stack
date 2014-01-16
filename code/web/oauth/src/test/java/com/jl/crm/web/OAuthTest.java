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

import static com.jl.crm.web.SecurityRequestPostProcessors.csrf;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URLEncoder;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jl.crm.services.ServiceConfiguration;

/**
 * @author Rob Winch
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceConfiguration.class,
		RepositoryRestMvcConfiguration.class, WebMvcConfiguration.class,
		SecurityConfiguration.class })
@WebAppConfiguration
public class OAuthTest {
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context)
				.addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void formLoginForContentTextHtml() throws Exception {
		mvc.perform(get("/").accept(browserMediaType)).andExpect(
				status().isMovedTemporarily());
	}

	private MediaType browserMediaType = MediaType.TEXT_HTML;

	@Test
	public void oauthLoginForJson() throws Exception {
		mvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(
						header().string(
								"WWW-Authenticate",
								"Bearer realm=\"oauth\", error=\"unauthorized\", error_description=\"Full authentication is required to access this resource\""));
	}

	@Test
	public void iOSTests() throws Exception {
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();

		// Get an OAuth Token

		RequestBuilder tokenRequest = post("/oauth/token")
				.accept(MediaType.APPLICATION_JSON)
				.param("password", "android").param("username", "roy")
				.param("grant_type", "password").param("scope", "read,write")
				.param("client_secret", "123456").param("client_id", "ios-crm");

		byte[] tokenResponse = mvc.perform(tokenRequest)
				.andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsByteArray();

		MockClientHttpResponse tokenHttpResponse = new MockClientHttpResponse(
				tokenResponse, HttpStatus.OK);
		OAuth2AccessToken token = (OAuth2AccessToken) converter.read(
				OAuth2AccessToken.class, tokenHttpResponse);

		// use the token to get Roy

		RequestBuilder userRequest = get("/user").accept(
				MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + token.getValue());

		byte[] userResponse = mvc.perform(userRequest)
				.andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsByteArray();

		MockClientHttpResponse userHttpResponse = new MockClientHttpResponse(
				userResponse, HttpStatus.OK);
		@SuppressWarnings("unchecked")
		Map<String, String> user = (Map<String, String>) converter.read(
				Map.class, userHttpResponse);

		// verify we got roy
		assertEquals("roy", user.get("username"));
	}

	@Test
	public void browserTests() throws Exception {
		// setup
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
		MockHttpSession session = new MockHttpSession() {
			// avoid session fixation protection issues
			public void invalidate() {
			}
		};
		String redirectUri = "http://localhost/crm/welcome.html";
		String encodedRedirectUri = URLEncoder.encode(redirectUri, "UTF-8");

		// get a token
		RequestBuilder tokenRequest = get(
				"/oauth/authorize?client_id=android-crm&response_type=token&scope=read%2Cwrite&redirect_uri="
						+ encodedRedirectUri).accept(browserMediaType)
				.param("client_id", "android-crm")
				.param("response_type", "token")
				.param("redirect_uri", redirectUri).session(session);

		mvc.perform(tokenRequest).andExpect(
				redirectedUrl("http://localhost/crm/signin.html"));

		// login within the browser
		RequestBuilder loginRequest = post("/crm/signin.html")
				.accept(browserMediaType).param("username", "joshlong")
				.param("password", "cowbell").session(session).with(csrf());

		mvc.perform(loginRequest)
				.andExpect(
						redirectedUrl("http://localhost/oauth/authorize?client_id=android-crm&response_type=token&scope=read%2Cwrite&redirect_uri="
								+ encodedRedirectUri));

		// make the original request now we are authenticated
		tokenRequest = get(
				"/oauth/authorize?client_id=android-crm&response_type=token&scope=read%2Cwrite&redirect_uri=http://localhost/crm/welcome.html")
				.accept(browserMediaType).param("client_id", "android-crm")
				.param("response_type", "token")
				.param("redirect_uri", redirectUri).session(session);
		mvc.perform(tokenRequest);

		// confirm access is granted
		RequestBuilder accessConfirmationRequest = post("/oauth/authorize")
				.accept(browserMediaType)
				// .accept(MediaType.ALL)
				.param("user_oauth_approval", "true").session(session)
				.with(csrf());

		String confirmationUrl = mvc.perform(accessConfirmationRequest)
				.andReturn().getResponse().getRedirectedUrl();

		// extract the token from the response
		String[] confirmationParts = confirmationUrl.split("[=&]");
		String token = null;
		for (int i = 0; i < confirmationParts.length; i++) {
			String part = confirmationParts[i];
			if (part.endsWith("access_token")) {
				token = confirmationParts[i + 1];
				break;
			}
		}

		// Get the user
		RequestBuilder userRequest = get("/user").accept(
				MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + token);

		byte[] userResponse = mvc.perform(userRequest)
				.andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsByteArray();

		MockClientHttpResponse userHttpResponse = new MockClientHttpResponse(
				userResponse, HttpStatus.OK);
		@SuppressWarnings("unchecked")
		Map<String, String> user = (Map<String, String>) converter.read(
				Map.class, userHttpResponse);

		// verify we got joshlong
		assertEquals("joshlong", user.get("username"));
	}
}
