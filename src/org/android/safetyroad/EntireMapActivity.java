package org.android.safetyroad;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.TMapPathType;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

public class EntireMapActivity extends Activity {

	public static final int REQUEST_CODE_ROUTE = 1004;
	public static final String APP_KEY = "62305c74-edf5-3198-bdce-ab26eced4be6";

	//private RelativeLayout entireMapLayout;
	
	TMapView tmap;
	TMapPoint startPoint = null;
	TMapPoint endPoint = null;
	Location cacheLocation = null;
	boolean isInitialized = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_entiremap);
		
		tmap = (TMapView) findViewById(R.id.entireMapLayout);
		new MapRegisterTask().execute("");
		
//		final TMapView tmap = new TMapView(this);
//		tmap.setLanguage(TMapView.LANGUAGE_KOREAN);
//		tmap.setIconVisibility(true);
//		tmap.setZoomLevel(16);
//		tmap.setMapType(TMapView.MAPTYPE_STANDARD);
		
		//entireMapLayout = (RelativeLayout) findViewById(R.id.entireMapLayout);
		//entireMapLayout.addView(tmap);
				
		//tmap.setSKPMapApiKey(APP_KEY);
		
		Button routeSearchBtn = (Button) findViewById(R.id.routeSearchBtn);
		//startPoint.setLatitude(37.5642336);
		//startPoint.setLongitude(126.973736);
		
		//endPoint.setLatitude(37.5744297);
		//endPoint.setLongitude(126.9927906);
		
//		TMapData tmapdata = new TMapData();
//		
//		TMapPoint startpoint = new TMapPoint(37.5248, 126.93);
//		TMapPoint endpoint = new TMapPoint(37.4601, 128.0428);
		
//		tmapdata.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint,
//				new FindPathDataListenerCallback() {
//				@Override
//				public void onFindPathData(TMapPolyLine polyLine) {
//					polyLine.setLineColor(Color.RED);
//					polyLine.setLineWidth(10);
//					tmap.addTMapPath(polyLine);
//				}
//		});	
		

//		routeSearchBtn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(getApplicationContext(), RouteSearchActivity.class);
//				startActivityForResult(intent, REQUEST_CODE_ROUTE);
//			}
//		});
		
		routeSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (startPoint != null && endPoint != null) {
					new RouteSearchTask().execute(startPoint,endPoint);
					startPoint = endPoint = null;
				}
			}
		});
	}
	
	class RouteSearchTask extends AsyncTask<TMapPoint, Integer, TMapPolyLine> {
		@Override
		protected TMapPolyLine doInBackground(TMapPoint... params) {
			TMapPoint start = params[0];
			TMapPoint end = params[1];
			TMapData data = new TMapData();
			try {
				TMapPolyLine path = data.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, start, end);
				return path;
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(TMapPolyLine result) {
			if (result != null) {
				tmap.addTMapPath(result);
				Bitmap bm = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
				tmap.setTMapPathIcon(bm, bm);
			}
		}
	}
	
	class MapRegisterTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			tmap.setSKPMapApiKey(APP_KEY);
			tmap.setLanguage(tmap.LANGUAGE_KOREAN);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			setUpMap();
		}
	}
	
	private void setUpMap() {
		isInitialized = true;
		if (cacheLocation != null) {
			//moveMap(cacheLocation);
			//moveMyLocation(cacheLocation);
			cacheLocation = null;
		}
		tmap.setTrafficInfo(false);
		tmap.setIconVisibility(true);
		tmap.setZoomLevel(14);
		tmap.setMapType(TMapView.MAPTYPE_STANDARD);

		tmap.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {

			@Override
			public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markers,
					ArrayList<TMapPOIItem> arg1, TMapPoint arg2, PointF arg3) {

				return false;
			}

			@Override
			public boolean onPressEvent(ArrayList<TMapMarkerItem> markers,
					ArrayList<TMapPOIItem> poiitems, TMapPoint point,
					PointF arg3) {
				Toast.makeText(
						EntireMapActivity.this,
						"lat : " + point.getLatitude() + ",lng : "
								+ point.getLongitude(), Toast.LENGTH_SHORT)
						.show();
				for (TMapMarkerItem item : markers) {
					Toast.makeText(EntireMapActivity.this,
							"click marker : " + item.getID(),
							Toast.LENGTH_SHORT).show();
				}

				for (TMapPOIItem item : poiitems) {
					Toast.makeText(EntireMapActivity.this,
							"poi : " + item.getPOIName(), Toast.LENGTH_SHORT)
							.show();
				}
				//currentPoint = point;
//				positionView.setText("lat : " + currentPoint.getLatitude()
//						+ ",lng : " + currentPoint.getLongitude());
				return false;
			}
		});

		tmap.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {

			@Override
			public void onCalloutRightButton(TMapMarkerItem item) {
				Toast.makeText(EntireMapActivity.this, "item id : " + item.getID(),
						Toast.LENGTH_SHORT).show();
			}
		});
		// mMap.setSightVisible(true);
		// mMap.setCompassMode(true);
		// mMap.setTrackingMode(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
