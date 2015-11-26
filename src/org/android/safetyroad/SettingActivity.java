package org.android.safetyroad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class SettingActivity extends Activity {
	private ListView mListView = null;
	private ListViewAdapter mAdapter = null;
	private ImageButton backBtn;
	private ImageButton setting_1min, setting_3min, setting_5min, setting_10min, setting_15min;
	private int defaultMin = 10;
	SharedPreferences pref;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		pref = getSharedPreferences("sharedData", MODE_PRIVATE);
		editor = pref.edit();

		/***********************
		 * Back Button
		 ***********************/
		backBtn = (ImageButton) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		/***********************
		 * Plus Button
		 ***********************/
		ImageButton plusBtn = (ImageButton) findViewById(R.id.phone_plus);
		plusBtn.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				plusBtnInputDialog();
			}
		});

		/***********************
		 * List
		 ***********************/
		mListView = (ListView) findViewById(R.id.setting_listview);

		mAdapter = new ListViewAdapter(this);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListData mData = mAdapter.mListData.get(position);
				Toast.makeText(SettingActivity.this, mData.mName + "  " + mData.mNumber, Toast.LENGTH_SHORT).show();
			}
		});

		/***********************
		 * Miniute Button
		 ***********************/
		setting_1min = (ImageButton) findViewById(R.id.setting_1min);
		setting_3min = (ImageButton) findViewById(R.id.setting_3min);
		setting_5min = (ImageButton) findViewById(R.id.setting_5min);
		setting_10min = (ImageButton) findViewById(R.id.setting_10min);
		setting_15min = (ImageButton) findViewById(R.id.setting_15min);

		// default minute
		if (pref.getBoolean("isDefaultUse", true))
			settingMin(defaultMin);
		else {
			int tmp = pref.getInt("SET_TIME", 10);
			settingMin(tmp);
		}

		setting_1min.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				settingMin(1);
			}
		});
		setting_3min.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				settingMin(3);
			}
		});
		setting_5min.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				settingMin(5);
			}
		});
		setting_10min.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				settingMin(10);
			}
		});
		setting_15min.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				settingMin(15);
			}
		});

	}

	/***********************
	 * Plus Button
	 ***********************/
	public void plusBtnInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("비상연락망 추가");
		builder.setCancelable(false);

		LayoutInflater inflater = getLayoutInflater();

		final View inputView = inflater.inflate(R.layout.activity_setting_custom_dialog, null);

		builder.setView(inputView);

		// 취소 버튼
		builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), "Cancle", Toast.LENGTH_SHORT).show();
			}
		});

		// 확인 버튼
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				EditText et1 = (EditText) inputView.findViewById(R.id.editName);
				EditText et2 = (EditText) inputView.findViewById(R.id.editPhoneNumber);

				String userName = et1.getText().toString();
				String userPhoneNumber = et2.getText().toString();

				mAdapter.addItem(userName, userPhoneNumber);

				Toast.makeText(getApplicationContext(), "입력 완료", Toast.LENGTH_SHORT).show();
			}
		});

		builder.create();
		builder.show();
	}

	/***********************
	 * List
	 ***********************/

	private class ViewHolder {
		public TextView mName;
		public TextView mNumber;
	}

	private class ListViewAdapter extends BaseAdapter {
		private Context mContext = null;
		private ArrayList<ListData> mListData = new ArrayList<ListData>();

		public ListViewAdapter(Context mContext) {
			super();
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return mListData.size();
		}

		@Override
		public Object getItem(int position) {
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// if(position >= 5){
			// Toast.makeText(SettingActivity.this, "연락처 5개를 초과할 수 없습니다.",
			// Toast.LENGTH_SHORT).show();
			// }
			// else{
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();

				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.activity_setting_custom_listview, null);

				holder.mName = (TextView) convertView.findViewById(R.id.listName);
				holder.mNumber = (TextView) convertView.findViewById((R.id.listPhoneNumber));

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ListData mData = mListData.get(position);
			Log.d("testing", "1" + mData.mName + ":" + mData.mNumber);

			holder.mName.setText(mData.mName);
			holder.mNumber.setText(mData.mNumber);

			ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.listDeleteButton);
			// Toast.makeText(SettingActivity.this, "deleteBtn",
			// Toast.LENGTH_SHORT).show();
			deleteBtn.setOnClickListener(new ImageButton.OnClickListener() {
				public void onClick(View v) {
					ListData mData = mAdapter.mListData.get(position);
					Toast.makeText(SettingActivity.this, mData.mName + "is deleted.", Toast.LENGTH_SHORT).show();
					remove(position);
				}
			});
			// }

			return convertView;
		}

		public void addItem(String mName, String mNumber) {
			ListData addInfo = new ListData();
			
			editor.putString("Name_0", mName);
			editor.putString("Number_0", mNumber);
			editor.putBoolean("isNameExist", true);
			Log.d("testing", "2" + mName + ":" + mNumber);

			addInfo.mName = mName;
			addInfo.mNumber = mNumber;

			mListData.add(addInfo);
		}

		public void remove(int position) {
			mListData.remove(position);
			dataChange();
		}

		public void dataChange() {
			mAdapter.notifyDataSetChanged();
		}

	}

	private void settingMin(int time) {
		pref = getSharedPreferences("sharedData", MODE_PRIVATE);
		editor = pref.edit();
		editor.putInt("SET_TIME", time);
		editor.putBoolean("isDefaultUse", false);
		editor.commit();

		setMinuteImg(time);
	}

	private void setMinuteImg(int time) {
		switch (time) {
		case 1:
			setting_1min.setSelected(true);
			setting_3min.setSelected(false);
			setting_5min.setSelected(false);
			setting_10min.setSelected(false);
			setting_15min.setSelected(false);
			break;

		case 3:
			setting_1min.setSelected(false);
			setting_3min.setSelected(true);
			setting_5min.setSelected(false);
			setting_10min.setSelected(false);
			setting_15min.setSelected(false);
			break;

		case 5:
			setting_1min.setSelected(false);
			setting_3min.setSelected(false);
			setting_5min.setSelected(true);
			setting_10min.setSelected(false);
			setting_15min.setSelected(false);
			break;

		case 10:
			setting_1min.setSelected(false);
			setting_3min.setSelected(false);
			setting_5min.setSelected(false);
			setting_10min.setSelected(true);
			setting_15min.setSelected(false);
			break;

		case 15:
			setting_1min.setSelected(false);
			setting_3min.setSelected(false);
			setting_5min.setSelected(false);
			setting_10min.setSelected(false);
			setting_15min.setSelected(true);
			break;

		}

	}
}