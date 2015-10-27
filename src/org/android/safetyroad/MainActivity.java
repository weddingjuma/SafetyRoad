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
import android.widget.ImageView;

public class MainActivity extends Activity {

	public static final int REQUEST_CODE_LOCATE = 1001;
	public static final int REQUEST_CODE_MAP = 1002;
	public static final int REQUEST_CODE_SETTING = 1003;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		startActivity(new Intent(this, SplashActivity.class));
		
		Button searchBtn = (Button) findViewById(R.id.searchBtn);
		ImageView settingBtn = (ImageView) findViewById(R.id.settingBtn);
		
		EditText departureEntry = (EditText) findViewById(R.id.departureEntry);
		EditText arriveEntry = (EditText) findViewById(R.id.arriveEntry);
		
		searchBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
								
				Intent intent = new Intent(getApplicationContext(),EntireMapActivity.class);
				startActivityForResult(intent, REQUEST_CODE_MAP);
			}			
		});
		
		settingBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
								
				Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SETTING);
			}			
		});
		
		departureEntry.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(getApplicationContext(),LocateSearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_LOCATE);
			}			
		});
		
		arriveEntry.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(getApplicationContext(),LocateSearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_LOCATE);
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
