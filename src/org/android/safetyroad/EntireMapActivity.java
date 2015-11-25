package org.android.safetyroad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.TMapPathType;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

public class EntireMapActivity extends Activity {

	public static final int REQUEST_CODE_SETTING = 1003;
	public static final int REQUEST_CODE_ROUTE = 1004;
	public static final String APP_KEY = "62305c74-edf5-3198-bdce-ab26eced4be6";
	public static String url = "http://openapi.seoul.go.kr:8088/516943625268733134394857784576/json/TB_GC_VVTV_INFO_ID01/1/400/";

	private TMapView tmap;
	private TMapPoint startPoint;
	private TMapPoint endPoint;
	private ImageButton settingBtn;
	private ImageButton backBtn;
		
	private ArrayList<TMapPoint> posOfCCTV;	
	private ArrayList<TMapPoint> cerOfCCTV;	
	
	private double depLon, depLat, arrLon, arrLat;	
	private ProgressDialog Dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_entiremap);
		
		tmap = (TMapView) findViewById(R.id.entireMapLayout);
		
		settingBtn = (ImageButton) findViewById(R.id.settingBtn);
		settingBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SETTING);
			}
		});
		
		backBtn = (ImageButton) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});		
		
		//mainActivity!!! transfer data!!!
		Intent intent = getIntent();
		depLat = intent.getDoubleExtra("depLat", 0);
		depLon = intent.getDoubleExtra("depLon", 0);
		arrLat = intent.getDoubleExtra("arrLat", 0);
		arrLon = intent.getDoubleExtra("arrLon", 0);
		
/*		startPoint = new TMapPoint(37.481910, 126.883364);
		endPoint = new TMapPoint(37.486258, 126.882572);*/
		
		startPoint = new TMapPoint(depLat, depLon);
		endPoint = new TMapPoint(arrLat, arrLon);
		
		Log.d("testing", "***start lat: "+startPoint.getLatitude()+" start long: "+startPoint.getLongitude());
		Log.d("testing", "***end lat: "+endPoint.getLatitude()+" end long: "+endPoint.getLongitude());
		
		StartEndMaker("start", startPoint);
		StartEndMaker("end", endPoint);
		
		new setPointOfCCTV().execute();		
		new MapRegisterTask().execute("");	
		
		tmap.setCenterPoint( (startPoint.getLongitude()+endPoint.getLongitude())/2 
								, (startPoint.getLatitude()+endPoint.getLatitude())/2 );
		
		new RouteSearchTask().execute(startPoint,endPoint);
								
		Button routeSearchBtn = (Button) findViewById(R.id.routeSearchBtn);		
		routeSearchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), RouteSearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_ROUTE);
			}
		});		
				
	}
	
	private class setPointOfCCTV extends AsyncTask<String, Void, JSONArray> {
		@Override
		protected void onPreExecute(){
		}
		
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
			TMapPoint leftTop = tmap.getLeftTopPoint();
			TMapPoint rightBottom = tmap.getRightBottomPoint();
			
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
			Dialog = new ProgressDialog(EntireMapActivity.this); 
			Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
			Dialog.setMessage("寃쎈줈 寃??以묒엯?덈떎."); 
			Dialog.show(); 
			super.onPreExecute(); 
		}
		@Override
		protected ArrayList<TMapPolyLine> doInBackground(TMapPoint... params) {
			TMapPoint start = params[0];
			TMapPoint end = params[1];
			
			TMapData data = new TMapData();
			try {				
				ArrayList<TMapPolyLine> path = new ArrayList<TMapPolyLine>();
				
				TMapPoint middlePoint = new TMapPoint(37.485905, 126.883331);
				
				path.add( data.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, start, middlePoint) );
				path.add( data.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, middlePoint, end) );		
			
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
					
					path.get(i).setLineColor(0x33495e);
					path.get(i).setLineWidth(15);
					totalDistance += path.get(i).getDistance();
					
					tmap.addTMapPolyLine("path"+i, path.get(i));
					//tmap.addTMapPath("path"+i, path.get(i));					
				}

				TextView totalMin = (TextView) findViewById(R.id.totalMinute);
				totalMin.setText(""+ (int)(totalDistance/50.0) + " min");
			}
			
			Dialog.dismiss();
		}
	}
	
	public void StartEndMaker(String name, TMapPoint pos){
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
			tmap.setZoomLevel(16);
			tmap.setMapType(TMapView.MAPTYPE_STANDARD);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
