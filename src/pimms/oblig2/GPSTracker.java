package pimms.oblig2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {
	private static final String TAG = "GPSTracker";
	
	String mGpsProvider;
	
	LocationManager mLocationManager;
	Location mLocation;
	
	private Context mContext;
	
	// Have the application registered for GPS updates?
	private boolean mLocSubscription;
	
	public GPSTracker(Context ctx) {
		mContext = ctx;
		mLocationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
		
		if (servicesAvailable()) {
			subscribeLocation();
		} else {
			Log.e("DBG", "GPS services are not available");
		}
	}
	
	/*
	 * Check if GPS services are available through either the GPS provider
	 * or the network provider. 
	 * 'mLocSubscription' is set to false if no provider is available.
	 */
	public boolean servicesAvailable() {
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mGpsProvider = LocationManager.GPS_PROVIDER;
		} else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			mGpsProvider = LocationManager.NETWORK_PROVIDER;
		} else {
			if (mLocSubscription) {
				mLocSubscription = false;
				mLocationManager.removeUpdates(this);
			}
			
			return false;
		}
		
		
		return true;
	}
	
	public Location getLocation() {
		if (!servicesAvailable()) {
			Log.e(TAG, "GPS Services are unavailable");
			return null;
		} 
		
		if (!mLocSubscription) {
			subscribeLocation();
			return null;
		}
		
		Location tmpLocation = mLocationManager.getLastKnownLocation(mGpsProvider);
		if (tmpLocation != null) {
			mLocation = tmpLocation;
		} else {
			Log.d(TAG, "Last known location is null (" + mGpsProvider + ")");
		}
		
		return mLocation;
	}
	
	
	private void subscribeLocation() {
		mLocationManager.requestLocationUpdates(mGpsProvider, 10000, 10, this);
		mLocSubscription = true;
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		Log.d(TAG, "Location changed!");
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Log.d(TAG, "Provider disabled: " + arg0);
		mGpsProvider = null;
		mLocSubscription = false;
	}
	
	@Override
	public void onProviderEnabled(String arg0) {
		Log.d(TAG, "Provider enabled: " + arg0);
		mGpsProvider = arg0;
	}
	
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
