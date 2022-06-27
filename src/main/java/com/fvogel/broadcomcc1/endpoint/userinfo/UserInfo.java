/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.endpoint.userinfo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import com.fvogel.broadcomcc1.stereotype.*;
import com.fvogel.broadcomcc1.validation.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * This is the POJO of UserInfo data exposed to client applications
 */
@lombok.Data
// The next 2 lines allow jackson-databind and lombok to play nice together.
// These 2 lines specifically resolve this exception:
// com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot
// construct instance...
// See https://www.thecuriousdev.org/lombok-builder-with-jackson/
@JsonDeserialize(builder = UserInfo.DefaultBuilder.class)
@Builder(builderClassName = "DefaultBuilder", toBuilder = true)
public class UserInfo implements RestfulResource<String> {

	@Null(groups = OnCreate.class)
	@NotNull(groups = OnUpdate.class)
	@ResourceId
	private String resourceId;

	/*
	 * An @Pattern can also be used here. The @Alphabet is used to demonstrate a
	 * custom validator
	 */
	@NotEmpty
	@Alphabetic
	private String text;

	/**
	 * Added to enable Lombok and jackson-databind to play nice together
	 */
	@JsonPOJOBuilder(withPrefix = "")
	public static class DefaultBuilder {
		// empty
	}
}