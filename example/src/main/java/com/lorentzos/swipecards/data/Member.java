package com.lorentzos.swipecards.data;

import com.squareup.moshi.Json;

/**
 * A member data object.
 */
public class Member {

	String login;
	@Json(name = "avatar_url")
	String avatarUrl;

}