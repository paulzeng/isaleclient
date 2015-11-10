package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
 * 功能描述：进店品项详情界面
 */
public class TerminalProductDetailActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private TextView tv_terminalname;
    private ListView lv_product;
    private Button btn_modify;

    private ProductListAdapter adapter;

    private String terminalid;
    private String terminalname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.terminalproduct_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.terminalproduct_detail);

        tv_terminalname = (TextView) findViewById(R.id.tv_terminalname);

        lv_product = (ListView) findViewById(R.id.lv_product);
        adapter = new ProductListAdapter(getLayoutInflater());
        lv_product.setAdapter(adapter);

        btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        terminalid = bundle.getString("terminalid");
        terminalname = bundle.getString("terminalname");

        tv_terminalname.setText(terminalname);
        getTerminalProduct();
    }

    @Override
    public void onClick(View view) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		switch (view.getId()) {
    		case R.id.btn_titlebar_back:
    			finish();
    			break;
    		case R.id.btn_modify:
    			Intent intent = new Intent(TerminalProductDetailActivity.this, TerminalProductSubmit.class);
    			intent.putExtra("terminalid", terminalid);
    			intent.putExtra("terminalname", terminalname);
    			startActivityForResult(intent, Constant.TERMINALPRODUCTLIST_REFRESH);
    			break;
    		}
    	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.TERMINALPRODUCTLIST_REFRESH) {
            if (resultCode == RESULT_OK) {
                adapter.clearItem();
                getTerminalProduct();
                setResult(RESULT_OK);
            }
        }
    }

    private void getTerminalProduct() {
        ISaleApplication application = (ISaleApplication)getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid",XmlValueUtil.encodeString(application.getConfig().getUserid()));
            params.put("terminalid",XmlValueUtil.encodeString(terminalid));
            params.put("productcode","");
            params.put("productbrand","");
            params.put("productcategory","");
            params.put("productname","");
            request("terminalproduct!list?code=" + Constant.TERMINALPRODUCT_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {
        if (response != null) {
            String functionno = response.getText("root.functionno");
        }
    }
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TERMINALPRODUCT_QUERY.equals(code)) {
                JsonArray terminalproducts = response.getAsJsonArray("body");
                if (terminalproducts != null) {
                    for (JsonElement  productElem : terminalproducts) {
                        JsonObject productObj = (JsonObject) productElem;
                        adapter.addItem(
                                productObj.get("id").getAsString(),
                                productObj.get("name").getAsString(),
                                productObj.get("intodate").getAsString(),
                                productObj.get("brandname").getAsString(),
                                "单价：￥" +productObj.get("price").getAsString()
                        );
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
