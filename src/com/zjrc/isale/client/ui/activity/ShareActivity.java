package com.zjrc.isale.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.util.xml.XmlNode;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：二维码界面
 */
public class ShareActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.share);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.v2_share);
    }

    @Override
    public void onClick(View view) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		switch (view.getId()) {
    		case R.id.btn_titlebar_back:
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
