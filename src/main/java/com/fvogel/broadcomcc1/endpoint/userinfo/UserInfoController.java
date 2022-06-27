/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.userinfo;

import com.fvogel.broadcomcc1.exception.*;
import com.fvogel.broadcomcc1.common.ResourceIdentity;
import com.fvogel.broadcomcc1.validation.OnCreate;
import com.fvogel.broadcomcc1.validation.OnUpdate;
import com.fvogel.broadcomcc1.validation.ResourceId;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.time.Duration;

@RestController
@RequestMapping("")
@Slf4j
public class UserInfoController {

	private final UserInfoService userInfoService;

	/*
	 * Constructor
	 */
	@Autowired
	public UserInfoController(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	/*
	 * Get all
	 */
	@GetMapping(value = UserInfoRoutes.FIND_ALL_USERINFO, produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<UserInfo> getAllUserInfos() {
		return userInfoService.findAllUserInfos();
	}

	/*
	 * Get one by resourceId
	 *
	 */
	@GetMapping(value = UserInfoRoutes.FIND_ONE_USERINFO, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<UserInfo> getUserInfoById(@PathVariable @ResourceId String id) {
		return userInfoService.findUserInfoByResourceId(id);
	}

	/**
	 * If api needs to push items as Streams to ensure Backpressure is applied, we
	 * need to set produces to MediaType.TEXT_EVENT_STREAM_VALUE
	 *
	 * MediaType.TEXT_EVENT_STREAM_VALUE is the official media type for Server Sent
	 * Events (SSE) MediaType.APPLICATION_STREAM_JSON_VALUE is for server to
	 * server/http client communications.
	 *
	 * https://stackoverflow.com/questions/52098863/whats-the-difference-between-text-event-stream-and-application-streamjson
	 *
	 */
	@GetMapping(value = UserInfoRoutes.STREAM_USERINFO, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public Flux<UserInfo> getUserInfoStream() {
		// This is only an example implementation. Modify this line as needed.
		return userInfoService.findAllUserInfos().delayElements(Duration.ofMillis(250));
	}

	/*
	 * Create
	 */
	@PostMapping(value = UserInfoRoutes.CREATE_USERINFO, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ResponseEntity<ResourceIdentity>> createUserInfo(
			@RequestBody @Validated(OnCreate.class) UserInfo resource) {
		Mono<String> id = userInfoService.createUserInfo(resource);
		return id.map(value -> ResponseEntity.status(HttpStatus.CREATED).body(new ResourceIdentity(value)));
	}

	/*
	 * Update by resourceId
	 */
	@PutMapping(value = UserInfoRoutes.UPDATE_USERINFO, produces = MediaType.APPLICATION_JSON_VALUE)
	public void updateUserInfo(@PathVariable @ResourceId String id,
			@RequestBody @Validated(OnUpdate.class) UserInfo userInfo) {
		if (!Objects.equals(id, userInfo.getResourceId())) {
			log.error("Update declined: mismatch between query string identifier, {}, and resource identifier, {}", id,
					userInfo.getResourceId());
			throw new UnprocessableEntityException("Mismatch between the identifiers in the URI and the payload");
		}
		userInfoService.updateUserInfo(userInfo);
	}

	/*
	 * Delete one
	 */
	@DeleteMapping(value = UserInfoRoutes.DELETE_USERINFO)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUserInfo(@PathVariable @ResourceId String id) {
		Mono<UserInfo> resource = userInfoService.findUserInfoByResourceId(id);
		resource.subscribe(value -> userInfoService.deleteUserInfoByResourceId(id));
	}
}