package org.android.safetyroad;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

public class FindGeo {
	
	private Context mContext;
	//private GeoPoint point;
	private Geocoder geocoder;
	
	public FindGeo(Context context) {
		this.mContext = context;
	}
	
	/*// 주소로 위도, 경도 취득
	public GeoPoint findGeoPoint(String address) {
		geocoder = new Geocoder(mContext);
		Address addr;

		try {
			List<Address> listAddress = geocoder.getFromLocationName(address, 1);
			if (listAddress.size() > 0) { // if address found
				addr = listAddress.get(0); // in Address format
				int lat = (int) (addr.getLatitude() * 1E6);
				int lng = (int) (addr.getLongitude() * 1E6);

				point = new GeoPoint(lat, lng);

				Toast.makeText(mContext, "주소로부터 취득한 위도 : " + lat / 1E6 + ", 경도 : " + lng / 1E6,
						Toast.LENGTH_SHORT).show();
				// Log.d(TAG, "주소로부터 취득한 위도 : " + lat + ", 경도 : " + lng);
			} else
				Toast.makeText(mContext, "Address Converting Fail", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return point;
	}*/

	
	/**
	 * Find String address using latitude & longitude 
	 * params - double lat & lng
	 * return value - String address
	 */
	public String findAddress(double lat, double lng) {
		StringBuffer bf = new StringBuffer();
		geocoder = new Geocoder(mContext, Locale.KOREA);
		String currentLocationAddress;
		List<Address> address;
		try {
			if (geocoder != null) {
				// 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
				address = geocoder.getFromLocation(lat, lng, 1);
				// 설정한 데이터로 주소가 리턴된 데이터가 있으면
				if (address != null && address.size() > 0) {
					// 주소
					currentLocationAddress = address.get(0).getAddressLine(0).toString();

					// 전송할 주소 데이터 (위도/경도 포함 편집)
					bf.append(currentLocationAddress);// .append(" ");
					// bf.append(lat).append(",");
					// bf.append(lng);
				}
			}

		} catch (IOException e) {
			Toast.makeText(mContext, "주소취득 실패", Toast.LENGTH_LONG).show();

			e.printStackTrace();
		}
		return bf.toString();
	}

}
