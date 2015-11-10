package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.zxing.activity.CaptureActivity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：网点产品搜索界面
 */
public class TerminalProductSelectSearchActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_barcode;

    private EditText et_productcode;
    private TextView tv_productbrand;
    private TextView tv_productcategory;
    private EditText et_productname;
    private Button btn_ok;

    private String productcode;
    private String productname;

    private String brandid;
    private String categoryid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.terminalproductselect_search);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.v2_search);

        btn_titlebar_barcode = (Button) findViewById(R.id.btn_titlebar_barcode);
//        btn_titlebar_barcode.setVisibility(View.VISIBLE);
        btn_titlebar_barcode.setOnClickListener(this);

        et_productcode = (EditText) findViewById(R.id.et_productcode);

        tv_productbrand = (TextView) findViewById(R.id.tv_productbrand);
        tv_productbrand.setOnClickListener(this);

        tv_productcategory = (TextView) findViewById(R.id.tv_productcategory);
        tv_productcategory.setOnClickListener(this);

        et_productname = (EditText) findViewById(R.id.et_productname);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productcode = bundle.getString("productcode");
            et_productcode.setText(productcode);
            tv_productbrand.setText(bundle.getString("productbrand"));
            tv_productcategory.setText(bundle.getString("productcategory"));
            productname = bundle.getString("productname");
            et_productname.setText(productname);
            brandid = bundle.getString("brandid");
            categoryid = bundle.getString("categoryid");
        }
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
    		case R.id.btn_titlebar_barcode:
    			intent = new Intent(TerminalProductSelectSearchActivity.this, CaptureActivity.class);
    			startActivityForResult(intent, Constant.BARCODE_SCAN);
    			break;
    		case R.id.tv_productbrand:
    			intent = new Intent(TerminalProductSelectSearchActivity.this, BrandSelectActivity.class);
    			startActivityForResult(intent, Constant.RESULT_BRAND_SELECT);
    			break;
    		case R.id.tv_productcategory:
    			intent = new Intent(TerminalProductSelectSearchActivity.this, CategorySelectActivity.class);
    			startActivityForResult(intent, Constant.RESULT_CATEGORY_SELECT);
    			break;
    		case R.id.btn_ok:
    			intent = new Intent();
    			bundle = new Bundle();
    			bundle.putString("productcode", et_productcode.getText().toString());
    			bundle.putString("productbrand", tv_productbrand.getText().toString());
    			bundle.putString("productcategory", tv_productcategory.getText().toString());
    			bundle.putString("productname", et_productname.getText().toString());
    			bundle.putString("brandid", brandid);
    			bundle.putString("categoryid", categoryid);
    			intent.putExtras(bundle);
    			setResult(RESULT_OK, intent);
    			finish();
    			break;
    		}
    	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.BARCODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                if (scanResult != null) {
                    productcode = scanResult;
                    et_productcode.setText(productcode);
//                    sendQueryProductDetail();
                }
            }
        } else if (requestCode == Constant.RESULT_BRAND_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                brandid = bundle.getString("brandid");
                tv_productbrand.setText(bundle.getString("brandname"));
            }
        } else if (requestCode == Constant.RESULT_CATEGORY_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                categoryid = bundle.getString("categoryid");
                tv_productcategory.setText(bundle.getString("categoryname"));
            }
        }
    }

    @Override
    public void onRecvData(XmlNode response) {

    }
    @Override
    public void onRecvData(JsonObject response) {

    }
}
