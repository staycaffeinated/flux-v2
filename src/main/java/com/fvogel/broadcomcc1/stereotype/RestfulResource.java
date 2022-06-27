/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.stereotype;

/**
 * A stereotype for RESTful resources
 */
public interface RestfulResource<T> {
	T getResourceId();
}
