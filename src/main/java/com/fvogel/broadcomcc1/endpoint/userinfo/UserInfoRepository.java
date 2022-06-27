/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.userinfo;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserInfoRepository extends ReactiveSortingRepository<UserInfoEntityBean, Long> {

	// Find by the resource ID known by external applications
	Mono<UserInfoEntityBean> findByResourceId(String id);

	// Find by the database ID
	Mono<UserInfoEntityBean> findById(Long id);

	/* returns the number of entities deleted */
	Mono<Long> deleteByResourceId(String id);

	Flux<UserInfoEntityBean> findAllByText(String text);
}
