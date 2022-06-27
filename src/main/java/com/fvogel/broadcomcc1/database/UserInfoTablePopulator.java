/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.database;

import com.fvogel.broadcomcc1.endpoint.userinfo.UserInfoEntityBean;
import com.fvogel.broadcomcc1.endpoint.userinfo.UserInfoRepository;
import com.fvogel.broadcomcc1.math.SecureRandomSeries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * This component populates the UserInfo database table with sample data. This
 * is suitable for testing and demonstration, but probably not what you want in
 * production.
 */
@Component
@Slf4j
public class UserInfoTablePopulator implements ApplicationListener<ApplicationReadyEvent> {

	private final UserInfoRepository repository;
	private final SecureRandomSeries randomSeries;

	/**
	 * Constructor
	 */
	public UserInfoTablePopulator(UserInfoRepository repository, SecureRandomSeries secureRandom) {
		this.repository = repository;
		this.randomSeries = secureRandom;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		repository.deleteAll()
				.thenMany(Flux.just("One", "Two", "Three", "Four", "Five").map(this::buildSampleRecord)
						.flatMap(repository::save))
				.thenMany(repository.findAll()).subscribe(pet -> log.info("Saving " + pet.toString()));
	}

	/**
	 * Creates a sample database record
	 */
	private UserInfoEntityBean buildSampleRecord(String text) {
		return UserInfoEntityBean.builder().resourceId(randomSeries.nextResourceId()).text(text).build();
	}
}