package org.android.safetyroad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.TMapPathType;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;



public class RouteSearchActivity extends Activity {

	public static String url = "http://openapi.seoul.go.kr:8088/516943625268733134394857784576/json/TB_GC_VVTV_INFO_ID01/1/400/";
	public static final String APP_KEY = "edcbe7e3-2ade-3654-93a9-90c940f92470";
	public String urgentNumber;

	private TMapView tmap;
	private ImageButton entirePath;
	private ImageButton normalMsg;
	private ImageButton urgentMsg;
	private ImageButton backBtn;
	private ImageButton settingBtn;
	private TextView nameunTime;
	private TextView textTime;

	private double nameunDistance;

	private TMapPoint startPoint;
	private TMapPoint endPoint;
	private TMapPoint currPoint;
	
	private ArrayList<TMapPoint> posOfCCTV;
	private ArrayList<TMapPoint> cerOfCCTV;
	
	private ProgressDialog Dialog;
	
	private int Minute;
	private SharedPreferences pref;

	private double topLat, bottomLat, leftLon, rightLon;
	private double depLon, depLat, arrLon, arrLat;	
	private boolean flagEntirePath, flagNorMsg;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routesearch);

		tmap = (TMapView) findViewById(R.id.TMap);
		nameunTime = (TextView) findViewById(R.id.namenunTime);
		textTime = (TextView) findViewById(R.id.textTime);
		
		currPoint = new TMapPoint(0, 0);

		Intent intent = getIntent();
		depLat = intent.getDoubleExtra("depLat", 0);
		depLon = intent.getDoubleExtra("depLon", 0);
		arrLat = intent.getDoubleExtra("arrLat", 0);
		arrLon = intent.getDoubleExtra("arrLon", 0);
		
		startPoint = new TMapPoint(depLat, depLon);
		endPoint = new TMapPoint(arrLat, arrLon);
		
		StartEndCurrMaker("start", startPoint);
		StartEndCurrMaker("end", endPoint);
		
		new setPointOfCCTV().execute();		
		new MapRegisterTask().execute("");	
		
		customZoomLevel(startPoint, endPoint);
		
		GpsInfo gps = new GpsInfo(RouteSearchActivity.this);
		if (gps.isGetLocation()) {
			currPoint.setLatitude((double)gps.getLatitude());
			currPoint.setLongitude((double)gps.getLongitude());
			StartEndCurrMaker("curr", currPoint);
		}
		
		new findCurrPath().execute(currPoint,endPoint);
		
		pref = getSharedPreferences("sharedData", MODE_PRIVATE);
		Minute = pref.getInt("SET_TIME", 10);
		Log.d("testing", "" + Minute);
		
		tmap.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
			@Override
			public void onEnableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
				setMarkerOfCCTV();
			}
		});

		flagEntirePath = true;
		flagNorMsg = false;

		entirePath = (ImageButton) findViewById(R.id.entirePath);
		entirePath.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!flagEntirePath) {
					flagEntirePath = true;
					entirePath.setSelected(true);
					
		        	new RouteSearchTask().execute(startPoint,endPoint);
		        	textTime.setText("예측소요시간");
				} else {
					flagEntirePath = false;
					entirePath.setSelected(false);
					
					tmap.removeTMapPolyLine("path0");
					textTime.setText("실제소요시간");
					nameunTime.setText((int)(nameunDistance/50.0) + " min");
				}
			}
		});
		normalMsg = (ImageButton) findViewById(R.id.normalMessage);
		normalMsg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!flagNorMsg) {
					flagNorMsg = true;
					normalMsg.setSelected(true);
					
					
				} else {
					flagNorMsg = false;
					normalMsg.setSelected(false);
				}
			}
		});

		urgentMsg = (ImageButton) findViewById(R.id.urgentMessage);
		urgentMsg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(RouteSearchActivity.this);

				builder.setTitle("Alert").setMessage("Transfer urgent message?")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String smsNum = pref.getString("Number_0", "01052040392");
						String smsText = "HELP";
						Log.d("testing", smsNum);
						sendSMS(smsNum, smsText);
					}
				}).setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();

			}
		});
		
		backBtn = (ImageButton) findViewById(R.id.backBtn);		
		settingBtn = (ImageButton) findViewById(R.id.settingBtn);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		settingBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
				startActivity(intent);
			}
		});

	}
	
	public class findCurrPath extends AsyncTask <TMapPoint, Integer, TMapPolyLine> {
		@Override
		protected void onPreExecute(){
		/*	Dialog = new ProgressDialog(RouteSearchActivity.this); 
			Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
			Dialog.setMessage("Loading..."); 
			Dialog.show(); 
			super.onPreExecute(); */
		}
		@Override
		protected TMapPolyLine doInBackground(TMapPoint... params) {
			TMapPoint curr = params[0];
			TMapPoint end = params[1];
			
			TMapData data = new TMapData();
			try {				
				TMapPolyLine path = new TMapPolyLine();
				path = data.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, curr, end);
				
				return path;
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(TMapPolyLine path) {
			nameunDistance=0;
			
			if (path != null) {
				
				path.setLineColor(0x33495e);
				path.setLineWidth(15);
				nameunDistance += path.getDistance();
				tmap.addTMapPolyLine("curr", path);
				nameunTime.setText((int)(nameunDistance/50.0) + " min");
			}
			
		}
		
	}
	
	public void customZoomLevel(TMapPoint start, TMapPoint end){
		
		TMapPoint leftTop = new TMapPoint(0, 0);
		TMapPoint rightBottom = new TMapPoint(0, 0);
		
		if(start.getLatitude() > end.getLatitude()){
			leftTop.setLatitude(start.getLatitude());
			rightBottom.setLatitude(end.getLatitude());
			
			topLat=leftTop.getLatitude();
			bottomLat=rightBottom.getLatitude();
		}
		else{
			rightBottom.setLatitude(start.getLatitude());
			leftTop.setLatitude(end.getLatitude());
			
			bottomLat=leftTop.getLatitude();
			topLat=rightBottom.getLatitude();
		}
		
		if(start.getLongitude() < end.getLongitude()){
			leftTop.setLongitude(start.getLongitude());
			rightBottom.setLongitude(end.getLongitude());
			
			leftLon=leftTop.getLongitude();
			rightLon=rightBottom.getLongitude();
		}
		else{
			rightBottom.setLongitude(start.getLongitude());
			leftTop.setLongitude(end.getLongitude());
			
			rightLon=leftTop.getLongitude();
			leftLon=rightBottom.getLongitude();
		}
		
		tmap.zoomToTMapPoint(leftTop, rightBottom);
		tmap.setCenterPoint( (startPoint.getLongitude()+endPoint.getLongitude())/2 
				, (startPoint.getLatitude()+endPoint.getLatitude())/2 );
	}
	
	private class setPointOfCCTV extends AsyncTask<String, Void, JSONArray> {
		
        @Override
        protected JSONArray doInBackground(String... strs) {
        	
        	String jsonPage = getStringFromUrl(url);
        	        	
        	JSONObject json = null;
			try {
				json = new JSONObject(jsonPage);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    		JSONObject json2 = null;
			try {
				json2 = new JSONObject(json.getString("TB_GC_VVTV_INFO_ID01"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		JSONArray jArr = null;
			try {
				jArr = json2.getJSONArray("row");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}        	
        	return jArr;
        }
        @Override
        protected void onPostExecute(JSONArray result) {
        	
        	posOfCCTV = new ArrayList<TMapPoint>();	        	
        	
        	for(int i=0; i<result.length(); i++){
        		
        		JSONObject insideObject = null;
        		TMapPoint tmp = new TMapPoint(0, 0);
        		
        		try {
					insideObject = result.getJSONObject(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				
				try {
					tmp.setLatitude(insideObject.getDouble("GC_MAPX"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					tmp.setLongitude(insideObject.getDouble("GC_MAPY"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				posOfCCTV.add(tmp);        	
        	}
        	
/*        	for(TMapPoint t : posOfCCTV){
        		System.out.print("latitu : "+t.getLatitude()+" long : "+t.getLongitude()+"\n");
           	}*/
        	
        	setMarkerOfCCTV();

        } 
    }
		
	public String getStringFromUrl(String pUrl){
		
		BufferedReader bufreader=null;
		HttpURLConnection urlConnection = null;
		
		StringBuffer page=new StringBuffer();
		
		try {
			  
        	URL url= new URL(pUrl);
        	urlConnection = (HttpURLConnection) url.openConnection();
        	InputStream contentStream = urlConnection.getInputStream();			
        	
			bufreader = new BufferedReader(new InputStreamReader(contentStream,"UTF-8"));						
			String line = null;				
					
			while((line = bufreader.readLine())!=null){
					Log.d("line:",line);
					page.append(line);				
			}			
		
		} catch (IOException e) {			
			e.printStackTrace();
		}finally{
			try {
				bufreader.close();
				urlConnection.disconnect();
			} catch (IOException e) {				
				e.printStackTrace();
			}					
		}
		
		return page.toString();
	}

	
	public void setMarkerOfCCTV(){
		
		cerOfCCTV = new ArrayList<TMapPoint>();
			
		for(int i=0; i<posOfCCTV.size(); i++){
			TMapPoint leftTop = (TMapPoint) tmap.getLeftTopPoint();
			TMapPoint rightBottom = (TMapPoint) tmap.getRightBottomPoint();
			
			if(rightBottom.getLatitude() < posOfCCTV.get(i).getLatitude() &&
					 posOfCCTV.get(i).getLatitude() < leftTop.getLatitude()){
				
				if(leftTop.getLongitude() < posOfCCTV.get(i).getLongitude() &&
						posOfCCTV.get(i).getLongitude() < rightBottom.getLongitude()){
					
					cerOfCCTV.add(posOfCCTV.get(i));
				}				
			}
		}
		
		for(int i=0; i<cerOfCCTV.size(); i++){
			
			TMapMarkerItem tItem = new TMapMarkerItem();
			tItem.setTMapPoint(cerOfCCTV.get(i));
			tItem.setName("cctv"+i);
			tItem.setVisible(TMapMarkerItem.VISIBLE);
			Bitmap bm = ((BitmapDrawable)getResources().getDrawable(R.drawable.map_cctv_mark)).getBitmap();
			tItem.setIcon(bm);
			
			tItem.setPosition(0.5f, 1.0f);
			tmap.addMarkerItem("testid"+i, tItem);
		}
		
	}
		
	class RouteSearchTask extends AsyncTask<TMapPoint, Integer, ArrayList<TMapPolyLine>> {
		@Override
		protected void onPreExecute(){
		/*	Dialog = new ProgressDialog(RouteSearchActivity.this); 
			Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
			Dialog.setMessage("Loading..."); 
			Dialog.show(); 
			super.onPreExecute(); */
		}
		@Override
		protected ArrayList<TMapPolyLine> doInBackground(TMapPoint... params) {
			TMapPoint start = params[0];
			TMapPoint end = params[1];
			
			TMapData data = new TMapData();
			try {				
				ArrayList<TMapPolyLine> path = new ArrayList<TMapPolyLine>();
				path.add( data.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, start, end) );
				
				return path;
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<TMapPolyLine> path) {
			double totalDistance=0;
			
			if (path != null) {
				
				for(int i=0; i<path.size(); i++){
					
					path.get(i).setLineColor(Color.GRAY);
					path.get(i).setLineWidth(15);
					totalDistance += path.get(i).getDistance();
					
					tmap.addTMapPolyLine("path"+i, path.get(i));
					//tmap.addTMapPath("path"+i, path.get(i));					
				}

				nameunTime.setText((int)(totalDistance/50.0) + " min");
			}
			
			//Dialog.dismiss();
		}
	}
	
	public void StartEndCurrMaker(String name, TMapPoint pos){
		TMapMarkerItem tItem = new TMapMarkerItem();
		tItem.setTMapPoint(pos);
		tItem.setName(name);
		tItem.setVisible(TMapMarkerItem.VISIBLE);
		
		if(name.equals("start")){
			Bitmap bm = ((BitmapDrawable)getResources().getDrawable(R.drawable.map_departure_mark)).getBitmap();
			tItem.setIcon(bm);
		}
		else if(name.equals("end")){
			Bitmap bm = ((BitmapDrawable)getResources().getDrawable(R.drawable.map_arrive_mark)).getBitmap();
			tItem.setIcon(bm);
		}
		else if(name.equals("curr")){
			Bitmap bm = ((BitmapDrawable)getResources().getDrawable(R.drawable.map_point_mark)).getBitmap();
			tItem.setIcon(bm);
		}
		
		tItem.setPosition(0.5f, 1.0f);
		tmap.addMarkerItem(name, tItem);
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
			// tmap.setZoomLevel(16);
			tmap.setMapType(TMapView.MAPTYPE_STANDARD);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Send SMS

	public void sendSMS(String smsNumber, String smsText) {

		PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
		PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context mContext, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					// �쟾�넚 �꽦怨�
					Toast.makeText(mContext, "Send Finish", Toast.LENGTH_SHORT).show();
					break;
				default:
					// �쟾�넚 �떎�뙣
					Toast.makeText(mContext, "Send Fail", Toast.LENGTH_SHORT).show();
				}
			}
		}, new IntentFilter("SMS_SENT_ACTION"));

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context mContext, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					// �룄李� �셿猷�
					Toast.makeText(mContext, "SMS arrive Finish", Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					// �룄李� �븞�맖
					Toast.makeText(mContext, "SMS arrive Fail", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter("SMS_DELIVERED_ACTION"));

		SmsManager mSmsManager = SmsManager.getDefault();
		mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
	}

}
