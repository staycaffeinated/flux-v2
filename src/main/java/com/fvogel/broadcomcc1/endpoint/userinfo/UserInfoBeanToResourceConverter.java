/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.userinfo;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserInfoBeanToResourceConverter implements Converter<UserInfoEntityBean, UserInfo> {

	/**
	 * Convert the source object of type {@code UserInfo} to target type
	 * {@code UserInfoResource}.
	 *
	 * @param source
	 *            the source object to convert, which must be an instance of
	 *            {@code UserInfo} (never {@code null})
	 * @return the converted object, which must be an instance of
	 *         {@code UserInfoResource} (potentially {@code null})
	 * @throws IllegalArgumentException
	 *             if the source cannot be converted to the desired target type
	 */
	@Override
	public UserInfo convert(@NonNull UserInfoEntityBean source) {
		return UserInfo.builder().resourceId(source.getResourceId()).text(source.getText()).build();
	}

	/**
	 * Convert a list of EJBs into RestfulResource objects
	 */
	public List<UserInfo> convert(@NonNull List<UserInfoEntityBean> sourceList) {
		return sourceList.stream().map(this::convert).toList();
	}
}