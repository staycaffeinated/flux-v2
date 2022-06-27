/*
 * Copyright 2022 [CopyrightOwner]
 */

package com.fvogel.broadcomcc1.endpoint.userinfo;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converts Drink entity beans into DrinkResource objects
 */
@Component
public class UserInfoResourceToBeanConverter implements Converter<UserInfo, UserInfoEntityBean> {
	/**
	 * Convert the source object of type {@code UserInfo to target type {@code
	 * UserInfoEntityBean}.
	 *
	 * @param resource the source object to convert, which must be an instance of
	 * {@code UserInfo} (never {@code null})
	 * 
	 * @return the converted object, which must be an instance of
	 *         {@code UserInfoEntityBean} (potentially {@code null})
	 * @throws IllegalArgumentException
	 *             if the source cannot be converted to the desired target type
	 */
	@Override
	public UserInfoEntityBean convert(@NonNull UserInfo resource) {
		var target = new UserInfoEntityBean();
		target.setResourceId(resource.getResourceId());
		target.setText(resource.getText());
		return target;
	}

	/**
	 * Convert a list of RestfulResource objects into EJBs
	 */
	public List<UserInfoEntityBean> convert(@NonNull List<UserInfo> sourceList) {
		return sourceList.stream().map(this::convert).toList();
	}
}
