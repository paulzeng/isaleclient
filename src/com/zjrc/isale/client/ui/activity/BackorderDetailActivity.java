package com.zjrc.isale.client.ui.activity;

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

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：退货信息详情界面
 */

public class BackorderDetailActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private TextView tv_terminalname;
    private TextView tv_date;
    private TextView tv_state;
    private TextView tv_backorderman;
    private TextView tv_backorderphone;
    private TextView tv_backorderreason;
    private ListView lv_product;
    private Button btn_modify;
    private Button btn_delete;
    private TextView tv_saletotalprice;

    private ProductListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.backorder_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.backorder_detail);

        tv_terminalname = (TextView) findViewById(R.id.tv_terminalname);

        tv_date = (TextView) findViewById(R.id.tv_date);

        tv_state = (TextView) findViewById(R.id.tv_state);

        tv_backorderman = (TextView) findViewById(R.id.tv_backorderman);

        tv_backorderphone = (TextView) findViewById(R.id.tv_backorderphone);

        tv_backorderreason = (TextView) findViewById(R.id.tv_backorderreason);

        lv_product = (ListView) findViewById(R.id.lv_product);
        adapter = new ProductListAdapter(getLayoutInflater());
        lv_product.setAdapter(adapter);

        btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(this);

        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

        tv_saletotalprice = (TextView) findViewById(R.id.tv_saletotalprice);

        Bundle bundle = getIntent().getExtras();
        String backorderid = bundle.getString("backorderid");
        sendQueryBackorderDetail(backorderid);
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

    private void sendQueryBackorderDetail(String id) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("backorderid", XmlValueUtil.encodeString(id));
            request("corpbackorder!detail?code=" + Constant.BACKORDER_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {

    }

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.BACKORDER_DETAIL.equals(code)) {
                JsonObject order = response.getAsJsonObject("body");
                if (order != null) {
                    tv_terminalname.setText(order.get("terminalname").getAsString());
                    tv_date.setText(order.get("backorderdate").getAsString());
                    if (order.get("signstate").getAsString().equalsIgnoreCase("0")) {
                        tv_state.setText("待处理");
                    } else {
                        if (order.get("signresult").getAsString().equalsIgnoreCase("0")) {//不同意退货
                            tv_state.setText("不同意退货");
                        } else {
                            tv_state.setText("同意退货");
                        }
                    }
                    tv_backorderman.setText(order.get("backorderman").getAsString() );
                    tv_backorderphone.setText(order.get("manphone").getAsString() );
                    tv_backorderreason.setText(order.get("cause").getAsString() );
                    tv_saletotalprice.setText(order.get("totalprice").getAsString());
                    JsonArray records = order.getAsJsonArray("records");
                    if(records!=null){
                        for (JsonElement recordElem : records) {
                            JsonObject recordObj = (JsonObject) recordElem;
                            adapter.addItem(
                                    recordObj.get("productid").getAsString(),
                                    recordObj.get("productname").getAsString(),
                                    "￥ " + recordObj.get("backorderprice").getAsString(),
                                    recordObj.get("brandname").getAsString(),
                                    "x " + recordObj.get("backordernum").getAsString()
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
