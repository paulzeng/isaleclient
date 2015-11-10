package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：产品品牌选择界面
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
import com.zjrc.isale.client.ui.adapter.BrandSelectAdapter;
import com.zjrc.isale.client.ui.adapter.BrandSelectAdapter.BrandItem;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;

;

public class BrandSelectActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener, OnScrollListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_filter;

    private ListView lv_list;

    private BrandSelectAdapter adapter;

    private String productbrandname = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.brandselect_list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.brand_list);

        btn_titlebar_filter = (Button) findViewById(R.id.btn_titlebar_filter);
        btn_titlebar_filter.setVisibility(View.VISIBLE);
        btn_titlebar_filter.setOnClickListener(this);

        lv_list = (ListView) findViewById(R.id.lv_list);
        adapter = new BrandSelectAdapter(getLayoutInflater());
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(this);
        lv_list.setOnScrollListener(this);

        sendQueryProductBrand("", "0");
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
    			intent = new Intent(BrandSelectActivity.this, BrandSelectSearchActivity.class);
    			bundle = new Bundle();
    			bundle.putString("productbrandname", productbrandname);
    			intent.putExtras(bundle);
    			startActivityForResult(intent, Constant.RESULT_BRANDLIST_REFRESH);
    			break;
    		}
    	}
    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {    // 当不滚动时
            case OnScrollListener.SCROLL_STATE_IDLE:
                if (lv_list.getLastVisiblePosition() == (lv_list.getCount() - 1)) {// 判断滚动到底部
                    BrandItem item = (BrandItem) adapter.getItem(lv_list.getCount() - 1);
                    if (item != null) {
                        sendQueryProductBrand(item.getBrandid(), "1");
                    }
                } else if (lv_list.getFirstVisiblePosition() == 0) {// 判断滚动到顶部
                    BrandItem item = (BrandItem) adapter.getItem(0);
                    if (item != null) {
                        sendQueryProductBrand(item.getBrandid(), "0");
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.RESULT_BRANDLIST_REFRESH) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    productbrandname = bundle.getString("productbrandname");
                }
                adapter.clearItem();
                adapter.notifyDataSetChanged();
                sendQueryProductBrand("", "0");
            }
        }
    }

    public void sendQueryProductBrand(String id, String order) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("brandname", productbrandname);
            params.put("brandid", id);
            params.put("order", order);
            request("productBrand!list?code=" + Constant.BRAND_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        BrandItem item = (BrandItem) adapter.getItem(arg2);
        if (item != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("brandid", item.getBrandid());
            bundle.putString("brandname", item.getBrandname());
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onRecvData(XmlNode response) {
    }

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.BRAND_LIST.equals(code)) {
                JsonArray brands = response.getAsJsonArray("body");
                if (brands != null) {
                    for (JsonElement brandElem : brands) {
                        JsonObject brandObj = (JsonObject) brandElem;
                        adapter.addItem(brandObj.get("id").getAsString(), brandObj.get("name").getAsString());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
