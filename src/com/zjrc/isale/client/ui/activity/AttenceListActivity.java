package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：我的考勤列表界面
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.adapter.AttenceListAdapter;
import com.zjrc.isale.client.ui.adapter.AttenceListAdapter.AttenceItem;
import com.zjrc.isale.client.ui.widgets.CustomDatePicker;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttenceListActivity extends BaseActivity implements
		OnItemClickListener {
	private Button btn_titlebar_back;

	private Button btn_titlebar_search;

	private TextView tv_titlebar_title;

	private EditText et_attencedate;

	private PullToRefreshListView lv_list;

	private ListView actualListView;

	private Button btn_add;

	private AttenceListAdapter adapter;

	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;

	int type;

	private Handler handler = new Handler() {
		public void handleMessage(Message smsg) {
			switch (smsg.what) {
			case Constant.TRAVELLIST_BTN_CLICK:
				break;
			default:
				break;
			}
		}
	};
	CustomDatePicker datePicker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.attence_list);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebarsearch_small);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_search_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);

		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);

		tv_titlebar_title.setText(R.string.work_myattence);

		type = 0;// 0全部 ,1 签到,2签退;

		btn_titlebar_search = (Button) findViewById(R.id.btn_titlebar_search);
		btn_titlebar_search.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(AttenceListActivity.this,
						AttenceSearchActivity.class);
				intent.putExtra("type", type);
				startActivityForResult(intent, Constant.ATTANCE_SEARCH);
			}
		});

		et_attencedate = (EditText) findViewById(R.id.et_attencedate);

		et_attencedate.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					datePicker = new CustomDatePicker(AttenceListActivity.this,
							"请选择开始时间", new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									et_attencedate.setText(datePicker.getYear()
											+ "-" + datePicker.getMonth() + "-"
											+ datePicker.getDay());
									datePicker.dismiss();

								}
							}, new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									datePicker.dismiss();
								}
							}, et_attencedate.getText().toString());
					datePicker.show();
					break;
				default:
					break;
				}
				return false;
			}
		});

		lv_list = (PullToRefreshListView) findViewById(R.id.lv_attencelist);
		actualListView = lv_list.getRefreshableView();
		adapter = new AttenceListAdapter(this, getLayoutInflater(), handler);
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);

		lv_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (actualListView.getFirstVisiblePosition() == 0) {
					lv_list.setPullLabel("加载中...");
					AttenceItem item = (AttenceItem) adapter
							.getItem(actualListView.getFirstVisiblePosition());
					if (item != null) {
						request(item.getAttenceid(), "0", false);
					}
				} else if (actualListView.getLastVisiblePosition() == adapter
						.getCount() + 1) {
					AttenceItem item = (AttenceItem) adapter
							.getItem(actualListView.getLastVisiblePosition() - 2);
					if (item != null) {
						request(item.getAttenceid(), "1", false);
					}
				}
			}
		});

		btn_add = (Button) findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AttenceListActivity.this,
						AttenceSubmitActivity.class);
				intent.putExtra("oper", "1");
				startActivityForResult(intent, Constant.ATTANCE_ADD);
			}
		});

		listview_foreGround = findViewById(R.id.ll_listview_foreground);
		tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
		iv_icon = (ImageView) listview_foreGround.findViewById(R.id.iv_icon);
		request("", "0", true);
		addReceiver();
	}

	private RefreshEventReceiver receiver;

	private void addReceiver() {
		receiver = new RefreshEventReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ATTENCE_CHECKIN_ACTION);
		filter.addAction(Constant.ATTENCE_CHECKOUT_ACTION);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.ATTANCE_SEARCH) {
			if (resultCode == Constant.RESULT_ATTANCELIST_SEARCH) {
				type = data.getIntExtra("type", 0);
				adapter.clearItem();
				adapter.notifyDataSetChanged();
				request("", "0", true);
			}
		}
	}

	public void requestDelete(String id) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("operate", "");
            params.put("date", "");
            params.put("province", "");
            params.put("city", "");
            params.put("zone", "");
            params.put("desc", "");
            params.put("days", "");
            params.put("begindate", "");
            params.put("enddate", "");
            params.put("corptravelid", id);
            request("travel!submit?code=" + Constant.TRAVEL_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	public void request(String id, String order, boolean needprogress) {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            int typeindex = type;
            typeindex = typeindex - 1;
            Log.i("info", "typeindex:" + typeindex);
            params.put("type", (typeindex == -1 ? "" : String.valueOf(typeindex)));
            params.put("date", et_attencedate.getText().toString());
            params.put("bizworkcheckid", id);
            params.put("order", order);
            request("attenceCheck!list?code=" + Constant.ATTENCE_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
		if (response != null) {
			String functionno = response.getText("root.functionno");
			if (functionno.equalsIgnoreCase(Constant.TRAVEL_SUBMIT)) {
//				adapter.clearItem();
//				adapter.notifyDataSetChanged();
//				request("", "0", true);
			}
		}
	}

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TRAVEL_SUBMIT.equals(code)) {
                adapter.clearItem();
                adapter.notifyDataSetChanged();
                request("", "0", true);
            } else if (Constant.ATTENCE_LIST.equals(code)) {//考勤登记列表
            	JsonArray body = response.getAsJsonArray("body");
                if (body != null) {
                	for (JsonElement elem : body) {
                		JsonObject obj = (JsonObject) elem;
                		String type = obj.get("type").getAsString();
						Integer typeindex = Integer.valueOf(type);
						String result = obj.get("result").getAsString();
						Integer resultindex = Integer.valueOf(result);
						adapter.addItem(
								obj.get("id").getAsString(),
								obj.get("date").getAsString(),
								obj.get("time").getAsString(),
								"",
								"",
								obj.get("address").getAsString(),
								(this.getResources()
										.getStringArray(R.array.attenceresult))[resultindex],
								(this.getResources()
										.getStringArray(R.array.attencetypes))[typeindex]);
                	}
                	adapter.notifyDataSetChanged();
                }
                if (adapter.getCount() <= 0) {
					listview_foreGround.setVisibility(View.VISIBLE);
					lv_list.setVisibility(View.GONE);
				} else {
					listview_foreGround.setVisibility(View.GONE);
					lv_list.setVisibility(View.VISIBLE);
				}
				lv_list.onRefreshComplete();
            }
        }
    }

    @Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		AttenceItem item = (AttenceItem) adapter.getItem(arg2 - 1);
		Intent intent = new Intent();
		intent.setClass(AttenceListActivity.this, AttenceDetailActivity.class);
		intent.putExtra("bizworkcheckid", item.getAttenceid());
		startActivity(intent);
	}

	private class RefreshEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Constant.ATTENCE_CHECKIN_ACTION)
					|| action
							.equalsIgnoreCase(Constant.ATTENCE_CHECKOUT_ACTION)) {
				adapter.clearItem();
				adapter.notifyDataSetChanged();
				request("", "0", true);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (type != 0) {
			type = 0;
			adapter.clearItem();
			adapter.notifyDataSetChanged();
			request("", "0", true);

			Toast.makeText(this, getString(R.string.v2_clearfilter),
					Toast.LENGTH_SHORT).show();
		} else {
			super.onBackPressed();
		}
	}
}
