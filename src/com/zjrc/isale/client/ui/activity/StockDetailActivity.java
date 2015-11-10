package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 刘磊
 * 功能描述：我的库存列表界面
 */

import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.zjrc.isale.client.ui.adapter.ProductListAdapter;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.HashMap;

public class StockDetailActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private TextView tv_terminalname;
    private TextView tv_date;
    private ListView lv_product;
    private Button btn_modify;
    private Button btn_delete;
    private TextView tv_saletotalprice;

    private ProductListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.stock_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.stock_detail);

        tv_terminalname = (TextView) findViewById(R.id.tv_terminalname);

        tv_date = (TextView) findViewById(R.id.tv_date);

        lv_product = (ListView) findViewById(R.id.lv_product);
        adapter = new ProductListAdapter(getLayoutInflater());
        lv_product.setAdapter(adapter);

        btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(this);

        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

        tv_saletotalprice = (TextView) findViewById(R.id.tv_saletotalprice);

        Bundle bundle = getIntent().getExtras();
        String stockid = bundle.getString("stockid");
        sendQueryStockDetail(stockid);
    }

    @Override
    public void onClick(View view) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		switch (view.getId()) {
    		case R.id.btn_titlebar_back:
    			finish();
    			break;
    		case R.id.btn_modify:
    			break;
    		case R.id.btn_delete:
    			break;
    		}
    	}
    }

    private void sendQueryStockDetail(String id) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("stockid", XmlValueUtil.encodeString(id));
            request("corpstock!detail?code=" + Constant.STOCK_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {

    }
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.STOCK_DETAIL.equals(code)) {
                JsonObject stock = response.getAsJsonObject("body");
                if (stock != null) {
                    tv_terminalname.setText(stock.get("terminalname").getAsString());
                    tv_date.setText(stock.get("stockdate").getAsString());
                    tv_saletotalprice.setText(stock.get("totalprice").getAsString());
                    JsonArray records = stock.getAsJsonArray("records");
                    if(records!=null){
                    for (JsonElement recordElem : records) {
                        JsonObject recordObj = (JsonObject) recordElem;
                        adapter.addItem(
                                recordObj.get("productid").getAsString(),
                                recordObj.get("productname").getAsString(),
                                "￥ " + recordObj.get("stockprice").getAsString(),
                                recordObj.get("brandname").getAsString(),
                                "x " + recordObj.get("stocknum").getAsString()
                        );
                     }
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
