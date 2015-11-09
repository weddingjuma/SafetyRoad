package org.android.safetyroad;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LocateSearchActivity extends Activity {

	public static final String APP_KEY = "62305c74-edf5-3198-bdce-ab26eced4be6";
	private RelativeLayout locateSearchMap;
	private EditText inputLocation;
	// private Spinner inputLocationSpinner;
	private ListView searchListView;
	private String[] recentList;
	private ArrayAdapter<String> searchListAdapter;

	// 찾을 주소
	private String address;
	// 검색버튼
	private Button searchBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_locatesearch);

		Intent intent = getIntent();
		String DepOrArr = intent.getStringExtra("where");

		inputLocation = (EditText) findViewById(R.id.inputLocation);
		searchBtn = (Button) findViewById(R.id.searchButton);
		// inputLocationSpinner = (Spinner)
		// findViewById(R.id.inputLocationSpinner);

		// main액티비티에서 출발지를 선택했는지, 도착지를 선택했는지 구분해서 listview를 띄운다.
		if (DepOrArr.equals("departure"))
			recentList = getResources().getStringArray(R.array.recentDepartureArray);
		else
			recentList = getResources().getStringArray(R.array.recentArriveArray);
		searchListView = (ListView) findViewById(R.id.searchList);
		searchListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recentList);
		searchListView.setAdapter(searchListAdapter);

		// Tmap 설정
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

		searchBtn.setOnClickListener(new OnClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 설정한 주소 검색
				address = inputLocation.getText().toString();
				if (!address.equals("")) {
					TMapData tmapdata = new TMapData();
					ArrayList<TMapPOIItem> POIItem = null;
					try {
						POIItem = tmapdata.findAddressPOI(address);
						Toast.makeText(getApplicationContext(), POIItem.get(0).address, Toast.LENGTH_SHORT);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FactoryConfigurationError e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
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