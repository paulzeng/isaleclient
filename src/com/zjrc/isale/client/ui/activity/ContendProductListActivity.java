package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：竞品信息列表界面
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
import com.zjrc.isale.client.ui.adapter.ContendproductListAdapter;
import com.zjrc.isale.client.ui.adapter.ContendproductListAdapter.ContendproductItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;

public class ContendProductListActivity extends BaseActivity implements
		OnItemClickListener, View.OnClickListener {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private Button btn_titlebar_filter;
	private Button btn_titlebar_add;

	private PullToRefreshListView lv_list;

	private ListView actualListView;

	private ContendproductListAdapter adapter;

	// 搜索项
	private String terminalid = "";
	private String terminalcode = "";
	private String terminalname = "";
	private String contendproductname = "";

	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;
	private String id="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.contendproduct_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);

		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(this);

		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.contendsbproduct_list);

		btn_titlebar_filter = (Button) findViewById(R.id.btn_titlebar_filter);
		btn_titlebar_filter.setVisibility(View.VISIBLE);
		btn_titlebar_filter.setOnClickListener(this);

		btn_titlebar_add = (Button) findViewById(R.id.btn_titlebar_add);
		btn_titlebar_add.setVisibility(View.VISIBLE);
		btn_titlebar_add.setOnClickListener(this);

		adapter = new ContendproductListAdapter(getLayoutInflater());
		lv_list = (PullToRefreshListView) findViewById(R.id.lv_list);
		actualListView = lv_list.getRefreshableView();
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);

		lv_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (actualListView.getFirstVisiblePosition() == 0) {
					ContendproductItem item = (ContendproductItem) adapter
							.getItem(0);
					if (item != null) {
						id="";
						sendQueryList(id, "0",false);
					}

				} else if (actualListView.getLastVisiblePosition() == adapter
						.getCount() + 1) {
					ContendproductItem item = (ContendproductItem) adapter
							.getItem(actualListView.getLastVisiblePosition() - 2);
					if (item != null) {
						id=item.getProductid();
						sendQueryList(id, "1",false);
					}
				}
			}
		});

		listview_foreGround = findViewById(R.id.ll_listview_foreground);
		tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
		iv_icon = (ImageView) listview_foreGround.findViewById(R.id.iv_icon);
		sendQueryList(id, "0",true);
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
				intent = new Intent(ContendProductListActivity.this,
						ContendProductSearchActivity.class);
				bundle = new Bundle();
				bundle.putString("terminalid", terminalid);
				bundle.putString("terminalcode", terminalcode);
				bundle.putString("terminalname", terminalname);
				bundle.putString("contendproductname", contendproductname);
				intent.putExtras(bundle);
				startActivityForResult(intent, Constant.CONTENDPRODUCTLIST_ADD);
				break;
			case R.id.btn_titlebar_add:
				intent = new Intent();
				bundle = new Bundle();
				bundle.putString("operate", "insert");
				bundle.putString("promotionid", "");
				bundle.putString("terminalid", "");
				bundle.putString("terminalname", "");
				intent.putExtras(bundle);
				intent.setClass(ContendProductListActivity.this,
						ContendProductSubmitActivity.class);
				startActivityForResult(intent, Constant.CONTENDPRODUCTLIST_ADD);
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ContendproductItem item = (ContendproductItem) adapter
				.getItem(arg2 - 1);
		if (item != null) {
			Intent intent = new Intent(ContendProductListActivity.this,
					ContendProductDetailActivity.class);
			intent.putExtra("contendproductid", item.getProductid());
			startActivityForResult(intent, Constant.CONTENDPRODUCTLIST_ADD);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.CONTENDPRODUCTLIST_ADD) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Bundle bundle = data.getExtras();
					terminalid = bundle.getString("terminalid");
					terminalcode = bundle.getString("terminalcode");
					terminalname = bundle.getString("terminalname");
					contendproductname = bundle.getString("contendproductname");
				}
				id="";
				sendQueryList(id, "0",true);
			}
		}
	}

	public void sendQueryList(String contendproductid, String order,boolean needProgress) {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("userid", application.getConfig().getUserid());
            params.put("terminalid", terminalid);
            params.put("terminalcode", terminalcode);
            params.put("terminalname", terminalname);
            params.put("name", contendproductname);
            params.put("contendproductid", contendproductid);
            params.put("order", order);
            request("conproduct!list?code=" + Constant.CONTENDPRODUCT_LIST, params, needProgress ? FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL : FLAG_DEFAULT);
		}
	}

    @Override
    public void onBackPressed() {
        if (!isEmpty(terminalid, terminalcode, terminalname, contendproductname)) {
            terminalid = "";
            terminalcode = "";
            terminalname = "";
            contendproductname = "";
            id="";
            sendQueryList(id, "0", true);
            Toast.makeText(this, getString(R.string.v2_clearfilter), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRecvData(XmlNode response) {
    }

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.CONTENDPRODUCT_LIST.equals(code)) {
                JsonArray body = response.getAsJsonArray("body");
                if (body != null) {
                    if (TextUtils.isEmpty(id)) {
                        adapter.clearItem();
                    }
                    for (JsonElement elem : body) {
                        JsonObject obj = (JsonObject) elem;
                        adapter.addItem(obj.get("id").getAsString(),
                                obj.get("terminalname").getAsString(),
                                obj.get("name").getAsString(),
                                obj.get("brand").getAsString(),
                                obj.get("price").getAsString(),
                                obj.get("createdate").getAsString());
                    }
                    adapter.notifyDataSetChanged();
                }

                if (adapter.getCount() <= 0) {
                    listview_foreGround.setVisibility(View.VISIBLE);
                    lv_list.setVisibility(View.GONE);
                    tv_msg.setText("暂无竞品信息~");
                } else {
                    listview_foreGround.setVisibility(View.GONE);
                    lv_list.setVisibility(View.VISIBLE);
                }
                lv_list.onRefreshComplete();
            }
        }
    }
}
