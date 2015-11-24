package org.android.safetyroad;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	private boolean isDepOrArr;

	// 찾을 주소
	private String address;
	// 검색버튼
	private Button searchBtn;
	// Tmap
	TMapView tmap;
	boolean isInitialized = false;
	Location cacheLocation = null;
	// GPS
	private GpsInfo gps;
	private static double tmpLat;
	private static double tmpLon;

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
		if (DepOrArr.equals("departure")) {
			recentList = getResources().getStringArray(R.array.recentDepartureArray);
			isDepOrArr = true;
		} else {
			recentList = getResources().getStringArray(R.array.recentArriveArray);
			isDepOrArr = false;
		}
		searchListView = (ListView) findViewById(R.id.searchList);
		searchListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recentList);
		searchListView.setAdapter(searchListAdapter);
		searchListView.setOnItemClickListener(recentListClickListener);

		// Locate search
		searchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Find lng & lat using address
				address = inputLocation.getText().toString();

				// Is address valid?
				FindGeo fg = new FindGeo(LocateSearchActivity.this);
				GeoPoint addressPoint = fg.findGeoPoint(address);
				double lng = addressPoint.getLongitudeE6() / 1E6;
				double lat = addressPoint.getLatitudeE6() / 1E6;
				tmap.setCenterPoint(lng, lat);

				// keyboard hiding
				InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMgr.hideSoftInputFromWindow(searchBtn.getWindowToken(), 0);
			}

		});
		// TMAP Long Touch Event
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
			tmap.setZoomLevel(16);
			tmap.setMapType(TMapView.MAPTYPE_STANDARD);
			gps = new GpsInfo(LocateSearchActivity.this);

			// GPS 사용유무 가져오기 + 현재위치로 setup
			if (gps.isGetLocation()) {
				double currentLat = gps.getLatitude();
				double currentLon = gps.getLongitude();
				tmap.setCenterPoint(currentLon, currentLat);
				Toast.makeText(LocateSearchActivity.this, "Find current point", Toast.LENGTH_LONG).show();
			} else
				Toast.makeText(LocateSearchActivity.this, "Check the GPS status", Toast.LENGTH_LONG).show();

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
					Log.d("LongTouchFindAddress", "Fail" + changeAddress);
				else
					Log.d("LongTouchFindAddress", "Success" + changeAddress);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return address;
		}

		protected void onPostExecute(String address) {
			Log.d("????", address);
			inputLocation.setText(address);
		}

	}

	/*
	 * private GeoPoint findGeoPoint(String address) { Geocoder geocoder = new
	 * Geocoder(LocateSearchActivity.this); Address addr; GeoPoint point = null;
	 * 
	 * try { List<Address> listAddress = geocoder.getFromLocationName(address,
	 * 1); if (listAddress.size() > 0) { // if address found addr =
	 * listAddress.get(0); // in Address format int lat = (int)
	 * (addr.getLatitude() * 1E6); int lng = (int) (addr.getLongitude() * 1E6);
	 * 
	 * point = new GeoPoint(lat, lng);
	 * 
	 * Toast.makeText(LocateSearchActivity.this, "주소로부터 취득한 위도 : " + lat / 1E6 +
	 * ", 경도 : " + lng / 1E6, Toast.LENGTH_SHORT).show(); // Log.d(TAG,
	 * "주소로부터 취득한 위도 : " + lat + ", 경도 : " + lng); } else
	 * Toast.makeText(LocateSearchActivity.this, "Address Converting Fail",
	 * Toast.LENGTH_SHORT).show(); } catch (IOException e) {
	 * e.printStackTrace(); } return point; }
	 * 
	 *//**
		 * 위도,경도로 주소취득
		 * 
		 * @param lat
		 * @param lng
		 * @return 주소
		 *//*
		 * private String findAddress(double lat, double lng) { StringBuffer bf
		 * = new StringBuffer(); Geocoder geocoder = new Geocoder(this,
		 * Locale.KOREA); String currentLocationAddress; List<Address> address;
		 * try { if (geocoder != null) { // 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
		 * address = geocoder.getFromLocation(lat, lng, 1); // 설정한 데이터로 주소가 리턴된
		 * 데이터가 있으면 if (address != null && address.size() > 0) { // 주소
		 * currentLocationAddress = address.get(0).getAddressLine(0).toString();
		 * 
		 * // 전송할 주소 데이터 (위도/경도 포함 편집)
		 * bf.append(currentLocationAddress);//.append(" ");
		 * //bf.append(lat).append(","); //bf.append(lng); } }
		 * 
		 * } catch (IOException e) { Toast.makeText(LocateSearchActivity.this,
		 * "주소취득 실패", Toast.LENGTH_LONG).show();
		 * 
		 * e.printStackTrace(); } return bf.toString(); }
		 */

	private OnItemClickListener recentListClickListener = new OnItemClickListener() {
		private String address;

		@Override
		public void onItemClick(AdapterView<?> listview, View item, int position, long id) {
			// select current point
			if (position == 0) {
				gps = new GpsInfo(LocateSearchActivity.this);
				FindGeo fg = new FindGeo(LocateSearchActivity.this);
				// GPS 사용유무 가져오기
				if (gps.isGetLocation()) {
					double currentLat = gps.getLatitude();
					double currentLon = gps.getLongitude();
					tmap.setCenterPoint(currentLon, currentLat);
					tmap.setZoomLevel(16);
					Toast.makeText(LocateSearchActivity.this,
							"Find current point : " + fg.findAddress(currentLat, currentLon), Toast.LENGTH_LONG).show();
					inputLocation.setText(fg.findAddress(currentLat, currentLon));
				} else
					Toast.makeText(LocateSearchActivity.this, "Check the GPS status", Toast.LENGTH_LONG).show();
			}

			// select recent point
			else if (position >= 1 && position <= 3) {
				Toast.makeText(LocateSearchActivity.this, "Convert" + position, Toast.LENGTH_LONG).show();
				Log.d("인간적", "여길");
				//FindGeo fg = new FindGeo(LocateSearchActivity.this);

				if (isDepOrArr) {
					address = getResources().getStringArray(R.array.recentDepartureArray)[position];
					//GeoPoint point = fg.findGeoPoint(address);
					//double lng = point.getLongitudeE6() / 1E6;
					//double lat = point.getLatitudeE6() / 1E6;
					new geoPointTask().execute(address);
					//tmap.setCenterPoint(lng, lat);
					tmap.setCenterPoint(tmpLon, tmpLat);
				} else {
					address = getResources().getStringArray(R.array.recentArriveArray)[position];
					//GeoPoint point = fg.findGeoPoint(address);
					new geoPointTask().execute(address);
					//double lng = point.getLongitudeE6() / 1E6;
					//double lat = point.getLatitudeE6() / 1E6;
					//tmap.setCenterPoint(lng, lat);
					tmap.setCenterPoint(tmpLon, tmpLat);
				}

			} else {
				Toast.makeText(LocateSearchActivity.this, "Empty Item" + position, Toast.LENGTH_LONG).show();
			}
		}

	};

	private class geoPointTask extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			getGeoPoint(getLocationInfo(params[0].replace("\n", " ").replace(" ", "%20"))); // 주소를
																							// 넘겨준다(공백이나
																							// 엔터는
																							// 제거합니다)

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}
	}

	public static JSONObject getLocationInfo(String address) {

		HttpGet httpGet = new HttpGet(
				"http://maps.google.com/maps/api/geocode/json?address=" + address + "&ka&sensor=false");
		// 해당 url을 인터넷창에 쳐보면 다양한 위도 경도 정보를 얻을수있다(크롬 으로실행하세요)
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static void getGeoPoint(JSONObject jsonObject) {

		Double lon = new Double(0);
		Double lat = new Double(0);

		try {
			lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lat");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tmpLat = lat.doubleValue();
		tmpLon = lon.doubleValue();

		Log.d("myLog", "경도:" + lon); // 위도/경도 결과 출력
		Log.d("myLog", "위도:" + lat);

	}

}
