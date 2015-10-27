package org.android.safetyroad;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class LocateSearchActivity extends Activity {

	public static final String APP_KEY = "62305c74-edf5-3198-bdce-ab26eced4be6";
	private RelativeLayout locateSearchMap;
        private EditText inputLocation;
        private Spinner inputLocationSpinner;
        private ListView searchListView;
        static ArrayList<String> searchList = new ArrayList<String>();

        static {
          searchList.add("현재 위치");
          searchList.add("검색 결과 1");
          searchList.add("검색 결과 2");
          searchList.add("검색 결과 3");
          searchList.add("검색 결과 4");
          searchList.add("검색 결과 5");
        }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_locatesearch);

                inputLocation = (EditText) findViewById(R.id.inputLocation);
                inputLocationSpinner = (Spinner) findViewById(R.id.inputLocationSpinner);

                searchListView = (ListView) findViewById(R.id.searchList);

                ArrayAdapter<String>
                searchListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchList);
                searchListView.setAdapter(searchListAdapter);

          // Tmap             
		TMapView tmap = new TMapView(this);
		tmap.setLanguage(TMapView.LANGUAGE_KOREAN);
		tmap.setIconVisibility(true);
		tmap.setZoomLevel(10);
		tmap.setMapType(TMapView.MAPTYPE_STANDARD);
		//               ?     ??    tmap    ?  ? 
		locateSearchMap = (RelativeLayout) findViewById(R.id.locateSearchMap);
		locateSearchMap.addView(tmap);
		// tmap     ?     
		tmap.setSKPMapApiKey(APP_KEY);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}