/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.userinfo;

import com.fvogel.broadcomcc1.math.SecureRandomSeries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Unit test the UserInfoController
 */
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = UserInfoController.class)
class UserInfoControllerTests {

	@MockBean
	private UserInfoService mockUserInfoService;

	@Autowired
	private WebTestClient webClient;
	@Value("${spring.webflux.base-path}")
	String applicationBasePath;

	final SecureRandomSeries randomSeries = new SecureRandomSeries();

	@Autowired
	public void setApplicationContext(ApplicationContext context) {
		webClient = WebTestClient.bindToApplicationContext(context).configureClient().build();
	}

	@Test
	void shouldGetOneUserInfo() {
		final String expectedResourceID = randomSeries.nextResourceId();
		UserInfo pojo = UserInfo.builder().text("testGetOne").resourceId(expectedResourceID).build();
		UserInfoEntityBean ejb = UserInfoEntityBean.builder().resourceId(expectedResourceID).text("testGetOne").build();

		when(mockUserInfoService.findByResourceId(expectedResourceID)).thenReturn(Mono.just(ejb));
		when(mockUserInfoService.findUserInfoByResourceId(expectedResourceID)).thenReturn(Mono.just(pojo));

		webClient.get().uri(UserInfoRoutes.FIND_ONE_USERINFO, expectedResourceID).accept(MediaType.APPLICATION_JSON)
				.exchange().expectStatus().isOk().expectBody().jsonPath("$.resourceId").isNotEmpty().jsonPath("$.text")
				.isNotEmpty();

		Mockito.verify(mockUserInfoService, times(1)).findUserInfoByResourceId(expectedResourceID);
	}

	@Test
	void shouldGetAllUserInfos() {
		List<UserInfo> list = createUserInfoList();
		Flux<UserInfo> flux = Flux.fromIterable(list);

		when(mockUserInfoService.findAllUserInfos()).thenReturn(flux);

		webClient.get().uri(UserInfoRoutes.FIND_ALL_USERINFO).accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
				.jsonPath("$.[0].text").isNotEmpty().jsonPath("$.[0].resourceId").isNotEmpty();
	}

	@Test
	void shouldCreateUserInfo() {
		UserInfo pojo = createUserInfo();
		pojo.setResourceId(null);
		String expectedId = randomSeries.nextResourceId();

		when(mockUserInfoService.createUserInfo(any(UserInfo.class))).thenReturn(Mono.just(expectedId));

		webClient.post().uri(UserInfoRoutes.CREATE_USERINFO).contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(pojo), UserInfo.class).exchange().expectStatus().isCreated().expectHeader()
				.contentType(MediaType.APPLICATION_JSON);
	}

	@Test
	void shouldUpdateUserInfo() {
		UserInfo pojo = createUserInfo();
		webClient.put().uri(UserInfoRoutes.UPDATE_USERINFO, pojo.getResourceId())
				.contentType(MediaType.APPLICATION_JSON).body(Mono.just(pojo), UserInfo.class).exchange().expectStatus()
				.isOk();
	}

	@Test
	void whenMismatchOfResourceIds_expectUnprocessableEntityException() {
		// Given
		UserInfo pojo = createUserInfo();
		String idInBody = randomSeries.nextResourceId();
		String idInParameter = randomSeries.nextResourceId();
		pojo.setResourceId(idInBody);

		// when the ID in the URL is a mismatch to the ID in the POJO, the request
		// should fail
		webClient.put().uri(UserInfoRoutes.UPDATE_USERINFO, idInParameter).contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(pojo), UserInfo.class).exchange().expectStatus().is4xxClientError();
	}

	@Test
	void shouldDeleteUserInfo() {
		UserInfo pojo = createUserInfo();
		when(mockUserInfoService.findUserInfoByResourceId(pojo.getResourceId())).thenReturn(Mono.just(pojo));

		webClient.delete().uri(UserInfoRoutes.DELETE_USERINFO, pojo.getResourceId()).exchange().expectStatus()
				.isNoContent();
	}

	@Test
	void shouldGetUserInfosAsStream() throws Exception {
		// Given
		List<UserInfo> resourceList = createUserInfoList();
		given(mockUserInfoService.findAllUserInfos()).willReturn(Flux.fromIterable(resourceList));

		// When
		FluxExchangeResult<UserInfo> result = webClient.get().uri(UserInfoRoutes.STREAM_USERINFO)
				.accept(MediaType.TEXT_EVENT_STREAM).exchange().expectStatus().isOk().returnResult(UserInfo.class);

		// Then
		Flux<UserInfo> events = result.getResponseBody();
		StepVerifier.create(events).expectSubscription().consumeNextWith(p -> {
			assertThat(p.getResourceId()).isNotNull();
			assertThat(p.getText()).isNotEmpty();
		}).consumeNextWith(p -> {
			assertThat(p.getResourceId()).isNotNull();
			assertThat(p.getText()).isNotEmpty();
		}).thenCancel().verify();
	}

	/**
	 * Generates a list of sample test data
	 */
	private List<UserInfo> createUserInfoList() {
		UserInfo w1 = UserInfo.builder().resourceId(randomSeries.nextResourceId()).text("Lorim ipsum dolor imit")
				.build();
		UserInfo w2 = UserInfo.builder().resourceId(randomSeries.nextResourceId()).text("Hodor Hodor Hodor Hodor")
				.build();
		UserInfo w3 = UserInfo.builder().resourceId(randomSeries.nextResourceId()).text("Now is the time to fly")
				.build();

		ArrayList<UserInfo> list = new ArrayList<>();
		list.add(w1);
		list.add(w2);
		list.add(w3);

		return list;
	}

	/**
	 * Generates a single test item
	 */
	private UserInfo createUserInfo() {
		return UserInfo.builder().resourceId(randomSeries.nextResourceId())
				.text("Duis aute irure dolor in reprehenderit.").build();
	}
}
