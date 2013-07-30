package com.jl.crm.android;

import android.content.SharedPreferences;

public class AccessTokenClient {

	private SharedPreferences sharedPreferences;
	private String accessTokenKey = "accessToken";

	public AccessTokenClient(SharedPreferences sharedPreferences, String accessTokenKey) {
		this.accessTokenKey = accessTokenKey;
		this.sharedPreferences = sharedPreferences;
	}

	public AccessTokenClient(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	public String readAccessTokenKey() {
		return this.sharedPreferences.getString(this.accessTokenKey, null);
	}

	public void writeAccessTokenKey(String at) {
		SharedPreferences.Editor editor = this.sharedPreferences.edit();
		editor.putString(this.accessTokenKey, at);
		editor.commit();
	}

}
