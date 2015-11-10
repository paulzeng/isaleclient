package com.zjrc.isale.client.ui.activity;

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
import com.zjrc.isale.client.ui.adapter.TravelListAdapter;
import com.zjrc.isale.client.ui.adapter.TravelListAdapter.TravelItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：我的差旅列表
 */
public class TravelListActivity extends BaseActivity implements
		OnItemClickListener {
	private Button btn_titlebar_back;
	private Button btn_titlebar_search;

	private TextView tv_titlebar_title;

	private PullToRefreshListView lv_list;

	private ListView actualListView;

	private Button btn_add;

	private TravelListAdapter adapter;
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
		setContentView(R.layout.travel_list);
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

		tv_titlebar_title.setText(R.string.travel_list);

		btn_titlebar_search = (Button) findViewById(R.id.btn_titlebar_search);

		btn_titlebar_search.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					intent.setClass(TravelListActivity.this,
							TravelSearchActivity.class);
					intent.putExtra("key", key);
					startActivityForResult(intent, Constant.TRAVEL_SEARCH);
				}
			}
		});

		lv_list = (PullToRefreshListView) findViewById(R.id.lv_travellist);
		actualListView = lv_list.getRefreshableView();
		adapter = new TravelListAdapter(this, getLayoutInflater(), handler);
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);

		lv_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (actualListView.getFirstVisiblePosition() == 0) {
					TravelItem item = (TravelItem) adapter.getItem(0);
					if (item != null) {
		                id= "";
						request(id, "0", false);
					}
				} else if (actualListView.getLastVisiblePosition() == (adapter
						.getCount() + 1)) {
					TravelItem item = (TravelItem) adapter.getItem(adapter
							.getCount() - 1);
					if (item != null) {
						id= item.getId();
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
					intent.setClass(TravelListActivity.this,
							TravelSubmitActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("operate", "insert");
					bundle.putString("travelid", "");
					intent.putExtras(bundle);
					startActivityForResult(intent, Constant.TRAVEL_ADD);
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
		filter.addAction(Constant.TRAVEL_ADD_ACTION);
		filter.addAction(Constant.TRAVEL_ARRIVE_ACTION);
		filter.addAction(Constant.TRAVEL_DELETE_ACTION);
		filter.addAction(Constant.TRAVEL_LEAVE_ACTION);
		filter.addAction(Constant.TRAVEL_START_ACTION);
		filter.addAction(Constant.TRAVEL_MODIFY_ACTION);
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
		if (requestCode == Constant.TRAVEL_SEARCH) {
			if (resultCode == Constant.RESULT_TRAVEL_SEARCH) {
				key = data.getStringExtra("key");
				id="";
				request(id, "0", true);
			}
		}
	}

	private String key = "";

	public void request(String id, String order, boolean needProgress) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            params.put("city", key);
            params.put("date","");
            params.put("corptravelid", id);
            params.put("order", order);
            request("travel!list?code=" + Constant.TRAVEL_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	@Override
	public void onRecvData(XmlNode response) {
//		if (response != null) {
//			String functionno = response.getText("root.functionno");
//			if (functionno.equalsIgnoreCase(Constant.TRAVEL_LIST)) {
//				XmlNode noderecords = response.getChildNode("records");
//				if (noderecords != null) {
//					ArrayList<XmlNode> records = noderecords
//							.getChildNodeSet("record");
//					if (id!=null&&id.equalsIgnoreCase("")) {
//						adapter.clearItem();
//					}
//					if (records != null) {
//						for (XmlNode record : records) {
//							adapter.addItem(record.getChildNodeText("id"),
//									record.getChildNodeText("province"),
//									record.getChildNodeText("city"),
//									record.getChildNodeText("zone"),
//									record.getChildNodeText("begindate"),
//									record.getChildNodeText("enddate"),
//									record.getChildNodeText("state"),
//									record.getChildNodeText("arrivetraceid"),
//									record.getChildNodeText("leavetraceid"),
//									record.getChildNodeText("actualbegindate"),
//									record.getChildNodeText("actualenddate")
//
//							);
//
//						}
//						adapter.notifyDataSetChanged();
//					}
//				}
//				if (adapter.getCount() <= 0) {
//					listview_foreGround.setVisibility(View.VISIBLE);
//					lv_list.setVisibility(View.GONE);
//				} else {
//					listview_foreGround.setVisibility(View.GONE);
//					lv_list.setVisibility(View.VISIBLE);
//				}
//				lv_list.onRefreshComplete();
//			}
//		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TravelItem item = (TravelItem) adapter.getItem(arg2 - 1);
		Intent intent = new Intent();
		intent.putExtra("corptravelid", item.getId());
		Bundle bundle = new Bundle();
		String state = item.getState();
		if (state.equalsIgnoreCase("0")) { // (0为未审批，1为审批通过，2为审批未通过，3为已开始,4已结束)
			intent.setClass(TravelListActivity.this,
					TravelSubmitDetailActivity.class);
			startActivityForResult(intent, Constant.TRAVEL_SUBMIT_DETAIL);
		} else if (state.equalsIgnoreCase("1")) {
			intent.setClass(TravelListActivity.this,
					TravelExecutionActivity.class);
			bundle.putString("state", "unstart");
			bundle.putString("corptravelid", item.getId());
			intent.putExtras(bundle);
			startActivityForResult(intent, Constant.TRAVEL_EXECUTION);
		} else if (state.equalsIgnoreCase("2")) {
			intent.setClass(TravelListActivity.this,
					TravelSubmitDetailActivity.class);
			startActivityForResult(intent, Constant.TRAVEL_SUBMIT_DETAIL);
		} else if (state.equalsIgnoreCase("3") || state.equalsIgnoreCase("4")) {// 已到达/已开始
			intent.setClass(TravelListActivity.this,
					TravelExecutionActivity.class);
			bundle.putString("state", "started");
			bundle.putString("corptravelid", item.getId());
			intent.putExtras(bundle);
			startActivityForResult(intent, Constant.TRAVEL_EXECUTION);
		} else {
			intent.setClass(TravelListActivity.this, TravelDetailActivity.class);
			bundle.putString("corptravelid", item.getId());
			intent.putExtras(bundle);
			startActivityForResult(intent, Constant.TRAVEL_EXECUTION);
		}
	}

	private class RefreshEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Constant.TRAVEL_ADD_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_ARRIVE_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_DELETE_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_LEAVE_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_MODIFY_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_START_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_AUDIT_ACTION)) {
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

			Toast.makeText(this, getString(R.string.v2_clearfilter),
					Toast.LENGTH_SHORT).show();
		} else {
			super.onBackPressed();
		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TRAVEL_LIST.equals(code)) {
                JsonArray travels = response.getAsJsonArray("body");
                if (travels != null) {
                    if (id!=null&&id.equalsIgnoreCase("")) {
                            adapter.clearItem();
                    }
                    for (JsonElement travelElem : travels) {
                        JsonObject travelObj = (JsonObject) travelElem;
                        adapter.addItem(travelObj.get("id").getAsString(),
                                travelObj.get("province").getAsString(),
                                travelObj.get("city").getAsString(),
                                travelObj.get("zone").getAsString(),
                                travelObj.get("begindate").getAsString(),
                                travelObj.get("enddate").getAsString(),
                                travelObj.get("state").getAsString(),
                                travelObj.get("arrivetraceid").getAsString(),
                                travelObj.get("leavetraceid").getAsString(),
                                travelObj.get("actualbegindate").getAsString(),
                                travelObj.get("actualenddate").getAsString()
                        );
                    }
                    adapter.notifyDataSetChanged();
                    if (adapter.getCount() <= 0) {
                        listview_foreGround.setVisibility(View.VISIBLE);
                        lv_list.setVisibility(View.GONE);
                    } else {
                        listview_foreGround.setVisibility(View.GONE);
                        lv_list.setVisibility(View.VISIBLE);
                    }
                }
                lv_list.onRefreshComplete();
            } else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
