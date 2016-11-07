package com.lorentzos.swipecards.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

/**
 * A member data object.
 */
public class Member implements Parcelable {

	String login;
	@Json(name = "avatar_url")
	String avatarUrl;

	private Member(Parcel in) {
		login = in.readString();
		avatarUrl = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(login);
		dest.writeString(avatarUrl);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>() {
		@Override
		public Member createFromParcel(Parcel in) {
			return new Member(in);
		}

		@Override
		public Member[] newArray(int size) {
			return new Member[size];
		}
	};
}