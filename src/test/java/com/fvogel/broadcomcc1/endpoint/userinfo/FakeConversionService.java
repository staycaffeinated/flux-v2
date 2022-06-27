/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.userinfo;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Creates a ConversionService instance suitable for testing
 */
public class FakeConversionService {

	static ConversionService build() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter(new UserInfoBeanToResourceConverter());
		service.addConverter(new UserInfoResourceToBeanConverter());
		return service;
	}
}
