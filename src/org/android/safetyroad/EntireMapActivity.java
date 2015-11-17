package org.android.safetyroad;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.TMapPathType;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

public class EntireMapActivity extends Activity {

	public static final int REQUEST_CODE_SETTING = 1003;
	public static final int REQUEST_CODE_ROUTE = 1004;
	public static final String APP_KEY = "62305c74-edf5-3198-bdce-ab26eced4be6";

	private TMapView tmap;
	private TMapPoint startPoint;
	private TMapPoint endPoint;
	private ImageButton settingBtn;
	private ImageButton backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_entiremap);
		
		tmap = (TMapView) findViewById(R.id.entireMapLayout);
		settingBtn = (ImageButton) findViewById(R.id.settingBtn);
		backBtn = (ImageButton) findViewById(R.id.backBtn);
		
		settingBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SETTING);
			}
		});
		
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		new MapRegisterTask().execute("");		
		
		//mainActivity!!! transfer data!!!
		startPoint = new TMapPoint(37.56760133023583, 126.98204040527344);
		endPoint = new TMapPoint(37.570101477782934, 126.9920825958251);
		
		new RouteSearchTask().execute(startPoint,endPoint);
				
		Button routeSearchBtn = (Button) findViewById(R.id.routeSearchBtn);		

		routeSearchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), RouteSearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_ROUTE);
			}
		});		
		
	}
		
	class RouteSearchTask extends AsyncTask<TMapPoint, Integer, ArrayList<TMapPolyLine>> {
		@Override
		protected ArrayList<TMapPolyLine> doInBackground(TMapPoint... params) {
			TMapPoint start = params[0];
			TMapPoint end = params[1];
			TMapData data = new TMapData();
			try {				
				ArrayList<TMapPolyLine> path = new ArrayList<TMapPolyLine>();
				
				TMapPoint middlePoint = new TMapPoint(37.566637, 126.978407);
				
				path.add( data.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, start, middlePoint) );
				path.add( data.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, middlePoint, end) );		
			
				return path;
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<TMapPolyLine> result) {
			
			if (result != null) {
				
				for(TMapPolyLine path: result){
					path.setLineColor(Color.RED);
					path.setLineWidth(15);
					
					tmap.addTMapPath(path);					
				}

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
			tmap.setIconVisibility(true);
			tmap.setZoomLevel(14);
			tmap.setMapType(TMapView.MAPTYPE_STANDARD);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
