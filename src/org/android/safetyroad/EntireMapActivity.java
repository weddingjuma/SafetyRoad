package org.android.safetyroad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

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

		// Tmap 생성 및 설정
		TMapView tmap = new TMapView(this);
		tmap.setLanguage(TMapView.LANGUAGE_KOREAN);
		tmap.setIconVisibility(true);
		tmap.setZoomLevel(10);
		tmap.setMapType(TMapView.MAPTYPE_STANDARD);
		// 전체 경로를 보여주는 레이아웃에 tmap을 달아준다
		entireMapLayout = (RelativeLayout) findViewById(R.id.entireMapLayout);
		entireMapLayout.addView(tmap);
		// tmap 개인키 설정
		tmap.setSKPMapApiKey(APP_KEY);

		Button routeSearchBtn = (Button) findViewById(R.id.routeSearchBtn);

		// 경로찾기 버튼 리스너
		routeSearchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Intent intent = new
				// Intent(getApplicationContext(),tempActivity.class);

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
