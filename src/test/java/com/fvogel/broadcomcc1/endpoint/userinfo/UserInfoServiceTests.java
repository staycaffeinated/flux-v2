/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.userinfo;

import com.fvogel.broadcomcc1.exception.ResourceNotFoundException;
import com.fvogel.broadcomcc1.exception.UnprocessableEntityException;
import com.fvogel.broadcomcc1.math.SecureRandomSeries;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.fvogel.broadcomcc1.math.SecureRandomSeries;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit tests of the UserInfo service
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unused"})
class UserInfoServiceTests {
	@Mock
	private UserInfoRepository mockRepository;

	@Mock
	private ApplicationEventPublisher publisher;

	@Mock
	private SecureRandomSeries mockSecureRandom;

	@InjectMocks
	private UserInfoService serviceUnderTest;

	@Spy
	private final ConversionService conversionService = FakeConversionService.build();

	final SecureRandomSeries randomSeries = new SecureRandomSeries();

	@Test
	void shouldFindAllUserInfos() {
		Flux<UserInfoEntityBean> ejbList = convertToFlux(createUserInfoList());
		given(mockRepository.findAll()).willReturn(ejbList);

		Flux<UserInfo> stream = serviceUnderTest.findAllUserInfos();

		StepVerifier.create(stream).expectSubscription().expectNextCount(3).verifyComplete();
	}

	@Test
	void shouldFindUserInfoByResourceId() {
		// Given
		UserInfoEntityBean expectedEJB = createUserInfo();
		String expectedId = randomSeries.nextResourceId();
		expectedEJB.setResourceId(expectedId);
		Mono<UserInfoEntityBean> rs = Mono.just(expectedEJB);
		given(mockRepository.findByResourceId(any(String.class))).willReturn(rs);

		// When
		Mono<UserInfo> publisher = serviceUnderTest.findUserInfoByResourceId(expectedId);

		// Then
		StepVerifier.create(publisher).expectSubscription()
				.consumeNextWith(item -> assertThat(Objects.equals(item.getResourceId(), expectedId))).verifyComplete();
	}

	@Test
	void shouldFindAllByText() {
		// Given
		final String expectedText = "Lorim ipsum";
		List<UserInfoEntityBean> expectedRows = createUserInfoListHavingSameTextValue(expectedText);
		given(mockRepository.findAllByText(expectedText)).willReturn(Flux.fromIterable(expectedRows));

		// When
		Flux<UserInfo> publisher = serviceUnderTest.findAllByText(expectedText);

		// Then
		StepVerifier.create(publisher).expectSubscription()
				.consumeNextWith(item -> assertThat(Objects.equals(item.getText(), expectedText)))
				.consumeNextWith(item -> assertThat(Objects.equals(item.getText(), expectedText)))
				.consumeNextWith(item -> assertThat(Objects.equals(item.getText(), expectedText))).verifyComplete();
	}

	@Test
	void shouldCreateUserInfo() {
		// Given
		String expectedResourceId = randomSeries.nextResourceId();
		// what the client submits to the service
		UserInfo expectedPOJO = UserInfo.builder().text("Lorim ipsum dolor amount").build();
		// what the persisted version looks like
		UserInfoEntityBean persistedObj = conversionService.convert(expectedPOJO, UserInfoEntityBean.class);
		persistedObj.setResourceId(expectedResourceId);
		persistedObj.setId(1L);
		given(mockRepository.save(any(UserInfoEntityBean.class))).willReturn(Mono.just(persistedObj));

		// When
		Mono<String> publisher = serviceUnderTest.createUserInfo(expectedPOJO);

		// Then
		StepVerifier.create(publisher.log("testCreate : ")).expectSubscription()
				.consumeNextWith(item -> assertThat(Objects.equals(item, expectedResourceId))).verifyComplete();

	}

	@Test
	void shouldUpdateUserInfo() {
		// Given
		// what the client submits
		UserInfo submittedPOJO = UserInfo.builder().text("Updated value").resourceId(randomSeries.nextResourceId())
				.build();
		// what the new persisted value looks like
		UserInfoEntityBean persistedObj = conversionService.convert(submittedPOJO, UserInfoEntityBean.class);
		Mono<UserInfoEntityBean> dataStream = Mono.just(persistedObj);
		given(mockRepository.findByResourceId(any(String.class))).willReturn(dataStream);
		given(mockRepository.save(persistedObj)).willReturn(dataStream);

		// When
		serviceUnderTest.updateUserInfo(submittedPOJO);

		// Then
		// verify publishEvent was invoked
		verify(publisher, times(1)).publishEvent(any());
	}

