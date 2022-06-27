/*
 * Copyright 2022 [CopyrightOwner]
 */

package com.fvogel.broadcomcc1.endpoint.userinfo;

class UserInfoGenerator {
	static UserInfo generateUserInfo() {
		return UserInfo.builder().text("sample text").build();
	}
}