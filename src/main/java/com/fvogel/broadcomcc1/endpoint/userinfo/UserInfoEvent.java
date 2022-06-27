/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.userinfo;

import org.springframework.context.ApplicationEvent;

/**
 * UserInfo events
 */
@SuppressWarnings({"unused"})
public class UserInfoEvent extends ApplicationEvent {

	public static final String CREATED = "CREATED";
	public static final String UPDATED = "UPDATED";
	public static final String DELETED = "DELETED";

	private static final long serialVersionUID = 9152086626754282698L;

	private final String eventType;

	public UserInfoEvent(String eventType, UserInfo resource) {
		super(resource);
		this.eventType = eventType;
	}

	public String getEventType() {
		return eventType;
	}

}