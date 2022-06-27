/*
 * Copyright 2022 [CopyrightOwner]
 */

package com.fvogel.broadcomcc1.endpoint.userinfo;

import com.fvogel.broadcomcc1.math.SecureRandomSeries;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserInfoResourceToBeanConverterTests {

	UserInfoResourceToBeanConverter converterUnderTest = new UserInfoResourceToBeanConverter();

	final SecureRandomSeries randomSeries = new SecureRandomSeries();

	@Test
	void whenDataToConvertIsWellFormed_expectSuccessfulConversion() {
		final String expectedPublicId = randomSeries.nextResourceId();
		final String expectedText = "hello world";

		UserInfo pojo = UserInfo.builder().resourceId(expectedPublicId).text(expectedText).build();
		UserInfoEntityBean ejb = converterUnderTest.convert(pojo);

		assertThat(ejb).isNotNull();
		assertThat(ejb.getResourceId()).isEqualTo(expectedPublicId);
		assertThat(ejb.getText()).isEqualTo(expectedText);
	}

	@Test
	void whenDataListIsWellFormed_expectSuccessfulConversion() {
		// Given a list of 3 items
		final String itemOne_expectedPublicId = randomSeries.nextResourceId();
		final String itemOne_expectedText = "hello goodbye";

		final String itemTwo_expectedPublicId = randomSeries.nextResourceId();
		final String itemTwo_expectedText = "strawberry fields";

		final String itemThree_expectedPublicId = randomSeries.nextResourceId();
		final String itemThree_expectedText = "sgt pepper";

		UserInfo itemOne = UserInfo.builder().resourceId(itemOne_expectedPublicId).text(itemOne_expectedText).build();
		UserInfo itemTwo = UserInfo.builder().resourceId(itemTwo_expectedPublicId).text(itemTwo_expectedText).build();
		UserInfo itemThree = UserInfo.builder().resourceId(itemThree_expectedPublicId).text(itemThree_expectedText)
				.build();

		ArrayList<UserInfo> list = new ArrayList<>();
		list.add(itemOne);
		list.add(itemTwo);
		list.add(itemThree);

		// When
		List<UserInfoEntityBean> results = converterUnderTest.convert(list);

		// Then expect the fields of the converted items to match the original items
		assertThat(results).hasSameSizeAs(list);
		assertThat(fieldsMatch(itemOne, results.get(0))).isTrue();
		assertThat(fieldsMatch(itemTwo, results.get(1))).isTrue();
		assertThat(fieldsMatch(itemThree, results.get(2))).isTrue();
	}

	@Test
	void whenConvertingNullObject_expectNullPointerException() {
		assertThrows(NullPointerException.class, () -> converterUnderTest.convert((UserInfo) null));
	}

	@Test
	void whenConvertingNullList_expectNullPointerException() {
		assertThrows(NullPointerException.class, () -> converterUnderTest.convert((List<UserInfo>) null));
	}

	@Test
	void shouldPopulateAllFields() {
		UserInfo resource = UserInfo.builder().resourceId(randomSeries.nextResourceId()).text("hello world").build();

		UserInfoEntityBean bean = converterUnderTest.convert(resource);
		assertThat(bean.getResourceId()).isEqualTo(resource.getResourceId());
		assertThat(bean.getText()).isEqualTo(resource.getText());
	}

	// ------------------------------------------------------------------
	// Helper methods
	// ------------------------------------------------------------------
	private boolean fieldsMatch(UserInfo expected, UserInfoEntityBean actual) {
		if (!Objects.equals(expected.getResourceId(), actual.getResourceId()))
			return false;
		if (!Objects.equals(expected.getText(), actual.getText()))
			return false;
		return true;
	}
}
