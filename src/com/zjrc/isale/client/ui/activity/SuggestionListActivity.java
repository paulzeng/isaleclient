package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者：贺彬
 * @功能描述：我的反馈列表
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.zjrc.isale.client.ui.adapter.SuggestionListAdapter;
import com.zjrc.isale.client.ui.adapter.SuggestionListAdapter.SuggestionItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SuggestionListActivity extends BaseActivity implements
		OnItemClickListener {
	private Button btn_titlebar_back;
	private Button btn_titlebar_search;

	private TextView tv_titlebar_title;

	private PullToRefreshListView lv_list;

	private ListView actualListView;

	private Button btn_add;

	private SuggestionListAdapter adapter;
	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;
	private int type;
	String id="";
	private Handler handler = new Handler() {
		public void handleMessage(Message smsg) {
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.suggestion_list);
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

		tv_titlebar_title.setText(R.string.home_mysuggestion);

		btn_titlebar_search = (Button) findViewById(R.id.btn_titlebar_search);

		btn_titlebar_search.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					intent.setClass(SuggestionListActivity.this, SuggestionSearchActivity.class);
					intent.putExtra("key", key);
					startActivityForResult(intent, Constant.RESULT_SUGGESTIONLIST_REFRESH);
				}
			}
		});

		type = 0;// 全部

		lv_list = (PullToRefreshListView) findViewById(R.id.lv_suggestionlist);

		actualListView = lv_list.getRefreshableView();
		adapter = new SuggestionListAdapter(this, getLayoutInflater(), handler);
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);

		lv_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (actualListView.getFirstVisiblePosition() == 0) {
					SuggestionItem item = (SuggestionItem) adapter
							.getItem(actualListView.getFirstVisiblePosition());
					if (item != null) {
						id="";
						request(id, "0", false);
					}
				} else if (actualListView.getLastVisiblePosition() == (adapter
						.getCount() + 1)) {
					SuggestionItem item = (SuggestionItem) adapter
							.getItem(actualListView.getLastVisiblePosition() - 2);
					if (item != null) {
						id=item.getSuggestionid();
						request(id, "1", false);
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
					intent.setClass(SuggestionListActivity.this,
							SuggestionSubmitActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("operate", "insert");
					intent.putExtras(bundle);
					startActivityForResult(intent,
							Constant.RESULT_SUGGESTIONLIST_REFRESH);
				}
			}
		});
		listview_foreGround = findViewById(R.id.ll_listview_foreground);
		tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
		iv_icon = (ImageView) listview_foreGround.findViewById(R.id.iv_icon);
		request(id, "0", true);
		addReceiver();
	}

	private RefreshEventReceiver receiver;

	private void addReceiver() {
		receiver = new RefreshEventReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.SUGGESTION_ADD_ACTION);
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
		if (requestCode == Constant.RESULT_SUGGESTIONLIST_REFRESH) {
			if (resultCode == Activity.RESULT_OK) {
				key = data.getStringExtra("key");
                id= "";
				request(id, "0", true);
			}
		}
	}

	String title = "";
	String date = "";
	int typeindex = 0;
	String key = "";

	public void request(String id, String order, boolean needProgress) {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("title", key);
            params.put("corpsuggestionid", id);
            params.put("order", order);
            request("suggestion!list?code=" + Constant.SUGGESTION_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		SuggestionItem item = (SuggestionItem) adapter.getItem(arg2 - 1);
		Intent intent = new Intent();
		intent.setClass(SuggestionListActivity.this,
				SuggestionDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("suggestionid", item.getSuggestionid());
		intent.putExtras(bundle);
		startActivityForResult(intent, Constant.RESULT_SUGGESTIONLIST_REFRESH);
	}

	private class RefreshEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Constant.SUGGESTION_ADD_ACTION)
					|| action
							.equalsIgnoreCase(Constant.SUGGESTION_MODIFY_ACTION)
					|| action
							.equalsIgnoreCase(Constant.SUGGESTION_DELETE_ACTION)) {
				id="";
				request(id, "0", true);
			}
		}
	}

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(key)) {
            key = "";
        	id="";
			request(id, "0", true);
            Toast.makeText(this, getString(R.string.v2_clearfilter), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.SUGGESTION_LIST.equals(code)) {//投诉建议列表
            	JsonArray body = response.getAsJsonArray("body");
                if (body != null) {
                    if (TextUtils.isEmpty(id)) {
                        adapter.clearItem();
                    }
                    for (JsonElement elem : body) {
                        JsonObject obj = (JsonObject) elem;
                        adapter.addItem(obj.get("id").getAsString(),
                        		obj.get("title").getAsString(),
                        		obj.get("type").getAsString(),
                        		obj.get("date").getAsString(),
                        		obj.get("signstate").getAsString(),
                        		obj.get("signresult").getAsString());
                    }
                    adapter.notifyDataSetChanged();
                }
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
