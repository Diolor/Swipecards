package com.lorentzos.swipecards.data;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Simple Github service
 */
public class GitHubService {

	private static final String BASE_URL = "https://api.github.com/";

	public static Service create() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(MoshiConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build();

		return retrofit.create(Service.class);
	}

	public interface Service {
		@GET("orgs/{org}/members")
		Single<List<Member>> listOrgMembers(@Path("org") String user);

	}

}
