package org.android.safetyroad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SettingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		
		ListView list = (ListView)findViewById(R.id.setting_listview);

		List<Map<String, String>> dataList = this.createData();
        
        SimpleAdapter adapter= new SimpleAdapter(
        	this, dataList, android.R.layout.simple_list_item_2, new String[]{"name", "phoneNumber"},
        	new int[]{android.R.id.text1, android.R.id.text2}
        );
        
        list.setAdapter(adapter);
    }
    
    private List<Map<String, String>> createData(){
    	List<Map<String, String>> retDataList = new ArrayList<Map<String,String>>();
    	
    	for(int i=0; i<5; i++){
    		Map<String, String> data = new HashMap<String, String>();
    		data.put("name", "사람이름"+i);
    		data.put("phoneNumber", "폰번호"+i);
    		
    		retDataList.add(data);
    	}
    	
    	return retDataList;
    }
}