package org.android.safetyroad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	public static final int REQUEST_CODE_LOCATE = 1001;
	public static final int REQUEST_CODE_MAP = 1002;
	public static final int REQUEST_CODE_SETTING = 1003;

	private boolean isFirst = true;

	private Button searchBtn;
	private ImageButton settingBtn;
	private EditText departureEntry;
	private EditText arriveEntry;

	private double depLon, depLat, arrLon, arrLat;
	private String depAddress, arrAddress;
	private static PointManager depPoint = new PointManager();
	private static PointManager arrPoint = new PointManager();
	private boolean isDepOrArr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		if (isFirst) {
			startActivity(new Intent(this, SplashActivity.class));
			isFirst = false;
		}

		searchBtn = (Button) findViewById(R.id.searchBtn);
		settingBtn = (ImageButton) findViewById(R.id.settingBtn);

		departureEntry = (EditText) findViewById(R.id.departureEntry);
		departureEntry.setInputType(0);
		arriveEntry = (EditText) findViewById(R.id.arriveEntry);
		arriveEntry.setInputType(0);

		searchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), EntireMapActivity.class);
				intent.putExtra("depLon", depPoint.getLon());
				intent.putExtra("depLat", depPoint.getLat());
				intent.putExtra("arrLon", arrPoint.getLon());
				intent.putExtra("arrLat", arrPoint.getLat());

				startActivityForResult(intent, REQUEST_CODE_MAP);
			}
		});

		settingBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SETTING);
			}
		});

		departureEntry.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), LocateSearchActivity.class);
				intent.putExtra("where", "departure");
				startActivityForResult(intent, REQUEST_CODE_LOCATE);
				finish();
			}
		});

		arriveEntry.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), LocateSearchActivity.class);
				intent.putExtra("where", "arriveEntry");
				startActivityForResult(intent, REQUEST_CODE_LOCATE);
				finish();
			}
		});

		Intent intent = getIntent();
		isDepOrArr = intent.getBooleanExtra("isDepOrArr", false);

		if (depPoint.getAddress().equals("")) {
			Log.d("1", "dddd");
			if (isDepOrArr) {
				Log.d("2", "dddd");
				depLon = intent.getDoubleExtra("Lon", 0);
				depLat = intent.getDoubleExtra("Lat", 0);
				depAddress = intent.getStringExtra("address");
				// departureEntry.setText(depAddress);
				Log.d("3?", "dddd");
				depPoint.setLat(depLat);
				depPoint.setLon(depLon);
				depPoint.setAddress(depAddress);
				Log.d("4?", "dddd");
			}
		}
		if (arrPoint.getAddress().equals("")) {
			Log.d("5", "dddd");
			if (!isDepOrArr) {
				Log.d("6", "dddd");
				arrLon = intent.getDoubleExtra("Lon", 0);
				arrLat = intent.getDoubleExtra("Lat", 0);
				arrAddress = intent.getStringExtra("address");
				Log.d("7", "dddd");
				// arriveEntry.setText(arrAddress);
				arrPoint.setLat(arrLat);
				arrPoint.setLon(arrLon);
				arrPoint.setAddress(arrAddress);
				Log.d("8?", "dddd");
			}
		}
		
		departureEntry.setText(depPoint.getAddress());
		arriveEntry.setText(arrPoint.getAddress());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
