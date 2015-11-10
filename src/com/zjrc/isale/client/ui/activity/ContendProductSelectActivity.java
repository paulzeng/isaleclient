package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：竞品选择界面
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.zjrc.isale.client.ui.adapter.ContendProductSelectAdapter;
import com.zjrc.isale.client.ui.adapter.ContendProductSelectAdapter.ContendproductItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;

public class ContendProductSelectActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener, PullToRefreshBase.OnRefreshListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_filter;

    private PullToRefreshListView lv_list;
    private ListView actualListView;
    private View listview_foreGround;
    private TextView tv_msg;
    private ImageView iv_icon;

    private ContendProductSelectAdapter adapter;

    private String terminalid = "";

    // 搜索项
    private String productname = "";
    private String productbrand = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.contendproductselect_list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.contendproduct_list);

        btn_titlebar_filter = (Button) findViewById(R.id.btn_titlebar_filter);
        btn_titlebar_filter.setVisibility(View.VISIBLE);
        btn_titlebar_filter.setOnClickListener(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        terminalid = bundle.getString("terminalid");

        lv_list = (PullToRefreshListView) findViewById(R.id.lv_list);
        adapter = new ContendProductSelectAdapter(getLayoutInflater());
        actualListView = lv_list.getRefreshableView();
        actualListView.setAdapter(adapter);
        actualListView.setOnItemClickListener(this);
        lv_list.setOnRefreshListener(this);

        listview_foreGround = findViewById(R.id.ll_listview_foreground);
        tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
        iv_icon = (ImageView) listview_foreGround.findViewById(R.id.iv_icon);

        sendQueryContendproduct("", "0", true);
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
    			intent = new Intent(ContendProductSelectActivity.this, ContendProductSelectSearchActivity.class);
    			bundle = new Bundle();
    			bundle.putString("productname", productname);
    			bundle.putString("productbrand", productbrand);
    			intent.putExtras(bundle);
    			startActivityForResult(intent, Constant.RESULT_PRODUCTLIST_REFRESH);
    			break;
    		}
    	}
    }

    @Override
    public void onRefresh() {
        if (actualListView.getFirstVisiblePosition() == 0) {
            ContendproductItem item = (ContendproductItem) adapter.getItem(0);
            if (item != null) {
                sendQueryContendproduct(item.getContendproductid(), "0", false);
            }
        } else if (actualListView.getLastVisiblePosition() == adapter.getCount() + 1) {
            ContendproductItem item = (ContendproductItem) adapter.getItem(actualListView.getLastVisiblePosition() - 2);
            if (item != null) {
                sendQueryContendproduct(item.getContendproductid(), "1", false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.RESULT_PRODUCTLIST_REFRESH) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    productbrand = bundle.getString("productbrand");
                    productname = bundle.getString("productname");
                }
                adapter.clearItem();
                adapter.notifyDataSetChanged();
                sendQueryContendproduct("", "0", true);
            }
        }
    }


    public void sendQueryContendproduct(String id, String order, boolean needProgress) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("userid", application.getConfig().getUserid());
            params.put("terminalid", terminalid);
            params.put("terminalcode", "");
            params.put("terminalname", "");
            params.put("brand", productbrand);
            params.put("name", productname);
            params.put("contendproductid", id);
            params.put("order", order);
            request("conproduct!list?code=" + Constant.CONTENDPRODUCT_LIST, params, needProgress ? FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL : FLAG_DEFAULT);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ContendproductItem item = (ContendproductItem) adapter.getItem(arg2 -1);
        if (item != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("contendproductid", item.getContendproductid());
            bundle.putString("contendproductname", item.getContendproductname());
            bundle.putString("contendproductbrandname", item.getContendproductbrandname());
            bundle.putString("contendproductnorm", item.getContendproductnorm());
            bundle.putString("contendproductprice", item.getContendproductprice());
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isEmpty(productname, productbrand)) {
            productname = "";
            productbrand = "";
            sendQueryContendproduct("", "0", true);

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
                    for (JsonElement elem : body) {
                        JsonObject obj = (JsonObject) elem;
                        adapter.addItem(obj.get("id").getAsString(),
                                obj.get("name").getAsString(),
                                obj.get("terminalid").getAsString(),
                                obj.get("terminalname").getAsString(),
                                obj.get("brand").getAsString(),
                                obj.get("norm").getAsString(),
                                obj.get("price").getAsString());
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
