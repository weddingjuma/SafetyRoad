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

	// 현재 GPS 사용 유무
	boolean isGPSEnadbled = false;

	// 네트워크 사용 유무
	boolean isNetworkEnabled = false;

	// GPS status
	boolean isGetLocation = false;

	Location location;
	double lat; // 위도
	double lon; // 경도

	// 최소 GPS 정보 업데이트 거리 10미터
	private static final long MIN_DISTANCE_CHANE_FOR_UPDATES = 10;

	// 최소 GPS 정보 업데이트 시간(밀리세컨이므로 1분)
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

	protected LocationManager locationMngr;

	public GpsInfo(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationMngr = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

			// GPS 정보 가져오기
			isGPSEnadbled = locationMngr.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// 현재 네트워크 상태 값 알아오기
			isNetworkEnabled = locationMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnadbled && !isNetworkEnabled) {
				// GPS와 네트워크 사용이 가능하지 않을 때 소스 구현.
			} else {
				this.isGetLocation = true;
				// 네트워크 정보로부터 위치값 가져오기
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

	// GPS 종료
	public void stopUsingGPS() {
		if (locationMngr != null) {
			locationMngr.removeUpdates(GpsInfo.this);
		}
	}

	// 위도값을 가져온다.
	public double getLatitude() {
		if (location != null) {
			lat = location.getLatitude();
		}
		return lat;
	}

	// 경도값을 가져온다.
	public double getLongitude() {
		if (location != null) {
			lon = location.getLongitude();
		}
		return lon;
	}

	/**
	 * GPS 나 wife 정보가 켜져있는지 확인합니다.
	 */
	public boolean isGetLocation() {
		return this.isGetLocation;
	}

	/**
     * GPS 정보를 가져오지 못했을때 
     * 설정값으로 갈지 물어보는 alert 창
     * */
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
