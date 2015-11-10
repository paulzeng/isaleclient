package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：产品类别选择界面
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
import com.zjrc.isale.client.ui.adapter.CategorySelectAdapter;
import com.zjrc.isale.client.ui.adapter.CategorySelectAdapter.CategoryItem;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;

;

public class CategorySelectActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener, OnScrollListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_filter;

    private ListView lv_list;

    private CategorySelectAdapter adapter;

    private String productcategoryname = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.categoryselect_list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.category_list);

        btn_titlebar_filter = (Button) findViewById(R.id.btn_titlebar_filter);
        btn_titlebar_filter.setVisibility(View.VISIBLE);
        btn_titlebar_filter.setOnClickListener(this);

        lv_list = (ListView) findViewById(R.id.lv_list);
        adapter = new CategorySelectAdapter(getLayoutInflater());
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(this);
        lv_list.setOnScrollListener(this);

        sendQueryProductCategory("", "0");
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
    			intent = new Intent(CategorySelectActivity.this, CategorySelectSearchActivity.class);
    			bundle = new Bundle();
    			bundle.putString("productcategoryname", productcategoryname);
    			intent.putExtras(bundle);
    			startActivityForResult(intent, Constant.RESULT_CATEGORYLIST_REFRESH);
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
                    CategoryItem item = (CategoryItem) adapter.getItem(lv_list.getCount() - 1);
                    if (item != null) {
                        sendQueryProductCategory(item.getCategoryid(), "1");
                    }
                } else if (lv_list.getFirstVisiblePosition() == 0) {// 判断滚动到顶部
                    CategoryItem item = (CategoryItem) adapter.getItem(0);
                    if (item != null) {
                        sendQueryProductCategory(item.getCategoryid(), "0");
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.RESULT_CATEGORYLIST_REFRESH) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    productcategoryname = bundle.getString("productcategoryname");
                }
                adapter.clearItem();
                adapter.notifyDataSetChanged();
                sendQueryProductCategory("", "0");
            }
        }
    }

    public void sendQueryProductCategory(String id, String order) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("categoryname", productcategoryname);
            params.put("categoryid", id);
            params.put("order", order);
            request("productCategory!list?code=" + Constant.CATEGORY_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        CategoryItem item = (CategoryItem) adapter.getItem(arg2);
        if (item != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("categoryid", item.getCategoryid());
            bundle.putString("categoryname", item.getCategoryname());
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
            if (Constant.CATEGORY_LIST.equals(code)) {
                JsonArray categories = response.getAsJsonArray("body");
                if (categories != null) {
                    for (JsonElement categoryElem : categories) {
                        JsonObject categoryObj = (JsonObject) categoryElem;
                        adapter.addItem(categoryObj.get("id").getAsString(), categoryObj.get("name").getAsString());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