	@Test
	void shouldDeleteUserInfo() {
		String deletedId = randomSeries.nextResourceId();
		// The repository returns 1, to indicate 1 row was deleted
		given(mockRepository.deleteByResourceId(deletedId)).willReturn(Mono.just(1L));

		serviceUnderTest.deleteUserInfoByResourceId(deletedId);

		verify(publisher, times(1)).publishEvent(any());
	}

	@Test
	void whenDeleteNullUserInfo_expectNullPointerException() {
		assertThrows(NullPointerException.class, () -> serviceUnderTest.deleteUserInfoByResourceId((String) null));
	}

	@Test
	void whenFindNonExistingEntity_expectResourceNotFoundException() {
		given(mockRepository.findByResourceId(any())).willReturn(Mono.empty());

		Mono<UserInfoEntityBean> publisher = serviceUnderTest.findByResourceId(randomSeries.nextResourceId());

		StepVerifier.create(publisher).expectSubscription().expectError(ResourceNotFoundException.class).verify();
	}

	@Test
	void whenUpdateOfNullUserInfo_expectNullPointerException() {
		assertThrows(NullPointerException.class, () -> serviceUnderTest.updateUserInfo(null));
	}

	@Test
	void whenFindAllByNullText_expectNullPointerException() {
		assertThrows(NullPointerException.class, () -> serviceUnderTest.findAllByText(null));
	}

	@Test
	void whenCreateNullUserInfo_expectNullPointerException() {
		assertThrows(NullPointerException.class, () -> serviceUnderTest.createUserInfo(null));
	}

	/**
	 * Per its API, a ConversionService::convert method _could_ return null. The
	 * scope of this test case is to verify our own code's behavior should a null be
	 * returned. In this case, an UnprocessableEntityException is thrown.
	 */
	@Test
	void whenConversionToEjbFails_expectUnprocessableEntityException() {
		// given
		ConversionService mockConversionService = Mockito.mock(ConversionService.class);
		UserInfoService localService = new UserInfoService(mockRepository, mockConversionService, publisher,
				new SecureRandomSeries());
		given(mockConversionService.convert(any(UserInfo.class), eq(UserInfoEntityBean.class)))
				.willReturn((UserInfoEntityBean) null);

		UserInfo sample = UserInfo.builder().text("sample").build();

		// when/then
		assertThrows(UnprocessableEntityException.class, () -> localService.createUserInfo(sample));
	}

	// -----------------------------------------------------------
	// Helper methods
	// -----------------------------------------------------------

	private Flux<UserInfoEntityBean> convertToFlux(List<UserInfoEntityBean> list) {
		return Flux.fromIterable(createUserInfoList());
	}

	private List<UserInfo> convertToPojo(List<UserInfoEntityBean> list) {
		return list.stream().map(item -> conversionService.convert(item, UserInfo.class)).collect(Collectors.toList());
	}

	private List<UserInfoEntityBean> createUserInfoList() {
		UserInfoEntityBean w1 = UserInfoEntityBean.builder().resourceId(randomSeries.nextResourceId())
				.text("Lorim ipsum dolor imit").build();
		UserInfoEntityBean w2 = UserInfoEntityBean.builder().resourceId(randomSeries.nextResourceId())
				.text("Duis aute irure dolor in reprehenderit").build();
		UserInfoEntityBean w3 = UserInfoEntityBean.builder().resourceId(randomSeries.nextResourceId())
				.text("Excepteur sint occaecat cupidatat non proident").build();

		ArrayList<UserInfoEntityBean> dataList = new ArrayList<>();
		dataList.add(w1);
		dataList.add(w2);
		dataList.add(w3);

		return dataList;
	}

	private List<UserInfoEntityBean> createUserInfoListHavingSameTextValue(final String value) {
		UserInfoEntityBean w1 = UserInfoEntityBean.builder().resourceId(randomSeries.nextResourceId()).text(value)
				.build();
		UserInfoEntityBean w2 = UserInfoEntityBean.builder().resourceId(randomSeries.nextResourceId()).text(value)
				.build();
		UserInfoEntityBean w3 = UserInfoEntityBean.builder().resourceId(randomSeries.nextResourceId()).text(value)
				.build();

		ArrayList<UserInfoEntityBean> dataList = new ArrayList<>();
		dataList.add(w1);
		dataList.add(w2);
		dataList.add(w3);

		return dataList;
	}

	private UserInfoEntityBean createUserInfo() {
		return UserInfoEntityBean.builder().resourceId(randomSeries.nextResourceId()).text("Lorim ipsum dolor imit")
				.build();
	}
}