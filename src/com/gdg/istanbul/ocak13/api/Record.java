package com.gdg.istanbul.ocak13.api;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

public class Record {
	public String title;
	public String body;
	public String user;
	public String id;
	public String createdAt;
	public double locLatitude;
	public double locLongitude;
	public String pic;

	public Record(JSONObject object) throws Exception {
		if (object != null) {

			if (object.has("loc")) {
				JSONArray loc = object.getJSONArray("loc");
				locLatitude = loc.getDouble(0);
				locLongitude = loc.getDouble(1);
			}else{
				locLatitude=41.110100d;
				locLongitude=29.031714d;
			}

			if (object.has("_id"))
				id = object.getString("_id");
			if (TextUtils.isEmpty(id))
				throw new Exception("couldn't be created. id empty");

			if (object.has("title"))
				title = object.getString("title");
			if (TextUtils.isEmpty(title))
				throw new Exception("couldn't be created. title empty");

			if (object.has("user"))
				user = object.getString("user");
			if (TextUtils.isEmpty(user))
				throw new Exception("couldn't be created. user empty");

			if (object.has("body"))
				body = object.getString("body");

			if (object.has("pic"))
				pic = object.getString("pic");

			if (object.has("createdAt"))
				createdAt = object.getString("createdAt");
		} else {
			throw new Exception("couldn't be created");
		}
	}
}
