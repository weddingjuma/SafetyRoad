package org.android.safetyroad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.FindPathDataListenerCallback;
import com.skp.Tmap.TMapData.TMapPathType;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

public class EntireMapActivity extends Activity {

	public static final int REQUEST_CODE_ROUTE = 1004;
	public static final String APP_KEY = "62305c74-edf5-3198-bdce-ab26eced4be6";

	private RelativeLayout entireMapLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_entiremap);
		
		final TMapView tmap = new TMapView(this);
		tmap.setLanguage(TMapView.LANGUAGE_KOREAN);
		tmap.setIconVisibility(true);
		tmap.setZoomLevel(16);
		tmap.setMapType(TMapView.MAPTYPE_STANDARD);
		
		entireMapLayout = (RelativeLayout) findViewById(R.id.entireMapLayout);
		entireMapLayout.addView(tmap);
		
		tmap.setSKPMapApiKey(APP_KEY);
		
		Button routeSearchBtn = (Button) findViewById(R.id.routeSearchBtn);
		
		TMapData tmapdata = new TMapData();
		
		TMapPoint startpoint = new TMapPoint(37.5248, 126.93);
		TMapPoint endpoint = new TMapPoint(37.4601, 128.0428);
		
		tmapdata.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint,
				new FindPathDataListenerCallback() {
				@Override
				public void onFindPathData(TMapPolyLine polyLine) {
					polyLine.setLineColor(Color.RED);
					polyLine.setLineWidth(10);
					tmap.addTMapPath(polyLine);
				}
		});
		
		

		routeSearchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), RouteSearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_ROUTE);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
