package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：网点产品选择界面
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
import com.zjrc.isale.client.ui.adapter.TerminalProductSelectAdapter;
import com.zjrc.isale.client.ui.adapter.TerminalProductSelectAdapter.ProductItem;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.HashMap;

public class TerminalProductSelectActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_filter;

    private ListView lv_list;
    private View listview_foreGround;
    private TextView tv_msg;
    private ImageView iv_icon;

    private TerminalProductSelectAdapter adapter;

    private String terminalid;

    // 搜索项
    private String productcode;
    private String productbrand;
    private String productcategory;
    private String productname;
    private String brandid;
    private String categoryid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.terminalproductselect);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.product_list);

        btn_titlebar_filter = (Button) findViewById(R.id.btn_titlebar_filter);
        btn_titlebar_filter.setVisibility(View.VISIBLE);
        btn_titlebar_filter.setOnClickListener(this);

        lv_list = (ListView) findViewById(R.id.lv_list);
        adapter = new TerminalProductSelectAdapter(getLayoutInflater());
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(this);

        listview_foreGround = findViewById(R.id.ll_listview_foreground);
        tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
        iv_icon = (ImageView) listview_foreGround.findViewById(R.id.iv_icon);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            terminalid = bundle.getString("terminalid");
        } else {
            terminalid = "";
        }

        sendQueryTerminalProduct();
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
    			intent = new Intent(TerminalProductSelectActivity.this, TerminalProductSelectSearchActivity.class);
    			bundle = new Bundle();
    			bundle.putString("productcode", productcode);
    			bundle.putString("productbrand", productbrand);
    			bundle.putString("productcategory", productcategory);
    			bundle.putString("productname", productname);
    			bundle.putString("brandid", brandid);
    			bundle.putString("categoryid", categoryid);
    			intent.putExtras(bundle);
    			startActivityForResult(intent, Constant.RESULT_PRODUCTLIST_REFRESH);
    			break;
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
                    productcode = bundle.getString("productcode");
                    productbrand = bundle.getString("productbrand");
                    productcategory = bundle.getString("productcategory");
                    productname = bundle.getString("productname");
                    brandid = bundle.getString("brandid");
                    categoryid = bundle.getString("categoryid");
                }
                adapter.clearItem();
                adapter.notifyDataSetChanged();
                sendQueryTerminalProduct();
            }
        }
    }

    /**
     * 发送查询销售网点的进店品项请求
     */
    private void sendQueryTerminalProduct() {
        ISaleApplication application = (ISaleApplication)getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid",XmlValueUtil.encodeString(application.getConfig().getUserid()));
            params.put("terminalid", XmlValueUtil.encodeString(terminalid));
            params.put("productcode",XmlValueUtil.encodeString(productcode));
            params.put("productbrand",XmlValueUtil.encodeString(brandid));
            params.put("productcategory",XmlValueUtil.encodeString(categoryid));
            params.put("productname",XmlValueUtil.encodeString(productname));

            request("terminalproduct!list?code=" + Constant.TERMINALPRODUCT_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {
        if (response != null) {
            String functionno = response.getText("root.functionno");
            if (functionno != null) {
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ProductItem item = (ProductItem) adapter.getItem(arg2);
        if (item != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("productid", item.getProductid());
            bundle.putString("productname", item.getProductname());
            bundle.putString("brandname", item.getProductbrandname());
            bundle.putString("categoryname", item.getProductcategoryname());
            bundle.putString("norm", item.getProductnorm());
            bundle.putString("price", item.getProductprice());
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TERMINALPRODUCT_QUERY.equals(code)) {
                JsonArray terminalproducts = response.getAsJsonArray("body");
                if (terminalproducts != null) {
                    for (JsonElement productElem : terminalproducts) {
                        JsonObject productObj = (JsonObject) productElem;
                        adapter.addItem(productObj.get("id").getAsString(),
                                productObj.get("name").getAsString(),
                                productObj.get("brandname").getAsString(),
                                productObj.get("categoryname").getAsString(),
                                productObj.get("norm").getAsString(),
                                productObj.get("price").getAsString());
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
                if (adapter.getCount() <= 0) {
                    listview_foreGround.setVisibility(View.VISIBLE);
                    lv_list.setVisibility(View.GONE);
                    tv_msg.setText("暂无产品信息~");
                } else {
                    listview_foreGround.setVisibility(View.GONE);
                    lv_list.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
