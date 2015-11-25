package org.android.safetyroad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
				intent.putExtra("depLon", depLon);
				intent.putExtra("depLat", depLat);
				intent.putExtra("arrLon", arrLon);
				intent.putExtra("arrLat", arrLat);
				
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
			}
		});

		arriveEntry.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), LocateSearchActivity.class);
				intent.putExtra("where", "arriveEntry");
				startActivityForResult(intent, REQUEST_CODE_LOCATE);
			}
		});

		Intent intent = getIntent();
		isDepOrArr = intent.getBooleanExtra("isDepOrArr", false);
		
		if (isDepOrArr) {
			depLon = intent.getDoubleExtra("Lon", 0);
			depLat = intent.getDoubleExtra("Lat", 0);
			depAddress = intent.getStringExtra("address");
			departureEntry.setText(depAddress);
		} else {
			arrLon = intent.getDoubleExtra("Lon", 0);
			arrLat = intent.getDoubleExtra("Lat", 0);
			arrAddress = intent.getStringExtra("address");
			arriveEntry.setText(arrAddress);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
