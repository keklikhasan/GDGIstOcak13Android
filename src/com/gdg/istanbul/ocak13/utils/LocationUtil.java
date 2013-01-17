package com.gdg.istanbul.ocak13.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtil {
	public static Location getLastBestLocation(Context context) {
		// Criteria criteria = new Criteria();
		// provider = locationManager.getBestProvider(criteria, false);
		LocationManager mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location locationGPS = mLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location locationNet = mLocationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		long GPSLocationTime = 0;
		if (null != locationGPS) {
			GPSLocationTime = locationGPS.getTime();
		}

		long NetLocationTime = 0;

		if (null != locationNet) {
			NetLocationTime = locationNet.getTime();
		}

		if (0 < GPSLocationTime - NetLocationTime) {
			return locationGPS;
		} else {
			return locationNet;
		}
	}
}
