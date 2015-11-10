package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：我的拜访列表界面
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
import com.zjrc.isale.client.ui.adapter.PlanListAdapter;
import com.zjrc.isale.client.ui.adapter.PlanListAdapter.PlanItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class PlanListActivity extends BaseActivity implements
		OnItemClickListener {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;
	private Button btn_titlebar_search;

	private PullToRefreshListView lv_list;

	private ListView actualListView;

	private PlanListAdapter adapter;

	private int planstatus;

	private String date = "";
	private String terminalCode = "";

	private String terminalName = "";
	private int type = 0; // ""全部,0临时,1常规 (0.全部,1临时,2常规)
	private Button btn_add;
	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;

	private String id = "";
	private Handler handler = new Handler() {
		public void handleMessage(Message smsg) {
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.plan_list);
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

		tv_titlebar_title.setText(R.string.plan_list);

		btn_titlebar_search = (Button) findViewById(R.id.btn_titlebar_search);

		btn_titlebar_search.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					intent.setClass(PlanListActivity.this,
							PlanSearchActivity.class);
					intent.putExtra("terminalname", terminalName);
					intent.putExtra("date", date);
					intent.putExtra("type", type);
					intent.putExtra("planstatus", planstatus);
					startActivityForResult(intent, Constant.PLAN_SEARCH);
				}
			}
		});

		btn_add = (Button) findViewById(R.id.btn_titlebar_add);
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("operate", "insert");
					bundle.putString("planid", "");
					bundle.putString("terminalid", "");
					bundle.putString("terminalname", "");
					intent.putExtras(bundle);
					intent.setClass(PlanListActivity.this,
							PlanSubmitActivity.class);
					startActivityForResult(intent, Constant.PLAN_ADD);
				}
			}
		});

		lv_list = (PullToRefreshListView) findViewById(R.id.lv_planlist);
		actualListView = lv_list.getRefreshableView();
		adapter = new PlanListAdapter(this, getLayoutInflater(), handler);
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);

		lv_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (actualListView.getFirstVisiblePosition() == 0) {
					PlanItem item = (PlanItem) adapter.getItem(actualListView
							.getFirstVisiblePosition());
					if (item != null) {
						id = "";
						sendQueryPlanList(id, "0", false);
					}
				} else if (actualListView.getLastVisiblePosition() == (adapter
						.getCount() + 1)) {
					PlanItem item = (PlanItem) adapter.getItem(actualListView
							.getLastVisiblePosition() - 2);
					if (item != null) {
						id = item.getId();
						sendQueryPlanList(id, "1", false);
					}
				}

			}
		});
		listview_foreGround = findViewById(R.id.ll_listview_foreground);
		tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
		iv_icon = (ImageView) listview_foreGround.findViewById(R.id.iv_icon);
		sendQueryPlanList(id, "0", true);
		addReceiver();
	}

	private RefreshEventReceiver receiver;

	private void addReceiver() {
		receiver = new RefreshEventReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.PLAN_ADD_ACTION);
		filter.addAction(Constant.PLAN_DELAY_ACTION);
		filter.addAction(Constant.PLAN_DELETE_ACTION);
		filter.addAction(Constant.PLAN_EXECUTION_ACTION);
		filter.addAction(Constant.PLAN_MODIFY_ACTION);
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
		if (requestCode == Constant.BARCODE_SCAN) {
			// if (resultCode == Activity.RESULT_OK) {
			// Bundle bundle = data.getExtras();
			// String scanResult = bundle.getString("result");
			// if (scanResult != null) {
			// et_terminalcode.setText(scanResult);
			// }
			// }
		} else if (resultCode == Constant.RESULT_PLAN_SEARCH) {
			terminalName = data.getStringExtra("terminalname");
			date = data.getStringExtra("date");
			type = data.getIntExtra("type", 0);
			planstatus = data.getIntExtra("planstatus", 0);
			id = "";
			sendQueryPlanList(id, "0", true);
		}
	}

	public void sendQueryPlanList(String id, String order, boolean needProgress) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("terminalcode", XmlValueUtil.encodeString(terminalCode));
            params.put("terminalname", XmlValueUtil.encodeString(terminalName));
            params.put("date",  XmlValueUtil.encodeString(date) );
            params.put("planstatus", planstatus + "");
            params.put("plantype",  ((((type - 1) == -1) ? "" : (type - 1)))+"");
            params.put("corpmonthplanid",XmlValueUtil.encodeString(id));
            params.put("order",XmlValueUtil.encodeString(order));
            request("plan!list?code=" + Constant.PLAN_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	@Override
	public void onRecvData(XmlNode response) {
//		if (response != null) {
//			String functionno = response.getText("root.functionno");
//			if (functionno.equalsIgnoreCase(Constant.PLAN_LIST)) {
//				XmlNode noderecords = response.getChildNode("records");
//				if (noderecords != null) {
//					ArrayList<XmlNode> records = noderecords
//							.getChildNodeSet("record");
//					if (id != null && id.equalsIgnoreCase("")) {
//						adapter.clearItem();
//					}
//					if (records != null && records.size() > 0) {
//						for (XmlNode record : records) {
//							adapter.addItem(record.getChildNodeText("id"),
//									record.getChildNodeText("terminalname"),
//									record.getChildNodeText("plandate"),
//									record.getChildNodeText("signstate"),
//									record.getChildNodeText("signresult"),
//									record.getChildNodeText("planstate"),
//									record.getChildNodeText("patrolid"),
//									record.getChildNodeText("plantype"));
//						}
//						adapter.notifyDataSetChanged();
//					} else {
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
//            else if (functionno.equalsIgnoreCase(Constant.PLAN_SUBMIT)) {
//				Toast.makeText(this, "拜访计划删除成功!", Toast.LENGTH_SHORT).show();
//				adapter.clearItem();
//				sendQueryPlanList("", "0", true);
//			}
//		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		PlanItem item = (PlanItem) adapter.getItem(arg2 - 1);
		Bundle bundle = new Bundle();

		String signState = item.getSignstate();
		String signResult = item.getSignresult();
		String state = item.getPlanstate();
		String plantype = item.getPlantype();
		Intent intent = new Intent();

		if (plantype.equalsIgnoreCase("0")) {// 临时任务
			if (state.equalsIgnoreCase("0")) {// 未执行
				intent.putExtras(bundle);
				intent.setClass(PlanListActivity.this,
						PlanSubmitDetailActivity.class);
			} else if (state.equalsIgnoreCase("1")) {// 已执行
				intent.setClass(PlanListActivity.this, PlanDetailActivity.class);
				bundle.putString("planid", item.getId());
				bundle.putString("patrolid", item.getPatrolid());
				intent.putExtras(bundle);
			} else if (state.equalsIgnoreCase("2")) {// 已推迟
				intent.setClass(PlanListActivity.this,
						PlanDelayDetailActivity.class);
			} else if (state.equalsIgnoreCase("3")) {// 已取消
				intent.setClass(PlanListActivity.this,
						PlanDelayDetailActivity.class);
			}
		} else {
			if (signState.equalsIgnoreCase("0")) {// 未审核
				intent.putExtras(bundle);
				intent.setClass(PlanListActivity.this,
						PlanSubmitDetailActivity.class);
			} else if (signState.equalsIgnoreCase("1")) {// 已审核
				intent.putExtras(bundle);
				intent.setClass(PlanListActivity.this,
						PlanSubmitDetailActivity.class);
				if (signResult.equalsIgnoreCase("1")) {// 审核通过
					intent.putExtras(bundle);
					intent.setClass(PlanListActivity.this,
							PlanSubmitDetailActivity.class);
					if (state.equalsIgnoreCase("0")) {// 未执行
						intent.putExtras(bundle);
						intent.setClass(PlanListActivity.this,
								PlanSubmitDetailActivity.class);
					} else if (state.equalsIgnoreCase("1")) {// 已执行
						intent.setClass(PlanListActivity.this,
								PlanDetailActivity.class);
						bundle.putString("planid", item.getId());
						bundle.putString("patrolid", item.getPatrolid());
						intent.putExtras(bundle);
					} else if (state.equalsIgnoreCase("2")) {// 已推迟
						intent.setClass(PlanListActivity.this,
								PlanDelayDetailActivity.class);
					} else if (state.equalsIgnoreCase("3")) {// 已取消
						intent.setClass(PlanListActivity.this,
								PlanDelayDetailActivity.class);
					}
				} else {// 审核未通过
					intent.putExtras(bundle);
					intent.setClass(PlanListActivity.this,
							PlanSubmitDetailActivity.class);
				}
			}
		}
		intent.putExtra("planid", item.getId());
		this.startActivityForResult(intent, Constant.PLANLIST_REFRESH);
	}

	private class RefreshEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Constant.PLAN_ADD_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_DELAY_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_DELETE_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_EXECUTION_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_MODIFY_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_AUDIT_ACTION)) {
				adapter.clearItem();
				adapter.notifyDataSetChanged();
				sendQueryPlanList("", "0", true);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (!TextUtils.isEmpty(terminalName) || !TextUtils.isEmpty(date)
				|| type != 0 || planstatus != 0) {
			terminalName = "";
			date = "";
			type = 0;
			planstatus = 0;
			id = "";
			sendQueryPlanList(id, "0", true);
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
            if (Constant.PLAN_LIST.equals(code)) {
                if (id != null && id.equalsIgnoreCase("")) {
                    adapter.clearItem();
                }
                JsonArray brands = response.getAsJsonArray("body");
                if (brands != null) {
                    for (JsonElement brandElem : brands) {
                        JsonObject planObj = (JsonObject) brandElem;
                        adapter.addItem(planObj.get("id").getAsString(),
                                planObj.get("terminalname").getAsString(),
                                planObj.get("plandate").getAsString(),
                                planObj.get("signstate").getAsString(),
                                planObj.get("signresult").getAsString(),
                                planObj.get("planstate").getAsString(),
                                planObj.get("patrolid").getAsString(),
                                planObj.get("plantype").getAsString());
                    }
                    adapter.notifyDataSetChanged();
                    if (adapter.getCount() <= 0) {
                        listview_foreGround.setVisibility(View.VISIBLE);
                        lv_list.setVisibility(View.GONE);
                    } else {
                        listview_foreGround.setVisibility(View.GONE);
                        lv_list.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
                lv_list.onRefreshComplete();
            }
        }
    }
}
