/*
 * Copyright 2022 [CopyrightOwner]
 */

package com.fvogel.broadcomcc1.endpoint.userinfo;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import com.fvogel.broadcomcc1.math.SecureRandomSeries;
import com.fvogel.broadcomcc1.validation.OnCreate;
import com.fvogel.broadcomcc1.validation.OnUpdate;
import com.fvogel.broadcomcc1.exception.ResourceNotFoundException;
import com.fvogel.broadcomcc1.exception.UnprocessableEntityException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserInfoService {

	private final ApplicationEventPublisher publisher;
	private final ConversionService conversionService;
	private final UserInfoRepository repository;
	private final SecureRandomSeries secureRandom;

	/*
	 * Constructor
	 */
	@Autowired
	public UserInfoService(UserInfoRepository userInfoRepository,
			@Qualifier("userInfoConverter") ConversionService conversionService, ApplicationEventPublisher publisher,
			SecureRandomSeries secureRandom) {
		this.repository = userInfoRepository;
		this.conversionService = conversionService;
		this.publisher = publisher;
		this.secureRandom = secureRandom;
	}

	/*
	 * findAll
	 */
	public Flux<UserInfo> findAllUserInfos() {
		return Flux.from(repository.findAll().map(ejb -> conversionService.convert(ejb, UserInfo.class)));
	}

	/**
	 * findByResourceId
	 */
	public Mono<UserInfo> findUserInfoByResourceId(String id) throws ResourceNotFoundException {
		Mono<UserInfoEntityBean> monoItem = findByResourceId(id);
		return monoItem.flatMap(it -> Mono.just(conversionService.convert(it, UserInfo.class)));
	}

	/*
	 * findAllByText
	 */
	public Flux<UserInfo> findAllByText(@NonNull String text) {
		return Flux.from(repository.findAllByText(text).map(ejb -> conversionService.convert(ejb, UserInfo.class)));
	}

	/**
	 * Create
	 */
	public Mono<String> createUserInfo(@NonNull @Validated(OnCreate.class) UserInfo resource) {
		UserInfoEntityBean entity = conversionService.convert(resource, UserInfoEntityBean.class);
		if (entity == null) {
			log.error("This POJO yielded a null value when converted to an entity bean: {}", resource);
			throw new UnprocessableEntityException();
		}
		entity.setResourceId(secureRandom.nextResourceId());
		return repository.save(entity).doOnSuccess(item -> publishEvent(UserInfoEvent.CREATED, item))
				.flatMap(item -> Mono.just(item.getResourceId()));
	}

	/**
	 * Update
	 */
	public void updateUserInfo(@NonNull @Validated(OnUpdate.class) UserInfo resource) {
		Mono<UserInfoEntityBean> entityBean = findByResourceId(resource.getResourceId());
		entityBean.subscribe(value -> {
			// As fields are added to the entity, this block has to be updated
			value.setText(resource.getText());

			repository.save(value).doOnSuccess(item -> publishEvent(UserInfoEvent.UPDATED, item)).subscribe();
		});
	}

	/**
	 * Delete
	 */
	public void deleteUserInfoByResourceId(@NonNull String id) {
		repository.deleteByResourceId(id).doOnSuccess(item -> publishDeleteEvent(UserInfoEvent.DELETED, id))
				.subscribe();
	}

	/**
	 * Find the EJB having the given resourceId
	 */
	Mono<UserInfoEntityBean> findByResourceId(String id) throws ResourceNotFoundException {
		return repository.findByResourceId(id).switchIfEmpty(Mono.defer(() -> Mono.error(
				new ResourceNotFoundException(String.format("Entity not found with the given resourceId: %s", id)))));
	}

	/**
	 * Publish events
	 */
	private void publishEvent(String event, UserInfoEntityBean entity) {
		log.debug("publishEvent: {}, resourceId: {}", event, entity.getResourceId());
		this.publisher.publishEvent(new UserInfoEvent(event, conversionService.convert(entity, UserInfo.class)));
	}

	private void publishDeleteEvent(String event, String resourceId) {
		this.publisher.publishEvent(new UserInfoEvent(event, UserInfo.builder().resourceId(resourceId).build()));
	}
}