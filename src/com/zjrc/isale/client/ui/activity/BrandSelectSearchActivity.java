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
 * 功能描述：产品品牌搜索界面
 */
public class BrandSelectSearchActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private EditText et_productbrandname;
    private Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.brandselect_search);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.v2_search);

        et_productbrandname = (EditText) findViewById(R.id.et_productbrandname);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            et_productbrandname.setText(bundle.getString("productbrandname"));
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
    			bundle.putString("productbrandname", et_productbrandname.getText().toString());
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
