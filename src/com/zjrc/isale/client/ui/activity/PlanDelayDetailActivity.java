package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.DateUtil;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.Date;
import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：拜访取消延迟详情
 */
public class PlanDelayDetailActivity extends BaseActivity {

	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private TextView tv_title;
	private TextView tv_add_time;
	private TextView tv_state;
	private TextView tv_terminal;

	private TextView tv_date;

	private TextView tv_content;
	private TextView tv_delay_date;
	private TextView tv_type;
	private TextView tv_reason;

	private String id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.plan_delay_detail);
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

		tv_title = (TextView) findViewById(R.id.tv_title);

		tv_add_time = (TextView) findViewById(R.id.tv_add_time);
		tv_state = (TextView) findViewById(R.id.tv_state);
		tv_date = (TextView) findViewById(R.id.tv_time);
		tv_terminal = (TextView) findViewById(R.id.tv_terminal);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_delay_date = (TextView) findViewById(R.id.tv_delay_date);
		tv_type = (TextView) findViewById(R.id.tv_delay_type);
		tv_reason = (TextView) findViewById(R.id.tv_delay_reason);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		tv_titlebar_title.setText(R.string.plan_detail);
		id = bundle.getString("planid");
		sendQuery();
	}

	private void sendQuery() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("planid", id);
        request("plan!detail?code=" + Constant.PLAN_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	@Override
	public void onRecvData(final XmlNode response) {
//		if (response != null) {
//			String functionno = response.getText("root.functionno");
//            if (functionno.equalsIgnoreCase(Constant.PLAN_SUBMIT)) {
//
//				Toast.makeText(PlanDelayDetailActivity.this, "拜访任务删除成功!",
//						Toast.LENGTH_SHORT).show();
//				setResult(Constant.RESULT_PLANLIST_REFRESH);
//				finish();
//			}
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.PLAN_ADD) {
			if (resultCode == Constant.RESULT_PLAN_ADD) {
				sendQuery();
			}
		}
	}
    @Override
    public void onRecvData(JsonObject response) {

        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.PLAN_QUERY.equals(code)) {
                JsonObject plan = response.getAsJsonObject("body");

                String signState =plan.get("signstate").getAsString();
                String signResult =plan.get("signresult").getAsString() ;
                String state =plan.get("planstate").getAsString();
                tv_title.setText(plan.get("terminalname").getAsString());
                if (state.equalsIgnoreCase("2")) {
                    tv_state.setText("已推迟");
                    tv_type.setText("延迟执行");
                } else if (state.equalsIgnoreCase("3")) {
                    tv_state.setText("已取消");
                    tv_type.setText("取消执行");
                }
                tv_add_time.setText(plan.get("plandate").getAsString()
                        + " 申请");
                tv_date.setText(plan.get("plandate").getAsString());
                tv_terminal.setText(plan.get("terminalname").getAsString());
                tv_content.setText(plan.get("plancontent").getAsString());
                String dateStr =plan.get("statedate").getAsString();
                Date date = DateUtil.str2Date(dateStr);
                tv_delay_date.setText(DateUtil.date2Str(date, getResources()
                        .getString(R.string.year_mouth_day_name_format)));
                tv_reason.setText(plan.get("plancomment").getAsString());
            }
        }
    }
}
