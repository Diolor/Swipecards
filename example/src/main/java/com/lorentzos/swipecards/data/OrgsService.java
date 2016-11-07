package com.lorentzos.swipecards.data;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * A Organizations api service.
 */
public interface OrgsService {

	/**
	 * @param org the organization name
	 * @return a list of {@link Member} of the organization
	 */
	@GET("orgs/{org}/members")
	Single<List<Member>> listOrgMembers(@Path("org") String org);

}
