/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.common;

/**
 * Spring profiles
 */
public final class Constants {
	private Constants() {
	}

	public static final String PROFILE_PROD = "prod";
	public static final String PROFILE_NOT_PROD = "!" + PROFILE_PROD;
	public static final String PROFILE_TEST = "test";
	public static final String PROFILE_IT = "integration-test";
}