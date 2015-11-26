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
	
	/*// �ּҷ� ����, �浵 ���
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

				Toast.makeText(mContext, "�ּҷκ��� ����� ���� : " + lat / 1E6 + ", �浵 : " + lng / 1E6,
						Toast.LENGTH_SHORT).show();
				// Log.d(TAG, "�ּҷκ��� ����� ���� : " + lat + ", �浵 : " + lng);
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
				// ����° �μ��� �ִ������ε� �ϳ��� ���Ϲ޵��� �����ߴ�
				address = geocoder.getFromLocation(lat, lng, 1);
				// ������ �����ͷ� �ּҰ� ���ϵ� �����Ͱ� ������
				if (address != null && address.size() > 0) {
					// �ּ�
					currentLocationAddress = address.get(0).getAddressLine(0).toString();

					// ������ �ּ� ������ (����/�浵 ���� ����)
					bf.append(currentLocationAddress);// .append(" ");
					// bf.append(lat).append(",");
					// bf.append(lng);
				}
			}

		} catch (IOException e) {
			Toast.makeText(mContext, "�ּ���� ����", Toast.LENGTH_LONG).show();

			e.printStackTrace();
		}
		return bf.toString();
	}

}
