/*
 * Copyright 2022 [CopyrightOwner]
 */

package com.fvogel.broadcomcc1.endpoint.userinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("UserInfo")
public class UserInfoEntityBean {

	/**
	 * SPECIAL NOTE: This entity is supported by two SQL files, found in the
	 * {@code src/main/resources/database/} directory of this project. One of those
	 * files defines the database schema and one defines sample values suitable for
	 * testing.
	 *
	 * If you rename any of these fields, or change their data type, or add new
	 * columns, those SQL files should be updated to mirror the changes made here.
	 * If you encounter SQL errors during integration testing or end-to-end testing,
	 * its probable this entity definition and those SQL files are out of sync.
	 *
	 * For integration tests, there is also a database initializer for this entity's
	 * underlying database table in the {@code com.fvogel.broadcomcc1.config}
	 * directory.
	 */

	/**
	 * This is the identifier of this entity that is exposed to the outside world.
	 * This is a secure random value with at least 160 bits of entropy, with the
	 * goal of making it difficult for a hacker to guess. This is also a unique
	 * value in the database.
	 *
	 * This value is 48 or 49 characters long. In the SQL schema file, the column
	 * type is defined as CHAR(50), which is simply a round-up to the next even byte
	 * length.
	 */
	@Column(value = "resource_id")
	private String resourceId;

	/**
	 * This is the database identifier. Naturally, this value should never be
	 * exposed outside the boundaries of this application.
	 */
	@Id
	private Long id;

	@NotEmpty(message = "Text cannot be empty")
	@Column(value = "text")
	private String text;
}