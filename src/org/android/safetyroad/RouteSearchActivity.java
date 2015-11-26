package org.android.safetyroad;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.Toast;

import com.skp.Tmap.TMapView;

public class RouteSearchActivity extends Activity {

	public static final String APP_KEY = "edcbe7e3-2ade-3654-93a9-90c940f92470";
	public static final String urgentNumber = "01071881348";
	
	private TMapView tmap;
	private ImageButton entirePath;
	private ImageButton normalMsg;
	private ImageButton urgentMsg;
	private int Minute;
	
	private boolean flagEntirePath, flagNorMsg, flagUrgentMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routesearch);

		tmap = (TMapView) findViewById(R.id.TMap);
		
		new MapRegisterTask().execute("");	
		
		Intent intent = getIntent();
		
		SharedPreferences pref = getSharedPreferences("sharedData",MODE_PRIVATE);
		Minute = pref.getInt("SET_TIME", 10);
		Log.d("testing","" + Minute);
		
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
					

					String smsNum = urgentNumber;
				    String smsText = "이미 너의 마음속 이야 ";
				   sendSMS(smsNum,smsText);
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
	

	//문자 전송
		
			public void sendSMS(String smsNumber, String smsText){
				
			    PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
			    PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);
			 
			    registerReceiver(new BroadcastReceiver() {
			        @Override
			        public void onReceive(Context mContext, Intent intent) {
			            switch(getResultCode()){
			                case Activity.RESULT_OK:
			                    // 전송 성공
			                    Toast.makeText(mContext, "전송 완료", Toast.LENGTH_SHORT).show();
			                    break;
			                    default :
			                    // 전송 실패
			                    Toast.makeText(mContext, "전송 실패", Toast.LENGTH_SHORT).show();
			            }
			         }
			    }, new IntentFilter("SMS_SENT_ACTION"));
			 
			    registerReceiver(new BroadcastReceiver() {
			        @Override
			        public void onReceive(Context mContext, Intent intent) {
			            switch (getResultCode()){
			                case Activity.RESULT_OK:
			                    // 도착 완료
			                    Toast.makeText(mContext, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
			                    break;
			                case Activity.RESULT_CANCELED:
			                    // 도착 안됨
			                    Toast.makeText(mContext, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
			                    break;
			            }
			        }
			    }, new IntentFilter("SMS_DELIVERED_ACTION"));
			 
			    SmsManager mSmsManager = SmsManager.getDefault();
			    mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
			}



}
