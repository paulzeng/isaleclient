package com.zjrc.isale.client.ui.activity;

import java.util.HashMap;
import java.util.Map;

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
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.zxing.activity.CaptureActivity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：竞品信息搜索界面
 */
public class ContendProductSearchActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_barcode;

    private EditText et_terminalcode;
    private EditText et_terminalname;
    private TextView et_contendproductname;
    private Button btn_ok;

    private String terminalid = "";
    private String terminalcode = "";
    private String terminalname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.contendproduct_search);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.v2_search);

        btn_titlebar_barcode = (Button) findViewById(R.id.btn_titlebar_barcode);
        btn_titlebar_barcode.setVisibility(View.VISIBLE);
        btn_titlebar_barcode.setOnClickListener(this);

        et_terminalcode = (EditText) findViewById(R.id.et_terminalcode);

        et_terminalname = (EditText) findViewById(R.id.et_terminalname);

        et_contendproductname = (TextView) findViewById(R.id.et_contendproductname);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            terminalid = "";
            terminalcode ="";
            et_terminalcode.setText(terminalcode);
            terminalname = "";
            et_terminalname.setText(terminalname);
            et_contendproductname.setText("");
        }
    }

    @Override
    public void onClick(View view) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		switch (view.getId()) {
    		case R.id.btn_titlebar_back:
    			finish();
    			break;
    		case R.id.btn_titlebar_barcode:
    			Intent openCameraIntent = new Intent(ContendProductSearchActivity.this, CaptureActivity.class);
    			startActivityForResult(openCameraIntent, Constant.BARCODE_SCAN);
    			break;
    		case R.id.btn_ok:
    			Intent intent = new Intent();
    			Bundle bundle = new Bundle();
    			bundle.putString("terminalid", terminalid);
    			bundle.putString("terminalcode", et_terminalcode.getText().toString());
    			bundle.putString("terminalname", et_terminalname.getText().toString());
    			bundle.putString("contendproductname", et_contendproductname.getText().toString());
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
                    terminalcode = scanResult;
                    et_terminalcode.setText(terminalcode);
                    sendQueryTerminalDetail();
                }
            }
        }
    }

    private void sendQueryTerminalDetail() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null && application.getConfig() != null && application.getConfig().getUserid() != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("terminalid", "");
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("terminalcode", terminalcode);
            request("terminal!detail?code=" + Constant.TERMINAL_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {
    }
    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TERMINAL_DETAIL.equals(code)) {
            	JsonObject backorder = response.getAsJsonObject("body");
                if(backorder != null) {
                	terminalid = backorder.get("id").getAsString();
    				terminalname = backorder.get("name").getAsString();
                    et_terminalname.setText(terminalname);
                }
            }
        }
    }
}
