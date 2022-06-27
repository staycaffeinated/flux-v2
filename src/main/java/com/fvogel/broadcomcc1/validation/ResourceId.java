/*
 * Copyright 2022 [CopyrightOwner]
 */
package com.fvogel.broadcomcc1.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// See https://www.baeldung.com/spring-mvc-custom-validator

@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = ResourceIdValidator.class)
@Documented
public @interface ResourceId {
	String message() default "{ResourceId.invalid}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
