package org.android.safetyroad;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;


public class SettingActivity extends Activity {
	private ListView mListView = null;
	private ListViewAdapter mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mListView = (ListView)findViewById(R.id.setting_listview);

		mAdapter = new ListViewAdapter(this);
		mListView.setAdapter(mAdapter);

		mAdapter.addItem(getResources().getDrawable(R.drawable.ic_launcher), "문대영", "01074439595");

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListData mData = mAdapter.mListData.get(position);
				Toast.makeText(SettingActivity.this, mData.mName, Toast.LENGTH_SHORT).show();
			}
		});

    }
    
   private  class ViewHolder{
	   public ImageView mImage;
	   public TextView mName;
	   public TextView mNumber;
   }

	private  class ListViewAdapter extends BaseAdapter{
		private Context mContext = null;
		private ArrayList<ListData> mListData = new ArrayList<ListData>();

		public ListViewAdapter(Context mContext){
			super();
			this.mContext = mContext;
		}

		@Override
		public int getCount(){
			return mListData.size();
		}

		@Override
		public Object getItem(int position){
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position){
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			ViewHolder holder;

			if(convertView == null) {
				holder = new ViewHolder();

				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.activity_setting_custom_listview, null);

				holder.mImage = (ImageView) convertView.findViewById(R.id.pImage);
				holder.mName = (TextView) convertView.findViewById(R.id.pName);
				holder.mNumber = (TextView) convertView.findViewById((R.id.pNumber));

				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
			}

			ListData mData = mListData.get(position);

			if(mData.mImage != null){
				holder.mImage.setVisibility(View.VISIBLE);
				holder.mImage.setImageDrawable(mData.mImage);
			}
			else{
				holder.mImage.setVisibility(View.GONE);
			}

			holder.mName.setText(mData.mName);
			holder.mNumber.setText(mData.mNumber);

			return convertView;
		}

		public void addItem(Drawable icon, String mName, String mNumber){
			ListData addInfo = null;
			addInfo = new ListData();
			addInfo.mImage = icon;
			addInfo.mName = mName;
			addInfo.mNumber = mNumber;

			mListData.add(addInfo);
		}

		public  void remove(int position){
			mListData.remove(position);
			dataChange();
		}

		public  void  sort(){
			Collections.sort(mListData, ListData.ALPHA_COMPARATOR);
			dataChange();
		}

		public void dataChange(){
			mAdapter.notifyDataSetChanged();
		}

	}
}