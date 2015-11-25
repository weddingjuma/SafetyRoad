package org.android.safetyroad;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.skp.Tmap.TMapView;

public class RouteSearchActivity extends Activity {

	public static final String APP_KEY = "edcbe7e3-2ade-3654-93a9-90c940f92470";
	private TMapView tmap;
	private ImageButton entirePath;
	private ImageButton normalMsg;
	private ImageButton urgentMsg;
	
	private boolean flagEntirePath, flagNorMsg, flagUrgentMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routesearch);

		tmap = (TMapView) findViewById(R.id.TMap);
		
		new MapRegisterTask().execute("");	
		
		Intent intent = getIntent();
		
		
		
		flagEntirePath=flagNorMsg=flagUrgentMsg=false;
		
		entirePath = (ImageButton) findViewById(R.id.entirePath);
		entirePath.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!flagEntirePath){
					flagEntirePath=true;
					entirePath.setSelected(true);
				}
				else{
					flagEntirePath=false;
					entirePath.setSelected(false);
				}
			}
		});
		normalMsg = (ImageButton) findViewById(R.id.normalMessage);
		normalMsg.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!flagNorMsg){
					flagNorMsg=true;
					normalMsg.setSelected(true);
				}
				else{
					flagNorMsg=false;
					normalMsg.setSelected(false);
				}
			}
		});
		
		urgentMsg = (ImageButton) findViewById(R.id.urgentMessage);
		urgentMsg.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!flagUrgentMsg){
					flagUrgentMsg=true;
					urgentMsg.setSelected(true);
				}
				else{
					flagUrgentMsg=false;
					urgentMsg.setSelected(false);
				}
			}
		});
		
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
			//tmap.setZoomLevel(16);
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
