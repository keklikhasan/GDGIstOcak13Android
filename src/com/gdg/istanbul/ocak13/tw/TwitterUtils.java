package com.gdg.istanbul.ocak13.tw;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.gdg.istanbul.ocak13.R;
import com.gdg.istanbul.ocak13.utils.Constants;

public class TwitterUtils {

	public static String getUserName(Context mContext, SharedPreferences mPref)
			throws Exception {
		String token = mPref.getString(Constants.PREF_TWITTER_AUTH_TOKEN, "");
		String secret = mPref.getString(Constants.PREF_TWITTER_AUTH_SECRET, "");
		if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(secret)) {
			Twitter tw = TwitterFactory.getSingleton();
			try {
				tw.setOAuthConsumer(
						mContext.getString(R.string.tw_consumer_key),
						mContext.getString(R.string.tw_consumer_secret));
			} catch (Exception e) {
				e.printStackTrace();
			}
			AccessToken tok = new AccessToken(token, secret);
			tw.setOAuthAccessToken(tok);

			return tw.getScreenName();
		}
		return null;
	}
}
