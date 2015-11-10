package com.zjrc.isale.client.ui.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.xml.XmlNode;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：考勤详情
 */
public class AttenceDetailActivity extends BaseActivity {

	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;
	private TextView tv_username;
	private TextView tv_date;
	private TextView tv_time;
	private TextView tv_address;
	private TextView tv_type;
	private TextView tv_result;
	private TextView tv_title;

	private String latitude;
	private String longitude;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.attence_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_type = (TextView) findViewById(R.id.tv_type);
		tv_result = (TextView) findViewById(R.id.tv_result);

		Intent intent = getIntent();
		ISaleApplication application = (ISaleApplication) getApplication();

		if (application != null) {
			tv_username.setText(application.getConfig().getUsername());
		}
		tv_titlebar_title.setText(R.string.attence_view);
		request(intent.getStringExtra("bizworkcheckid"));

	}

	public void request(String id) {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("bizworkcheckid", id);
            request("attenceCheck!detail?code=" + Constant.ATTENCE_ITEM, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.ATTENCE_ITEM.equals(code)) {//考勤登记详情
            	JsonObject attence = response.getAsJsonObject("body");
                if(attence != null) {
                	tv_date.setText(attence.get("date").getAsString());
    				tv_time.setText(attence.get("time").getAsString());
    				latitude = attence.get("longitude").getAsString();
    				longitude = attence.get("latitude").getAsString();
    				tv_address.setText(attence.get("address").getAsString());
    				tv_title.setText(attence.get("date").getAsString() + " 考勤");
    				String type = attence.get("type").getAsString();
    				Integer typeindex = Integer.valueOf(type);
    				String result = attence.get("attenceresult").getAsString();
    				Integer resultindex = Integer.valueOf(result);
    				tv_type.setText((this.getResources()
    						.getStringArray(R.array.attencetypes))[typeindex]);
    				tv_result.setText((this.getResources()
    						.getStringArray(R.array.attenceresult))[resultindex]);
                }
            }
        }
    }
}
