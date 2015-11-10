package com.zjrc.isale.client.ui.activity;

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
import com.zjrc.isale.client.util.xml.XmlNode;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：竞品搜索界面
 */
public class ContendProductSelectSearchActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private EditText et_productname;
    private EditText et_productbrand;
    private Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.contendproductselect_search);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.v2_search);

        et_productname = (EditText) findViewById(R.id.et_productname);

        et_productbrand = (EditText) findViewById(R.id.et_productbrand);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            et_productbrand.setText("");
            et_productname.setText("");
        }
    }

    @Override
    public void onClick(View view) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		switch (view.getId()) {
    		case R.id.btn_titlebar_back:
    			finish();
    			break;
    		case R.id.btn_ok:
    			Intent intent = new Intent();
    			Bundle bundle = new Bundle();
    			bundle.putString("productname", et_productname.getText().toString());
    			bundle.putString("productbrand", et_productbrand.getText().toString());
    			intent.putExtras(bundle);
    			setResult(RESULT_OK, intent);
    			finish();
    			break;
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
