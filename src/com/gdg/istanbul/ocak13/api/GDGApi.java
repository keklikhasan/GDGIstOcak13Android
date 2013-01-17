package com.gdg.istanbul.ocak13.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.gdg.istanbul.ocak13.R;
import com.gdg.istanbul.ocak13.utils.Constants;
import com.gdg.istanbul.ocak13.utils.MessageUtil;
import com.gdg.istanbul.ocak13.utils.PropertiesUtil;

public class GDGApi {

	public static final String defaultUrl = "http://amazon.minikod.com/";
	public static final String url_new = "api/activity/new";
	public static final String url_get_all = "api/activity/";

	public static String getUrl(Context mContext, String last) {
		String url = PropertiesUtil.getSharedPreferences(mContext).getString(
				Constants.PREF_API_URL, defaultUrl);
		if (TextUtils.isEmpty(url)) {
			url = defaultUrl;
		}

		if (!url.endsWith("/")) {
			url = url + "/";
		}

		return url = url + last;

	}

	public static boolean newRecord(Context mContext, MessageUtil messageUtil,
			String mTitleText, String mContentText, String mUserName,
			String mLocationString, byte[] bitmapdata) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(getUrl(mContext, url_new));
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("title", mTitleText));
			nameValuePairs.add(new BasicNameValuePair("body", mContentText));
			nameValuePairs.add(new BasicNameValuePair("user", mUserName));
			nameValuePairs.add(new BasicNameValuePair("geo", mLocationString));
			nameValuePairs.add(new BasicNameValuePair("file", Base64
					.encodeToString(bitmapdata, Base64.DEFAULT)));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			String resp = Constants.convertStreamToString(is);
			if (resp != null) {
				if (resp.contains("true")) {
					messageUtil.showToastMessage(mContext
							.getString(R.string.new_record_info_suc));
					return true;
				} else {
					messageUtil.showToastMessage(mContext
							.getString(R.string.new_record_error_unkown_error));
				}
			} else {
				Log.e("Response", "Empty!!!!");
				messageUtil.showToastMessage(mContext
						.getString(R.string.new_record_error_unkown_error));
			}
		} catch (ClientProtocolException e) {
			messageUtil.showToastMessage(mContext
					.getString(R.string.new_record_error_unkown_error));
			e.printStackTrace();
		} catch (IOException e) {
			messageUtil.showToastMessage(mContext
					.getString(R.string.new_record_error_unkown_error));
			e.printStackTrace();
		}
		return false;
	}

	public static List<Record> getRecords(Context mContext,
			MessageUtil messageUtil) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(getUrl(mContext, url_get_all));
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			String resp = Constants.convertStreamToString(is);
			if (resp != null) {
				JSONArray array = new JSONArray(resp);
				if (array != null && array.length() > 0) {
					List<Record> records = new ArrayList<Record>();
					for (int i = 0; i < array.length(); i++) {
						try {
							Record res = new Record(array.getJSONObject(i));
							records.add(res);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (records != null && records.size() > 0)
						return records;
				}
			} else {
				Log.e("Response", "Empty!!!!");
				messageUtil.showToastMessage(mContext
						.getString(R.string.new_record_error_unkown_error));
			}
		} catch (Exception e) {
			messageUtil.showToastMessage(mContext
					.getString(R.string.new_record_error_unkown_error));
			e.printStackTrace();
		}
		return null;
	}

}
