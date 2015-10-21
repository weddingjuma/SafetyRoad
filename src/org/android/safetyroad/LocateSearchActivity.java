package org.android.safetyroad;

import org.android.safetyroad.R;

import com.skp.Tmap.TMapView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.RelativeLayout;

public class LocateSearchActivity extends Activity {

	public static final String APP_KEY = "62305c74-edf5-3198-bdce-ab26eced4be6";
	private RelativeLayout locateSearchMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_locatesearch);

		// Tmap 생성 및 설정
		TMapView tmap = new TMapView(this);
		tmap.setLanguage(TMapView.LANGUAGE_KOREAN);
		tmap.setIconVisibility(true);
		tmap.setZoomLevel(10);
		tmap.setMapType(TMapView.MAPTYPE_STANDARD);
		// 목적지를 보여주는 레이아웃에 tmap을 달아준다
		locateSearchMap = (RelativeLayout) findViewById(R.id.locateSearchMap);
		locateSearchMap.addView(tmap);
		// tmap 개인키 설정
		tmap.setSKPMapApiKey(APP_KEY);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
