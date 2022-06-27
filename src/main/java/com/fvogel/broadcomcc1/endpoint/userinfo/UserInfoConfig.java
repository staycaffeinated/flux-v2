/*
 * Copyright 2022 [CopyrightOwner]
 */

package com.fvogel.broadcomcc1.endpoint.userinfo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;

/**
 * Configures of the converters, since WebFlux does not automatically do this.
 */
@Configuration
public class UserInfoConfig {

	@Bean("userInfoConverter")
	ConversionService conversionService() {
		var factory = new ConversionServiceFactoryBean();
		Set<Converter<?, ?>> convSet = new HashSet<>();
		convSet.add(new UserInfoBeanToResourceConverter());
		convSet.add(new UserInfoResourceToBeanConverter());
		factory.setConverters(convSet);
		factory.afterPropertiesSet();
		return factory.getObject();
	}
}