package org.android.safetyroad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class LocateSearchActivity extends Activity {

	public static final String APP_KEY = "62305c74-edf5-3198-bdce-ab26eced4be6";

	private EditText inputLocation;
	private ListView searchListView;
	private String[] recentList;
	private ArrayAdapter<String> searchListAdapter;

	// 찾을 주소
	private String address;
	// 검색버튼
	private Button searchBtn;
	// Tmap
	TMapView tmap;
	boolean isInitialized = false;
	Location cacheLocation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_locatesearch);

		Intent intent = getIntent();
		String DepOrArr = intent.getStringExtra("where");

		inputLocation = (EditText) findViewById(R.id.inputLocation);
		searchBtn = (Button) findViewById(R.id.searchButton);
		tmap = (TMapView) findViewById(R.id.locateSearchMap);
		new MapRegisterTask().execute("");

		// main액티비티에서 출발지를 선택했는지, 도착지를 선택했는지 구분해서 listview를 띄운다.
		if (DepOrArr.equals("departure"))
			recentList = getResources().getStringArray(R.array.recentDepartureArray);
		else
			recentList = getResources().getStringArray(R.array.recentArriveArray);
		searchListView = (ListView) findViewById(R.id.searchList);
		searchListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recentList);
		searchListView.setAdapter(searchListAdapter);

		// Locate search
		searchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Find lng & lat using address
				address = inputLocation.getText().toString();
				// address = "도봉역";

				// Is address valid?

				GeoPoint addressPoint = findGeoPoint(address);
				double lng = addressPoint.getLongitudeE6() / 1E6;
				double lat = addressPoint.getLatitudeE6() / 1E6;
				tmap.setCenterPoint(lng, lat);
			}

		});
		
		tmap.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {

			@Override
			public void onLongPressEvent(ArrayList<TMapMarkerItem> markers, ArrayList<TMapPOIItem> poiitems,
					TMapPoint point) {
				new ProcessFindAddress().execute(point.getLatitude(), point.getLongitude());
			}
		});

	}

	class MapRegisterTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			tmap.setSKPMapApiKey(APP_KEY);
			tmap.setLanguage(tmap.LANGUAGE_KOREAN);
			Log.d("doinbackground", "intrance");
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			setUpMap();
			Log.d("onpost", "intrance");
		}

		private void setUpMap() {
			Log.d("setupmap", "intrance");
			isInitialized = true;
			if (cacheLocation != null) {
				// moveMap(cacheLocation);
				// moveMyLoation(cacheLocation);  
				cacheLocation = null;
			}
			// tmap.setTrafficInfo(true);
			tmap.setIconVisibility(true);
			tmap.setZoomLevel(14);
			tmap.setMapType(TMapView.MAPTYPE_STANDARD);

			tmap.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {

				@Override
				public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arg0, ArrayList<TMapPOIItem> arg1,
						TMapPoint arg2, PointF arg3) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean onPressEvent(ArrayList<TMapMarkerItem> markers, ArrayList<TMapPOIItem> poiitems,
						TMapPoint point, PointF arg3) {
					/*
					 * Toast.makeText(LocateSearchActivity.this, "lat : " +
					 * point.getLatitude() + ",lng : " + point.getLongitude(),
					 * Toast.LENGTH_SHORT) .show(); for (TMapMarkerItem item :
					 * markers) Toast.makeText(LocateSearchActivity.this,
					 * "click marker : " + item.getID(), Toast.LENGTH_SHORT)
					 * .show();
					 * 
					 * for (TMapPOIItem item : poiitems)
					 * Toast.makeText(LocateSearchActivity.this, "poi : " +
					 * item.getPOIName(), Toast.LENGTH_SHORT) .show();
					 */

					return false;
				}
			});

			tmap.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {

				@Override
				public void onCalloutRightButton(TMapMarkerItem item) {
					Toast.makeText(LocateSearchActivity.this, "item id : " + item.getID(), Toast.LENGTH_SHORT).show();
				}
			});
			// mMap.setSightVisible(true);
			// mMap.setCompassMode(true);
			// mMap.setTrackingMode(true);

			// When user long touch the map, find the address by lng&lat and set
			// the input EditText
		}
	}

	private class ProcessFindAddress extends AsyncTask<Double, Void, String> {

		@Override
		protected String doInBackground(Double... params) {
			try {
				TMapData tmapdata = new TMapData();
				String changeAddress = "Not Changed";
				changeAddress = tmapdata.convertGpsToAddress(params[0], params[1]);

				if (changeAddress.equals("Not Changed"))
					Log.d("LongTouchFindAddress", "Fail");
				else
					Log.d("LongTouchFindAddress", "Success");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return address;
		}
		
		protected void onPostExecute(String address){
			Log.d("????", address);
			inputLocation.setText(address);
		}

	}

	private GeoPoint findGeoPoint(String address) {
		Geocoder geocoder = new Geocoder(this);
		Address addr;
		GeoPoint location = null;
		try {
			List<Address> listAddress = geocoder.getFromLocationName(address, 1);
			if (listAddress.size() > 0) { // if address found
				addr = listAddress.get(0); // in Address format
				int lat = (int) (addr.getLatitude() * 1E6);
				int lng = (int) (addr.getLongitude() * 1E6);
				location = new GeoPoint(lat, lng);

				Toast.makeText(LocateSearchActivity.this, "주소로부터 취득한 위도 : " + lat / 1E6 + ", 경도 : " + lng / 1E6,
						Toast.LENGTH_SHORT).show();
				// Log.d(TAG, "주소로부터 취득한 위도 : " + lat + ", 경도 : " + lng);
			} else
				Toast.makeText(LocateSearchActivity.this, "Address Converting Fail", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return location;
	}

}