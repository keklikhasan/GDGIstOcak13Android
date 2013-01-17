package com.gdg.istanbul.ocak13.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Constants {

	public static final String EXTRA_USERNAME = "com.gdg.istanbul.ocak12.extra.USERNAME";
	public static final String EXTRA_MESSAGE = "com.gdg.istanbul.ocak12.extra.MESSAGE";
	public static final String EXTRA_SHOW = "com.gdg.istanbul.ocak12.extra.SHOW";


	public static final String PREF_API_URL = "com.gdg.istanbul.ocak12.pref.api.url";
	public static final String PREF_USERNAME = "com.gdg.istanbul.ocak12.pref.username";
	public static final String PREF_TWITTER_ID = "com.gdg.istanbul.ocak12.pref.tw_id";
	public static final String PREF_TWITTER_AUTH_TOKEN = "com.gdg.istanbul.ocak12.pref.tw_token";
	public static final String PREF_TWITTER_AUTH_SECRET = "com.gdg.istanbul.ocak12.pref.tw_secret";

	public static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append((line + "\n"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
