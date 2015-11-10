package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：我的假期列表界面
 */

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
import com.zjrc.isale.client.ui.adapter.VacationListAdapter;
import com.zjrc.isale.client.ui.adapter.VacationListAdapter.VacationItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;
import java.util.Map;

public class VacationListActivity extends BaseActivity implements
		OnItemClickListener {
	private Button btn_titlebar_back;
	private Button btn_titlebar_search;

	private TextView tv_titlebar_title;

	private PullToRefreshListView lv_list;

	private ListView actualListView;

	private Button btn_add;

	private VacationListAdapter adapter;

	private int type;

	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;
	private String id="";

	private Handler handler = new Handler() {
		public void handleMessage(Message smsg) {
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.vacation_list);
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

		tv_titlebar_title.setText(R.string.vacation_list);

		btn_titlebar_search = (Button) findViewById(R.id.btn_titlebar_search);

		btn_titlebar_search.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					intent.setClass(VacationListActivity.this, VacationSearchActivity.class);
					intent.putExtra("type", type);
					startActivityForResult(intent, Constant.VACATION_SEARCH);
				}
			}
		});

		type = 0;// 全部

		lv_list = (PullToRefreshListView) findViewById(R.id.lv_vacationlist);

		actualListView = lv_list.getRefreshableView();
		adapter = new VacationListAdapter(this, getLayoutInflater(), handler);
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);

		lv_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (actualListView.getFirstVisiblePosition() == 0) {
					VacationItem item = (VacationItem) adapter
							.getItem(actualListView.getFirstVisiblePosition());
					if (item != null) {//刷新
		                id="";
						request(id, "0", false);
					}
				} else if (actualListView.getLastVisiblePosition() == (adapter
						.getCount() + 1)) {//加載更多
					VacationItem item = (VacationItem) adapter
							.getItem(actualListView.getLastVisiblePosition() - 2);
					if (item != null) {
		                id=item.getVacationid();
						request(item.getVacationid(), "1", false);
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
					intent.setClass(VacationListActivity.this,
							VacationSubmitActivity.class);
					startActivityForResult(intent, Constant.VACATION_ADD);
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
		filter.addAction(Constant.VACATION_ADD_ACTION);
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
		if (requestCode == Constant.VACATION_SEARCH) {
			if (resultCode == Constant.RESULT_VACATION_SEARCH) {
				type = data.getIntExtra("type", 0);
                id="";
				request(id, "0", true);
			}
		}
	}

	public void request(String id, String order, boolean needProgress) {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            int typeindex = type;
			typeindex = typeindex - 1;
            params.put("type", (typeindex == -1 ? "" : String.valueOf(typeindex)));
            params.put("date", "");
            params.put("status", "");
            params.put("corpvacationid", id);
            params.put("order", order);
            request("vacation!list?code=" + Constant.VACATION_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		VacationItem item = (VacationItem) adapter.getItem(arg2 - 1);
		Intent intent = new Intent();
		intent.setClass(VacationListActivity.this, VacationDetailActivity.class);
		intent.putExtra("corpvacationid", item.getVacationid());
		this.startActivity(intent);
	}

	private class RefreshEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Constant.VACATION_ADD_ACTION)||action.equalsIgnoreCase(Constant.VACATION_AUDIT_ACTION)) {
				id="";
				request(id, "0", true);
			}
		}
	}

    @Override
    public void onBackPressed() {
        if (type != 0) {
            type = 0;
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
            if (Constant.VACATION_LIST.equals(code)) {//请假申请列表
            	JsonArray body = response.getAsJsonArray("body");
                if (body != null) {
                	if (TextUtils.isEmpty(id)) {
                		adapter.clearItem();
                	}
                	for (JsonElement elem : body) {
                		JsonObject obj = (JsonObject) elem;
                		
                		String type = obj.get("type").getAsString();
						Integer typeindex = Integer.valueOf(type);
						String[] vacationtypes = getResources()
								.getStringArray(R.array.vacationtypes);
						String result = "";
						String signstate = obj.get("signstate").getAsString();
						Integer signstateindex = Integer.valueOf(signstate);
                        String signresult="";
						if (signstateindex == 0)
							result = (getResources()
									.getStringArray(R.array.vacationstate))[signstateindex];
						else if (signstateindex == 1) {
                            signresult= obj.get("signresult").getAsString();
							Integer signresultindex = Integer
									.valueOf(signresult);
							result = (getResources()
									.getStringArray(R.array.vacationstate))[signresultindex + 1];
						}
						adapter.addItem(obj.get("id").getAsString(),
								obj.get("submitdate").getAsString(),
								obj.get("vacationdays").getAsString(),
								obj.get("begindate").getAsString(),
								obj.get("enddate").getAsString(),
								obj.get("signstate").getAsString(),
                                signresult,
                                typeindex>vacationtypes.length-1?"其他":vacationtypes[typeindex], result);
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
}
