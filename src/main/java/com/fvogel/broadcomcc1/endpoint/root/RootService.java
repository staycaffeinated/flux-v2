/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.root;

import org.springframework.stereotype.Service;

/**
 * Empty implementation of a Service
 */
// Because sonarqube complains about doNothing returning a constant value
@SuppressWarnings("java:S3400")
@Service
public class RootService {

	int doNothing() {
		return 0;
	}
}
