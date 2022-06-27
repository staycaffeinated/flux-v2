/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.userinfo;

import com.fvogel.broadcomcc1.math.SecureRandomSeries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * unit tests
 */
class UserInfoEventTests {

	final SecureRandomSeries randomSeries = new SecureRandomSeries();

	@Test
	void shouldReturnEventTypeOfCreated() {
		UserInfo resource = UserInfo.builder().resourceId(randomSeries.nextResourceId()).text("Hello world").build();
		UserInfoEvent event = new UserInfoEvent(UserInfoEvent.CREATED, resource);

		assertThat(event.getEventType()).isEqualTo(UserInfoEvent.CREATED);
	}

	@Test
	void shouldReturnEventTypeOfUpdated() {
		UserInfo resource = UserInfo.builder().resourceId(randomSeries.nextResourceId()).text("Hello world").build();
		UserInfoEvent event = new UserInfoEvent(UserInfoEvent.UPDATED, resource);

		assertThat(event.getEventType()).isEqualTo(UserInfoEvent.UPDATED);
	}

	@Test
	void shouldReturnEventTypeOfDeleted() {
		UserInfo resource = UserInfo.builder().resourceId(randomSeries.nextResourceId()).text("Hello world").build();
		UserInfoEvent event = new UserInfoEvent(UserInfoEvent.DELETED, resource);

		assertThat(event.getEventType()).isEqualTo(UserInfoEvent.DELETED);
	}
}