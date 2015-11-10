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
 * 功能描述：销量信息详情界面
 */
public class SaleDetailActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private TextView tv_terminalname;
    private TextView tv_saledate;
    private ListView lv_product;
    private Button btn_modify;
    private Button btn_delete;
    private TextView tv_saletotalprice;

    private ProductListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.sale_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.sale_detail);

        tv_terminalname = (TextView) findViewById(R.id.tv_terminalname);

        tv_saledate = (TextView) findViewById(R.id.tv_saledate);

        lv_product = (ListView) findViewById(R.id.lv_product);
        adapter = new ProductListAdapter(getLayoutInflater());
        lv_product.setAdapter(adapter);

        btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(this);

        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

        tv_saletotalprice= (TextView) findViewById(R.id.tv_saletotalprice);

        String saleid = getIntent().getExtras().getString("saleid");
        sendQuerySaleDetail(saleid);
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

    private void sendQuerySaleDetail(String id) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("saleid", XmlValueUtil.encodeString(id));
            request("corpsale!detail?code=" + Constant.SALE_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {

    }
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.SALE_DETAIL.equals(code)) {
                JsonObject sales_detail = response.getAsJsonObject("body");
                JsonArray accrods = sales_detail.getAsJsonArray("records");
                tv_terminalname.setText(sales_detail.get("terminalname").getAsString());
                tv_saledate.setText(sales_detail.get("date").getAsString());
                if (accrods != null) {
                    for (JsonElement detail : accrods) {
                        JsonObject detailObj = (JsonObject) detail;
                        adapter.addItem(detailObj.get("productid").getAsString(),
                                detailObj.get("productname").getAsString(),
                                "￥ "+detailObj.get("saleprice").getAsString(),
                                detailObj.get("brandname").getAsString(),
                                "x "+ detailObj.get("salenum").getAsString()
                               );
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
             tv_saletotalprice.setText(sales_detail.get("totalprice").getAsString());
            }
        }
    }
}
