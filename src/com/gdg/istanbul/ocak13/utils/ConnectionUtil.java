package com.gdg.istanbul.ocak13.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUtil {

	public static boolean checkReachable() {
		boolean result = false;
		try {
			HttpGet request = new HttpGet("http://www.google.com");

			HttpParams httpParameters = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpClient.execute(request);

			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public static boolean checkInternetWM(Context context) {
		Object systemService = context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		ConnectivityManager connect = (ConnectivityManager) systemService;
		if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			return checkReachable();
		}
		return false;
	}

	public static boolean checkInternetW(Context context) {
		Object systemService = context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		ConnectivityManager connect = (ConnectivityManager) systemService;
		if (connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			return checkReachable();
		}
		return false;
	}

	public static boolean checkInternetM(Context context) {
		Object systemService = context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		ConnectivityManager connect = (ConnectivityManager) systemService;
		if (connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
			return checkReachable();
		}
		return false;
	}

}
