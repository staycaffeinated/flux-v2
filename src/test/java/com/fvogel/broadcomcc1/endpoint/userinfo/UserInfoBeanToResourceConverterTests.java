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

class UserInfoBeanToResourceConverterTests {

	UserInfoBeanToResourceConverter converterUnderTest = new UserInfoBeanToResourceConverter();

	final SecureRandomSeries randomSeries = new SecureRandomSeries();

	@Test
	void whenDataToConvertIsWellFormed_expectSuccessfulConversion() {
		final String expectedPublicId = randomSeries.nextResourceId();
		final String expectedText = "hello world";

		UserInfoEntityBean ejb = UserInfoEntityBean.builder().resourceId(expectedPublicId).id(0L).text(expectedText)
				.build();
		UserInfo pojo = converterUnderTest.convert(ejb);

		assertThat(pojo).isNotNull();
		assertThat(pojo.getResourceId()).isEqualTo(expectedPublicId);
		assertThat(pojo.getText()).isEqualTo(expectedText);
	}

	@Test
	void whenDataListIsWellFormed_expectSuccessfulConversion() {
		// Given a list of 3 items
		final String itemOne_expectedPublicId = randomSeries.nextResourceId();
		final Long itemOne_expectedDatabaseId = 2424L;
		final String itemOne_expectedText = "hello goodbye";

		final String itemTwo_expectedPublicId = randomSeries.nextResourceId();
		final Long itemTwo_expectedDatabaseId = 42348L;
		final String itemTwo_expectedText = "strawberry fields";

		final String itemThree_expectedPublicId = randomSeries.nextResourceId();
		final Long itemThree_expectedDatabaseId = 9341L;
		final String itemThree_expectedText = "sgt pepper";

		UserInfoEntityBean itemOne = UserInfoEntityBean.builder().resourceId(itemOne_expectedPublicId)
				.text(itemOne_expectedText).id(itemOne_expectedDatabaseId).build();
		UserInfoEntityBean itemTwo = UserInfoEntityBean.builder().resourceId(itemTwo_expectedPublicId)
				.text(itemTwo_expectedText).id(itemTwo_expectedDatabaseId).build();
		UserInfoEntityBean itemThree = UserInfoEntityBean.builder().resourceId(itemThree_expectedPublicId)
				.text(itemThree_expectedText).id(itemThree_expectedDatabaseId).build();

		ArrayList<UserInfoEntityBean> list = new ArrayList<>();
		list.add(itemOne);
		list.add(itemTwo);
		list.add(itemThree);

		// When
		List<UserInfo> results = converterUnderTest.convert(list);

		// Then expect the fields of the converted items to match the original items
		assertThat(results).hasSameSizeAs(list);
		assertThat(fieldsMatch(itemOne, results.get(0))).isTrue();
		assertThat(fieldsMatch(itemTwo, results.get(1))).isTrue();
		assertThat(fieldsMatch(itemThree, results.get(2))).isTrue();
	}

	@Test
	void whenBeanIsNull_expectNullPointerException() {
		assertThrows(NullPointerException.class, () -> converterUnderTest.convert((UserInfoEntityBean) null));
	}

	@Test
	void whenListIsNull_expectNullPointerException() {
		assertThrows(NullPointerException.class, () -> converterUnderTest.convert((List<UserInfoEntityBean>) null));
	}

	/**
	 * Verify that properties of the EJB that must not shared outside the security
	 * boundary of the service are not copied into the RESTful resource. For
	 * example, the database ID assigned to an entity bean must not be exposed to
	 * external applications, thus the database ID is never copied into a RESTful
	 * resource.
	 */
	@Test
	void shouldCopyOnlyExposedProperties() {
		UserInfoEntityBean bean = new UserInfoEntityBean();
		bean.setResourceId(randomSeries.nextResourceId());
		bean.setText("hello, world");
		bean.setId(100L);

		UserInfo pojo = converterUnderTest.convert(bean);
		assertThat(pojo.getResourceId()).isEqualTo(bean.getResourceId());
		assertThat(pojo.getText()).isEqualTo(bean.getText());
	}

	// ------------------------------------------------------------------
	// Helper methods
	// ------------------------------------------------------------------
	private boolean fieldsMatch(UserInfoEntityBean expected, UserInfo actual) {
		if (!Objects.equals(expected.getResourceId(), actual.getResourceId()))
			return false;
		if (!Objects.equals(expected.getText(), actual.getText()))
			return false;
		return true;
	}
}
