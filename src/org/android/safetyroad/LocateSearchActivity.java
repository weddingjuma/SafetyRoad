package org.android.safetyroad;

import com.skp.Tmap.TMapView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class LocateSearchActivity extends Activity {

	public static final String APP_KEY = "62305c74-edf5-3198-bdce-ab26eced4be6";
	private RelativeLayout locateSearchMap;
	private EditText inputLocation;
	// private Spinner inputLocationSpinner;
	private ListView searchListView;
	private String[] recentList;
	ArrayAdapter<String> searchListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_locatesearch);

		Intent intent = getIntent();
		String depOrarr = intent.getStringExtra("where");

		inputLocation = (EditText) findViewById(R.id.inputLocation);
		// inputLocationSpinner = (Spinner)
		// findViewById(R.id.inputLocationSpinner);

		if (depOrarr.equals("departure"))
			recentList = getResources().getStringArray(R.array.recentDepartureArray);
		else
			recentList = getResources().getStringArray(R.array.recentArriveArray);
		searchListView = (ListView) findViewById(R.id.searchList);

		searchListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recentList);
		searchListView.setAdapter(searchListAdapter);

		// Tmap
		TMapView tmap = new TMapView(this);
		tmap.setLanguage(TMapView.LANGUAGE_KOREAN);
		tmap.setIconVisibility(true);
		tmap.setZoomLevel(10);
		tmap.setMapType(TMapView.MAPTYPE_STANDARD);
		// ? ?? tmap ? ?
		locateSearchMap = (RelativeLayout) findViewById(R.id.locateSearchMap);
		locateSearchMap.addView(tmap);
		// tmap ?
		tmap.setSKPMapApiKey(APP_KEY);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}