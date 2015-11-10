package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
import com.zjrc.isale.client.ui.adapter.TerminalProductAdapter;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：进店品项列表界面
 */

public class TerminalProductActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, View.OnClickListener {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private Button btn_titlebar_filter;
	private Button btn_titlebar_add;

	private PullToRefreshListView lv_list;

	private ListView actualListView;
	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;

	private TerminalProductAdapter adapter;

	private String terminalid;
	private String terminalcode;
	private String terminalname;
	private String intoterminaldate;
    private String id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.terminalproduct_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);

		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.datasubmit_terminalproduct);

		btn_titlebar_filter = (Button) findViewById(R.id.btn_titlebar_filter);
		btn_titlebar_filter.setVisibility(View.VISIBLE);
		btn_titlebar_filter.setOnClickListener(this);

		btn_titlebar_add = (Button) findViewById(R.id.btn_titlebar_add);
		btn_titlebar_add.setVisibility(View.VISIBLE);
		btn_titlebar_add.setOnClickListener(this);

		adapter = new TerminalProductAdapter(getLayoutInflater());
		lv_list = (PullToRefreshListView) findViewById(R.id.lv_list);
		actualListView = lv_list.getRefreshableView();
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);

		lv_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (actualListView.getFirstVisiblePosition() == 0) {
					TerminalProductAdapter.TerminalProductItem item = (TerminalProductAdapter.TerminalProductItem) adapter.getItem(0);
					if (item != null) {
                        id = "";
						sendQueryTerminalProduct("", "0", false);
					}
				} else if (actualListView.getLastVisiblePosition() == adapter.getCount() + 1) {
					TerminalProductAdapter.TerminalProductItem item = (TerminalProductAdapter.TerminalProductItem) adapter.getItem(actualListView.getLastVisiblePosition() - 2);
					if (item != null) {
                        id = item.getTerminalid();
						sendQueryTerminalProduct(id, "1", false);
					}

				}
			}
		});
		listview_foreGround = findViewById(R.id.ll_listview_foreground);
		tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
		iv_icon = (ImageView) listview_foreGround.findViewById(R.id.iv_icon);
		sendQueryTerminalProduct(id, "0", true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TerminalProductAdapter.TerminalProductItem item = (TerminalProductAdapter.TerminalProductItem) adapter
				.getItem(position - 1);
		if (item != null) {
			Intent intent = new Intent(TerminalProductActivity.this,
					TerminalProductDetailActivity.class);
			intent.putExtra("terminalid", item.getTerminalid());
			intent.putExtra("terminalname", item.getTerminalname());
			startActivityForResult(intent, Constant.TERMINALPRODUCTLIST_REFRESH);
		}
	}

	@Override
	public void onClick(View view) {
		if (CommonUtils.isNotFastDoubleClick()) {
			switch (view.getId()) {
			case R.id.btn_titlebar_filter:
				Intent intent = new Intent(TerminalProductActivity.this,
						TerminalProductSearchActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("terminalid", terminalid);
				bundle.putString("terminalcode", terminalcode);
				bundle.putString("terminalname", terminalname);
				bundle.putString("intoterminaldate", intoterminaldate);
				intent.putExtras(bundle);
				startActivityForResult(intent, Constant.TERMINALPRODUCTLIST_REFRESH);
				break;
			case R.id.btn_titlebar_add:
				startActivityForResult(new Intent(TerminalProductActivity.this,
						TerminalProductSubmit.class),
						Constant.TERMINALPRODUCTLIST_REFRESH);
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.TERMINALPRODUCTLIST_REFRESH) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Bundle bundle = data.getExtras();
					terminalid = bundle.getString("terminalid");
					terminalcode = bundle.getString("terminalcode");
					terminalname = bundle.getString("terminalname");
					intoterminaldate = bundle.getString("intoterminaldate");
				}
                id = "";
				sendQueryTerminalProduct(id, "0", true);
			}
		}
	}

	private void sendQueryTerminalProduct(String id, String order,
			boolean needProgress) {
        ISaleApplication application = (ISaleApplication)getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("terminalid", XmlValueUtil.encodeString(terminalid));
            params.put("terminalcode", XmlValueUtil.encodeString(terminalcode));
            params.put("terminalname", XmlValueUtil.encodeString(terminalname));
            params.put("intoterminaldate",XmlValueUtil.encodeString(intoterminaldate));
            params.put("id",XmlValueUtil.encodeString(id));
            params.put("order",XmlValueUtil.encodeString(order));
            request("terminalproduct!newlist?code=" + Constant.TERMINALPRODUCT_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }

    }

	@Override
	public void onRecvData(XmlNode response) {

	}

    @Override
    public void onBackPressed() {
        if (!isEmpty(terminalid, terminalcode, terminalname, intoterminaldate)) {
            terminalid = "";
            terminalcode = "";
            terminalname = "";
            intoterminaldate = "";

            adapter.clearItem();
            adapter.notifyDataSetChanged();
            id = "";
            sendQueryTerminalProduct(id, "0", true);

            Toast.makeText(this, getString(R.string.v2_clearfilter), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TERMINALPRODUCT_LIST.equals(code)) {
                JsonArray terminalProducts = response.getAsJsonArray("body");
                if(terminalProducts!=null){
                    if (TextUtils.isEmpty(id)) {
                        adapter.clearItem();
                    }
                    for(JsonElement productElem :terminalProducts){
                        JsonObject productObj = (JsonObject) productElem;
                        JsonArray products=productObj.getAsJsonArray("products");
                        List<TerminalProductAdapter.ProductItem> list = new ArrayList<TerminalProductAdapter.ProductItem>();
                        for (JsonElement product : products) {
                            JsonObject jsonProduct  = (JsonObject) product;
                            list.add(adapter.createProduct(jsonProduct.get("bizprodtrmnlrelid").getAsString()
                                   ,jsonProduct.get("productname").getAsString()
                                  ,jsonProduct.get("intoterminaldate").getAsString()
                                   ));
                        }
                        adapter.addItem(productObj.get("terminalid").getAsString(),
                                productObj.get("terminalname").getAsString(),
                                Integer.parseInt(productObj.get("productsize").getAsString()), list);
                    }
                        adapter.notifyDataSetChanged();
                }
                if (adapter.getCount() <= 0) {
                    listview_foreGround.setVisibility(View.VISIBLE);
                    lv_list.setVisibility(View.GONE);
                    tv_msg.setText("暂无进店品项信息~");
                } else {
                    listview_foreGround.setVisibility(View.GONE);
                    lv_list.setVisibility(View.VISIBLE);
                }
                lv_list.onRefreshComplete();
            }
        }
    }
}
