package org.android.safetyroad;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class GpsInfo extends Service implements LocationListener {

	private Context mContext;

	// Gps enable or not
	boolean isGPSEnadbled = false;

	// Network enable or not

	boolean isNetworkEnabled = false;

	// GPS status
	boolean isGetLocation = false;

	Location location;

	double lat; 
	double lon; 

	// Gps info update MIN distance
	private static final long MIN_DISTANCE_CHANE_FOR_UPDATES = 10;

	// Gps info update MIN time(milli)

	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

	protected LocationManager locationMngr;

	public GpsInfo(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationMngr = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

			// get the GPS info
			isGPSEnadbled = locationMngr.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// get the Network info
			isNetworkEnabled = locationMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnadbled && !isNetworkEnabled) {
				// When using GPS and network is not available
			} else {
				this.isGetLocation = true;
				// get the location info using Network

				if (isNetworkEnabled) {
					locationMngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANE_FOR_UPDATES, this);

					if (locationMngr != null) {
						location = locationMngr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							lat = location.getLatitude();
							lon = location.getLongitude();
						}
					}
				}

				if (isGPSEnadbled) {
					if (location == null) {
						locationMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANE_FOR_UPDATES, this);
						if (locationMngr != null) {
							location = locationMngr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								lat = location.getLatitude();
								lon = location.getLongitude();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	public void stopUsingGPS() {
		if (locationMngr != null) {
			locationMngr.removeUpdates(GpsInfo.this);
		}
	}

	public double getLatitude() {
		if (location != null) {
			lat = location.getLatitude();
		}
		return lat;
	}

	public double getLongitude() {
		if (location != null) {
			lon = location.getLongitude();
		}
		return lon;
	}

	/**
	 * Check the Gps or Wifi info.
	 */
	public boolean isGetLocation() {
		return this.isGetLocation;
	}

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
 
        alertDialog.setTitle("GPS 사용유무셋팅");

        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

   
        // OK 를 누르게 되면 설정창으로 이동합니다. 
        alertDialog.setPositiveButton("Settings", 
                                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        // Cancle 하면 종료 합니다. 
        alertDialog.setNegativeButton("Cancel", 
                              new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        alertDialog.show();
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
