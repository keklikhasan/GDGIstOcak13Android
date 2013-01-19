package com.gdg.istanbul.ocak13;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gdg.istanbul.ocak13.R;
import com.gdg.istanbul.ocak13.api.GDGApi;
import com.gdg.istanbul.ocak13.api.Record;
import com.gdg.istanbul.ocak13.map.MapDialogUtil;
import com.gdg.istanbul.ocak13.utils.Constants;
import com.gdg.istanbul.ocak13.utils.LanguageUtil;
import com.gdg.istanbul.ocak13.utils.LocationUtil;
import com.gdg.istanbul.ocak13.utils.MessageUtil;
import com.gdg.istanbul.ocak13.utils.PropertiesUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("HandlerLeak")
public class MapsActivityv2 extends FragmentActivity {
	// tasks
	private GetDataTask mGetDataTask = null;
	private Activity mContext;
	private static MessageUtil messageUtil;
	private SharedPreferences mPreferences = null;

	//
	private String mUserName;
	private static List<Record> records;

	// UI references.
	private GoogleMap mMap;
	private Location mLocation;
	private static boolean lifeCyleStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageUtil.setApplicationLanguage(this);
		setContentView(R.layout.activity_maps_v2);
		mContext = this;
		messageUtil = new MessageUtil(mContext);
		mPreferences = PropertiesUtil.getSharedPreferences(mContext);
		mUserName = mPreferences.getString(Constants.PREF_USERNAME, null);
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapview)).getMap();
		lifeCyleStatus = false;

		// check maps api v2 availability
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

		refresh();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (lifeCyleStatus) {
			lifeCyleStatus = false;
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
		getMenuInflater().inflate(R.menu.menu_map_v2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit: {
			finish();
			System.exit(0);
			break;
		}
		case R.id.menu_settings: {
			Intent intent = new Intent(mContext, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.menu_map_v1: {
			Intent intent = new Intent(mContext, MapsActivityv1.class);
			intent.putExtra(Constants.EXTRA_USERNAME, mUserName);
			startActivity(intent);
			finish();
			break;
		}
		case R.id.menu_new: {
			Intent intent = new Intent(mContext, NewRecordActivity.class);
			intent.putExtra(Constants.EXTRA_USERNAME, mUserName);
			startActivity(intent);
			finish();
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
			records = GDGApi.getRecords(mContext, messageUtil);
			// try {
			// records = new ArrayList<Record>();
			// Record rec = new Record(
			// new JSONObject(
			// "{\"pic\": \"https://developers.google.com/maps/documentation/android/images/marker-infowindows.png\",\"title\": \"ilk baslik\",  \"body\": \"ilk içerik\", \"user\": \"salim\",  \"_id\": \"50ecbf19131a73e01e000002\", \"__v\": 0,  \"createdAt\": \"2013-01-09T00:51:37.669Z\",  \"loc\": [ 41.2565, 29.23554 ]}"));
			// records.add(rec);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			getDataHandler.sendEmptyMessage(-1);
			return false;
		}
	}

	private Handler getDataHandler = new Handler() {
		@SuppressWarnings("unused")
		@Override
		public void handleMessage(Message msg) {
			try {

				mMap.clear();
				/*
				 * Where i am
				 */
				LatLng mePoint = new LatLng(getLocation().getLatitude(),
						getLocation().getLongitude());
				Marker meMarker = mMap.addMarker(new MarkerOptions()
						.position(mePoint)
						.title("here")
						.snippet("I'm here")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.me)));
				if (records != null && records.size() > 0) {
					for (int i = 0; i < records.size(); i++) {
						Record rec = records.get(i);
						int drawable = R.drawable.other;
						if (rec.user.equals(mUserName)) {
							drawable = R.drawable.my_post;
						} else {
							drawable = R.drawable.other;
						}

						LatLng itemPoint = new LatLng(rec.locLatitude,
								rec.locLongitude);
						Marker itemMarker = mMap.addMarker(new MarkerOptions()
								.position(itemPoint)
								.title(i + "")
								.snippet(rec.body)
								.icon(BitmapDescriptorFactory
										.fromResource(drawable)));
					}
				} else {
					messageUtil
							.showToastMessage(getString(R.string.error_no_records));
				}
				mMap.setInfoWindowAdapter(infoAdapter);
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mePoint,
						10));
			} catch (Exception e) {
				e.printStackTrace();
				messageUtil
						.showToastMessage(getString(R.string.error_no_unknown)
								+ e.toString());
			} finally {
				messageUtil.showProgress(false, null);
			}
		}

	};

	/*
	 * show info adapter
	 */
	InfoWindowAdapter infoAdapter = new InfoWindowAdapter() {

		@Override
		public View getInfoWindow(Marker marker) {

			if (!marker.getTitle().equals("here")) {
				try {
					Record rec = records
							.get(Integer.parseInt(marker.getTitle()));

					// View v = getLayoutInflater().inflate(
					// R.layout.maps_dialogv2, null);
					//
					// // Set Title
					// TextView mapsTitle = (TextView) v
					// .findViewById(R.id.map_title);
					// mapsTitle.setText(rec.title);
					//
					// // Set user
					// TextView mapsUser = (TextView) v
					// .findViewById(R.id.map_user);
					// mapsUser.setText(rec.user);
					//
					// // Set Content
					// TextView mapsContent = (TextView) v
					// .findViewById(R.id.map_content);
					// mapsContent.setText(rec.body);
					//
					// // Set Date
					// TextView mapsDate = (TextView) v
					// .findViewById(R.id.map_date);
					// mapsDate.setText(rec.createdAt);
					// return v;
					/*
					 * Show Popup
					 */

					View mapInfoView = getLayoutInflater().inflate(
							R.layout.maps_dialog, null);
					MapDialogUtil dialogUtil = new MapDialogUtil(mContext);
					dialogUtil.showDialog(rec, mapInfoView);
					/*
					 * 
					 */

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			View v = getLayoutInflater().inflate(R.layout.emptyview, null);
			return v;
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}
	};

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
}
