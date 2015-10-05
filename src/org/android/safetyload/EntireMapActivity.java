package org.android.safetyload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class EntireMapActivity extends Activity {
	
	public static final int REQUEST_CODE_ROUTE = 1004;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_entiremap);
		
		Button routeSearchBtn = (Button) findViewById(R.id.routeSearchBtn);
		
		routeSearchBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Intent intent = new Intent(getApplicationContext(),tempActivity.class);
				
				Intent intent = new Intent(getApplicationContext(),RouteSearchActivity.class);
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
