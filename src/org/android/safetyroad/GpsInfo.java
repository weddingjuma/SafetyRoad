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

	// ���� GPS ��� ����
	boolean isGPSEnadbled = false;

	// ��Ʈ��ũ ��� ����
	boolean isNetworkEnabled = false;

	// GPS status
	boolean isGetLocation = false;

	Location location;
	double lat; // ����
	double lon; // �浵

	// �ּ� GPS ���� ������Ʈ �Ÿ� 10����
	private static final long MIN_DISTANCE_CHANE_FOR_UPDATES = 10;

	// �ּ� GPS ���� ������Ʈ �ð�(�и������̹Ƿ� 1��)
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

	protected LocationManager locationMngr;

	public GpsInfo(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationMngr = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

			// GPS ���� ��������
			isGPSEnadbled = locationMngr.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// ���� ��Ʈ��ũ ���� �� �˾ƿ���
			isNetworkEnabled = locationMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnadbled && !isNetworkEnabled) {
				// GPS�� ��Ʈ��ũ ����� �������� ���� �� �ҽ� ����.
			} else {
				this.isGetLocation = true;
				// ��Ʈ��ũ �����κ��� ��ġ�� ��������
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

	// GPS ����
	public void stopUsingGPS() {
		if (locationMngr != null) {
			locationMngr.removeUpdates(GpsInfo.this);
		}
	}

	// �������� �����´�.
	public double getLatitude() {
		if (location != null) {
			lat = location.getLatitude();
		}
		return lat;
	}

	// �浵���� �����´�.
	public double getLongitude() {
		if (location != null) {
			lon = location.getLongitude();
		}
		return lon;
	}

	/**
	 * GPS �� wife ������ �����ִ��� Ȯ���մϴ�.
	 */
	public boolean isGetLocation() {
		return this.isGetLocation;
	}

	/**
     * GPS ������ �������� �������� 
     * ���������� ���� ����� alert â
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
 
        alertDialog.setTitle("GPS �����������");
        alertDialog.setMessage("GPS ������ ���� �ʾ������� �ֽ��ϴ�.\n ����â���� ���ðڽ��ϱ�?");
   
        // OK �� ������ �Ǹ� ����â���� �̵��մϴ�. 
        alertDialog.setPositiveButton("Settings", 
                                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        // Cancle �ϸ� ���� �մϴ�. 
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
