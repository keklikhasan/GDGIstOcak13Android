package com.gdg.istanbul.ocak13;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gdg.istanbul.ocak13.R;
import com.gdg.istanbul.ocak13.api.GDGApi;
import com.gdg.istanbul.ocak13.api.Record;
import com.gdg.istanbul.ocak13.map.CustomItemizedOverlay;
import com.gdg.istanbul.ocak13.utils.Constants;
import com.gdg.istanbul.ocak13.utils.LanguageUtil;
import com.gdg.istanbul.ocak13.utils.LocationUtil;
import com.gdg.istanbul.ocak13.utils.MessageUtil;
import com.gdg.istanbul.ocak13.utils.PropertiesUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

@SuppressLint("HandlerLeak")
public class MapsActivityv1 extends MapActivity {

	// tasks
	private GetDataTask mGetDataTask = null;
	private Context mContext;
	private static MessageUtil messageUtil;
	private SharedPreferences mPreferences = null;

	//
	private static String mUserName;
	private static List<Record> records;

	// UI references.
	private MapView mMap;
	private Location mLocation;
	private static boolean lifeCyleStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageUtil.setApplicationLanguage(this);
		setContentView(R.layout.activity_maps_v1);
		mContext = this;
		messageUtil = new MessageUtil(mContext);
		mPreferences = PropertiesUtil.getSharedPreferences(mContext);
		mUserName = mPreferences.getString(Constants.PREF_USERNAME, null);
		mMap = (MapView) findViewById(R.id.mapview);
		lifeCyleStatus = false;
		if (!mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
			mMap.setBuiltInZoomControls(true);
		}
		refresh();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (lifeCyleStatus) {
			finish();
			startActivity(getIntent());
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		lifeCyleStatus = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_map_v1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit: {
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			break;
		}
		case R.id.menu_settings: {
			Intent intent = new Intent(mContext, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.menu_about: {
			Intent intent = new Intent(mContext, AboutUsActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.menu_map_v2: {
			Intent intent = new Intent(mContext, MapsActivityv2.class);
			intent.putExtra(Constants.EXTRA_USERNAME, mUserName);
			startActivity(intent);
			break;
		}
		case R.id.menu_new: {
			Intent intent = new Intent(mContext, NewRecordActivity.class);
			intent.putExtra(Constants.EXTRA_USERNAME, mUserName);
			startActivity(intent);
			break;
		}
		case R.id.menu_refresh: {
			refresh();
			break;
		}
		}
		return true;
	}

	private void refresh() {
		if (!messageUtil.isProgress()) {
			messageUtil.showProgress(true,
					getString(R.string.maps_progress_getting_data));
			mGetDataTask = new GetDataTask();
			mGetDataTask.execute((Void) null);
		}
	}

	public class GetDataTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			List<Overlay> mapOverlays = mMap.getOverlays();
			mapOverlays.clear();
			/*
			 * Where i am
			 */
			Drawable meDrawable = getResources().getDrawable(R.drawable.me);
			CustomItemizedOverlay meItemizedOverlay = new CustomItemizedOverlay(
					meDrawable, mContext);
			GeoPoint mePoint = new GeoPoint(
					(int) (getLocation().getLatitude() * 1e6),
					(int) (getLocation().getLongitude() * 1e6));
			OverlayItem meOverlayitem = new OverlayItem(mePoint, "Hello",
					"I'm here");
			meItemizedOverlay.addOverlay(meOverlayitem);
			mapOverlays.add(meItemizedOverlay);

			records = GDGApi.getRecords(mContext, messageUtil);
			if (records != null && records.size() > 0) {
				for (int i = 0; i < records.size(); i++) {
					Record rec = records.get(i);
					Drawable drawable = null;
					if (rec.user.equals(mUserName)) {
						drawable = getResources().getDrawable(
								R.drawable.my_post);
					} else {
						drawable = getResources().getDrawable(R.drawable.other);
					}
					CustomItemizedOverlay itemizedOverlay = new CustomItemizedOverlay(
							drawable, mContext);
					itemizedOverlay.record = rec;
					GeoPoint point = new GeoPoint(
							(int) (rec.locLatitude * 1e6),
							(int) (rec.locLongitude * 1e6));
					OverlayItem overlayitem = new OverlayItem(point, rec.title,
							rec.body);
					itemizedOverlay.addOverlay(overlayitem);
					mapOverlays.add(itemizedOverlay);
				}
			} else {
				messageUtil
						.showToastMessage(getString(R.string.error_no_records));
			}

			MapController mapController = mMap.getController();
			mapController.animateTo(mePoint);
			mapController.setZoom(12);

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			messageUtil.showProgress(false, null);

		}

		@Override
		protected void onCancelled() {
			messageUtil.showProgress(false, null);
		}
	}

	/*
	 * Location
	 */

	public Location getLocation() {
		mLocation = LocationUtil.getLastBestLocation(mContext);
		if (mLocation == null) {
			// Default location
			mLocation = new Location(LocationManager.GPS_PROVIDER);
			mLocation.setLatitude(41.110100);
			mLocation.setLongitude(29.031714);
		}
		return mLocation;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
