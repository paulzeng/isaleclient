package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：我的汇报列表界面
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.adapter.WorkListAdapter;
import com.zjrc.isale.client.ui.adapter.WorkListAdapter.WorkItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;

public class WorkListActivity extends BaseActivity implements
		OnItemClickListener {
	private Button btn_titlebar_back;
	private Button btn_titlebar_search;

	private TextView tv_titlebar_title;

	private PullToRefreshListView lv_list;

	private ListView actualListView;

	private Button btn_add;

	private WorkListAdapter adapter;

	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;
	
	String title = "";
	String begindate = "";
	int typeindex = 0;

	private int type;
	private Handler handler = new Handler() {
		public void handleMessage(Message smsg) {
		}
	};
	private RefreshEventReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.work_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_search_add_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);

		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);

		tv_titlebar_title.setText(R.string.work_report);

		btn_titlebar_search = (Button) findViewById(R.id.btn_titlebar_search);

		btn_titlebar_search.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					intent.setClass(WorkListActivity.this, WorkreportSearchActivity.class);
					intent.putExtra("title", title);
					intent.putExtra("begindate", begindate);
					intent.putExtra("typeindex", typeindex);
					startActivityForResult(intent, Constant.WORKREPORT_SEARCH);
				}
			}
		});

		lv_list = (PullToRefreshListView) findViewById(R.id.lv_workreportlist);

		actualListView = lv_list.getRefreshableView();
		adapter = new WorkListAdapter(this, getLayoutInflater(), handler);
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);

		lv_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (actualListView.getFirstVisiblePosition() == 0) {
					WorkItem item = (WorkItem) adapter.getItem(actualListView
							.getFirstVisiblePosition());
					if (item != null) {
						request(item.getId(), "0", false);
					}
				} else if (actualListView.getLastVisiblePosition() == (adapter
						.getCount() + 1)) {
					WorkItem item = (WorkItem) adapter.getItem(actualListView
							.getLastVisiblePosition() - 2);
					if (item != null) {
						request(item.getId(), "1", false);
					}
				}
			}
		});

		btn_add = (Button) findViewById(R.id.btn_titlebar_add);
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					intent.setClass(WorkListActivity.this,
							WorkReportSubmitActivity.class);
					startActivity(intent);
				}
			}
		});
		listview_foreGround = findViewById(R.id.ll_listview_foreground);
		tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
		iv_icon = (ImageView) listview_foreGround.findViewById(R.id.iv_icon);
		request("", "0", true);
		addReceiver();
	}

	private void addReceiver() {
		receiver = new RefreshEventReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.WORKREPORT_ADD_ACTION);
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
		if (requestCode == Constant.WORKREPORT_SEARCH) {
			if (resultCode == Constant.RESULT_WORKREPORT_SEARCH) {
				title = data.getStringExtra("title");
				typeindex = data.getIntExtra("type", 0);
                begindate = data.getStringExtra("begindate");
				adapter.clearItem();
                adapter.notifyDataSetChanged();
				request("", "0", true);
			}
		}
	}

	public void request(String id, String order, boolean needProgress) {
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            params.put("title", title);
            params.put("type", (typeindex > 0 ? String.valueOf(typeindex-1) : ""));
            params.put("date", begindate);
            params.put("corpworkreportid", id);
            params.put("order", order);
            request("workReport!list?code=" + Constant.WORKREPORT_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		WorkItem item = (WorkItem) adapter.getItem(arg2 - 1);
		Intent intent = new Intent();
		intent.setClass(WorkListActivity.this, WorkItemActivity.class);
		intent.putExtra("corpworkreportid", item.getId());
		this.startActivity(intent);
	}

	private class RefreshEventReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Constant.WORKREPORT_ADD_ACTION)) {
				adapter.clearItem();
                adapter.notifyDataSetChanged();
				request("", "0", true);
			}
		}
	}

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(begindate) || typeindex != 0) {
            title = "";
            begindate = "";
            typeindex = 0;
            adapter.clearItem();
            adapter.notifyDataSetChanged();
            request("", "0", true);
            Toast.makeText(this, getString(R.string.v2_clearfilter), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.WORKREPORT_LIST.equals(code)) {//工作汇报列表
            	JsonArray body = response.getAsJsonArray("body");
                if (body != null) {
                    for (JsonElement elem : body) {
                        JsonObject obj = (JsonObject) elem;
                        String type = obj.get("type").getAsString();
						Integer index = Integer.valueOf(type);
						adapter.addItem(
								obj.get("id").getAsString(),
								obj.get("begindate").getAsString(),
								obj.get("enddate").getAsString(),
								"",
								"",
								"",
								"",
								obj.get("title").getAsString(),
								(this.getResources()
										.getStringArray(R.array.workreporttypes))[index]);
                    }
                    adapter.notifyDataSetChanged();
                }
                Log.i("info", "adapter:" + adapter.getCount());
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
}
