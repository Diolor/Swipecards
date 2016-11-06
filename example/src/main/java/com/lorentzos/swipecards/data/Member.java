package com.lorentzos.swipecards.data;

import com.squareup.moshi.Json;

public class Member {

	public String login;
	@Json(name = "avatar_url")
	public String avatarUrl;

}