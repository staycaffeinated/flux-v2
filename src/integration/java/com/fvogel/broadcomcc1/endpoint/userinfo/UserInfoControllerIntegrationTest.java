/*
 * Copyright 2022 [CopyrightOwner]
 */

package com.fvogel.broadcomcc1.endpoint.userinfo;

import com.fvogel.broadcomcc1.common.ResourceIdentity;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests of UserInfoController
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserInfoControllerIntegrationTest {

	@LocalServerPort
	int port;
	@Value("${spring.webflux.base-path}")
	String applicationBasePath;
	private WebTestClient client;
	private UserInfo userInfo;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	public void setApplicationContext(ApplicationContext context) {
		this.client = WebTestClient.bindToApplicationContext(context).configureClient().build();
	}

	@Test
	void testGetAllUserInfos() {
		this.client.get().uri(UserInfoRoutes.FIND_ALL_USERINFO).accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
				.jsonPath("$.[0].text").isNotEmpty().jsonPath("$.[0].resourceId").isNotEmpty();
	}

	@Test
	void testGetSingleUserInfo() {
		createUserInfo();

		this.client.get().uri(replaceId(UserInfoRoutes.FIND_ONE_USERINFO)).accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
				.jsonPath("$.resourceId").isNotEmpty().jsonPath("$.text").isNotEmpty();
	}

	@Test
	void testGetCatalogItemsStream() throws Exception {
		FluxExchangeResult<UserInfo> result = this.client.get().uri(UserInfoRoutes.STREAM_USERINFO)
				.accept(MediaType.TEXT_EVENT_STREAM).exchange().expectStatus().isOk().returnResult(UserInfo.class);

		Flux<UserInfo> events = result.getResponseBody();

		StepVerifier.create(events).expectSubscription().expectNextMatches(p -> p.getResourceId() != null)
				.expectNextMatches(p -> p.getResourceId() != null).expectNextMatches(p -> p.getResourceId() != null)
				.thenCancel().verify();
	}

	@Test
	void testCreateUserInfo() {
		UserInfo userInfo = UserInfoGenerator.generateUserInfo();
		userInfo.setResourceId(null);

		this.client.post().uri(UserInfoRoutes.CREATE_USERINFO).contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(userInfo), UserInfo.class).exchange().expectStatus().isCreated().expectHeader()
				.contentType(MediaType.APPLICATION_JSON);
	}

	@Test
	void testUpdateUserInfo() {
		createUserInfo();

		userInfo.setText("my new text");

		this.client.put().uri(replaceId(UserInfoRoutes.UPDATE_USERINFO)).contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(userInfo), UserInfo.class).exchange().expectStatus().isOk();
	}

	@Test
	void testDeleteUserInfo() {
		createUserInfo();

		this.client.delete().uri(replaceId(UserInfoRoutes.DELETE_USERINFO)).exchange().expectStatus().isNoContent();
	}

	@Test
	void testResourceNotFoundException() throws Exception {
		this.client.get().uri(UserInfoRoutes.FIND_ONE_USERINFO.replaceAll("\\{id\\}", "12345"))
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNotFound().expectHeader()
				.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * Creates a new UserInfo then updates the resourceId of the instance variable,
	 * userInfo, with the resourceId of the added UserInfo.
	 */
	void createUserInfo() {
		userInfo = UserInfoGenerator.generateUserInfo();
		userInfo.setResourceId(null);

		EntityExchangeResult<ResourceIdentity> result = this.client.post().uri(UserInfoRoutes.CREATE_USERINFO)
				.contentType(MediaType.APPLICATION_JSON).body(Mono.just(userInfo), UserInfo.class).exchange()
				.expectStatus().isCreated().expectBody(ResourceIdentity.class).returnResult();

		// After the userInfo is created, the endpoint returns the resourceId of the
		// created book. Here, the resourceId of the instance variable, userInfo, is
		// updated
		// to enable the current test to acquire the new UserInfo's resourceId.
		String resourceId = result.getResponseBody().getResourceId();
		userInfo.setResourceId(resourceId);
	}

	/**
	 * Use this to replace the 'id' parameter in the query string with the
	 * resourceId from the instance variable, userInfo
	 */
	String replaceId(String path) {
		return path.replaceAll("\\{id\\}", userInfo.getResourceId().toString());
	}
}