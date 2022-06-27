/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * A formalized wrapper for a resource identifier
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceIdentity {
	@NonNull
	private String resourceId;
}