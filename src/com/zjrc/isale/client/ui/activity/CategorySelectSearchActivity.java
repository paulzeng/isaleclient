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
 * 功能描述：产品类型搜索界面
 */
public class CategorySelectSearchActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private EditText et_productcategoryname;
    private Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.categoryselect_search);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.v2_search);

        et_productcategoryname = (EditText) findViewById(R.id.et_productcategoryname);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            et_productcategoryname.setText(bundle.getString("productcategoryname"));
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
    		case R.id.btn_ok:
    			intent = new Intent();
    			bundle = new Bundle();
    			bundle.putString("productcategoryname", et_productcategoryname.getText().toString());
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