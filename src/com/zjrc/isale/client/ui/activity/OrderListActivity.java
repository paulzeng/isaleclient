package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：订货信息列表界面
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.zjrc.isale.client.ui.adapter.OrderListAdapter;
import com.zjrc.isale.client.ui.adapter.OrderListAdapter.OrderItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.HashMap;

public class OrderListActivity extends BaseActivity implements
		OnItemClickListener, View.OnClickListener {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private Button btn_titlebar_filter;
	private Button btn_titlebar_add;

	private OrderListAdapter adapter;

	private String terminalid;
	private String terminalcode;
	private String terminalname;
	private String orderdate;
	private String orderstatus;
	private String orderstatus_index;
	private PullToRefreshListView lv_list;

	private ListView actualListView;
	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;
	private String id="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.order_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);

		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(this);

		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.order_list);

		btn_titlebar_filter = (Button) findViewById(R.id.btn_titlebar_filter);
		btn_titlebar_filter.setVisibility(View.VISIBLE);
		btn_titlebar_filter.setOnClickListener(this);

		btn_titlebar_add = (Button) findViewById(R.id.btn_titlebar_add);
		btn_titlebar_add.setVisibility(View.VISIBLE);
		btn_titlebar_add.setOnClickListener(this);

		adapter = new OrderListAdapter(getLayoutInflater());
		lv_list = (PullToRefreshListView) findViewById(R.id.lv_list);
		actualListView = lv_list.getRefreshableView();
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);

		lv_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (actualListView.getFirstVisiblePosition() == 0) {
					OrderItem item = (OrderItem) adapter.getItem(0);
					if (item != null) {
						id= "";
						sendQueryOrderList(id, "0", false);
					}
				} else if (actualListView.getLastVisiblePosition() == adapter
						.getCount() + 1) {
					OrderItem item = (OrderItem) adapter.getItem(actualListView
							.getLastVisiblePosition() - 2);
					if (item != null) {
						id= item.getOrderid();
						sendQueryOrderList(id, "1", false);
					}
				}
			}
		});
		listview_foreGround = findViewById(R.id.ll_listview_foreground);
		tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
		iv_icon = (ImageView) listview_foreGround.findViewById(R.id.iv_icon);
		sendQueryOrderList(id, "0", true);
	}

	@Override
	public void onClick(View view) {
		if (CommonUtils.isNotFastDoubleClick()) {
			Intent intent;
			Bundle bundle;
			switch (view.getId()) {
			case R.id.btn_titlebar_back:
				finish();
				break;
			case R.id.btn_titlebar_filter:
				intent = new Intent(OrderListActivity.this,
						OrderSearchActivity.class);
				bundle = new Bundle();
				bundle.putString("terminalid", terminalid);
				bundle.putString("terminalcode", terminalcode);
				bundle.putString("terminalname", terminalname);
				bundle.putString("orderdate", orderdate);
				bundle.putString("orderstatus", orderstatus);
				bundle.putString("orderstatus_index", orderstatus_index);
				intent.putExtras(bundle);
				startActivityForResult(intent, Constant.ORDERLIST_REFRESH);
				break;
			case R.id.btn_titlebar_add:
				intent = new Intent();
				bundle = new Bundle();
				bundle.putString("operate", "insert");
				bundle.putString("orderid", "");
				bundle.putString("terminalid", "");
				bundle.putString("terminalname", "");
				intent.putExtras(bundle);
				intent.setClass(OrderListActivity.this, OrderSubmitActivity.class);
				startActivityForResult(intent, Constant.ORDERLIST_REFRESH);
				break;
				
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		OrderItem item = (OrderItem) adapter.getItem(arg2 - 1);
		if (item != null) {
			Intent intent = new Intent(OrderListActivity.this,
					OrderDetailActivity.class);
			intent.putExtra("orderid", item.getOrderid());
			startActivityForResult(intent, Constant.ORDERLIST_REFRESH);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.ORDERLIST_REFRESH) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Bundle bundle = data.getExtras();
					terminalid = bundle.getString("terminalid");
					terminalcode = bundle.getString("terminalcode");
					terminalname = bundle.getString("terminalname");
					orderdate = bundle.getString("orderdate");
					orderstatus = bundle.getString("orderstatus");
					orderstatus_index = bundle.getString("orderstatus_index");
				}
				id="";
				sendQueryOrderList(id, "0", true);
			}
		}
	}

	public void sendQueryOrderList(String orderid, String order,
			boolean needProgress) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("orderdate", XmlValueUtil.encodeString(orderdate));
            params.put("terminalid",XmlValueUtil.encodeString(terminalid));
            params.put("terminalcode",  XmlValueUtil.encodeString(terminalcode));
            params.put("terminalname", XmlValueUtil.encodeString(terminalname));
            params.put("orderstatus",  XmlValueUtil.encodeString(orderstatus_index));
            params.put("orderid",XmlValueUtil.encodeString(orderid));
            params.put("order",XmlValueUtil.encodeString(order));
            request("corporder!list?code=" + Constant.ORDER_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onBackPressed() {
        if (!isEmpty(terminalid, terminalcode, terminalname, orderstatus, orderstatus_index)) {
            terminalid = "";
            terminalcode = "";
            terminalname = "";
            orderstatus = "";
            orderstatus_index = "";
            id	="";
            sendQueryOrderList(id, "0", true);

            Toast.makeText(this, getString(R.string.v2_clearfilter), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.ORDER_LIST.equals(code)) {
                if (TextUtils.isEmpty(id)) {
                    adapter.clearItem();
                }
                JsonArray orders = response.getAsJsonArray("body");
                if (orders != null) {
                    for (JsonElement ordersElem : orders) {
                        JsonObject ordersObj = (JsonObject) ordersElem;
                        adapter.addItem(ordersObj.get("id").getAsString(),
                                ordersObj.get("orderno").getAsString(),
                                ordersObj.get("terminalname").getAsString(),
                                ordersObj.get("totalprice").getAsString(),
                                ordersObj.get("date").getAsString(),
                                ordersObj.get("signstate").getAsString(),
                                ordersObj.get("signresult").getAsString(),
                                ordersObj.get("accountstate").getAsString()
                        );
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
                if (adapter.getCount() <= 0) {
                    listview_foreGround.setVisibility(View.VISIBLE);
                    lv_list.setVisibility(View.GONE);
                    tv_msg.setText("暂无订单信息~");
                } else {
                    listview_foreGround.setVisibility(View.GONE);
                    lv_list.setVisibility(View.VISIBLE);
                }
                lv_list.onRefreshComplete();
            }
        }
    }
}
