package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：休假详情
 */
public class VacationDetailActivity extends BaseActivity {

	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private TextView tv_title;
	private TextView tv_add_time;
	private TextView tv_state;
	private TextView tv_type;
	private TextView tv_state2;
	private TextView tv_begin;
	private TextView tv_end;
	private TextView tv_day;
	private TextView tv_reason;
	private TextView tv_man;
	private TextView tv_checktime;
	private TextView tv_result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.vacation_detail);
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
		tv_type = (TextView) findViewById(R.id.tv_type);
		tv_state2 = (TextView) findViewById(R.id.tv_state_2);
		tv_begin = (TextView) findViewById(R.id.tv_begin);
		tv_end = (TextView) findViewById(R.id.tv_end);
		tv_day = (TextView) findViewById(R.id.tv_day);
		tv_reason = (TextView) findViewById(R.id.tv_reason);
		tv_man = (TextView) findViewById(R.id.tv_man);
		tv_checktime = (TextView) findViewById(R.id.tv_checktime);
		tv_result = (TextView) findViewById(R.id.tv_result);

		if (!needAudit) {
			tv_state.setVisibility(View.GONE);
			findViewById(R.id.rl_state).setVisibility(View.GONE);
			findViewById(R.id.rl_man).setVisibility(View.GONE);
			findViewById(R.id.rl_checktime).setVisibility(View.GONE);
			findViewById(R.id.rl_result).setVisibility(View.GONE);
		}

		Intent intent = getIntent();

		tv_titlebar_title.setText(R.string.vacation_view);
		request(intent.getStringExtra("corpvacationid"));

	}

	public void request(String id) {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("corpvacationid", id);
            request("vacation!detail?code=" + Constant.VACATION_ITEM, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.VACATION_ITEM.equals(code)) {
            	JsonObject vacation = response.getAsJsonObject("body");
                if (vacation != null) {
                    String[] types = getResources().getStringArray(
                            R.array.vacationtypes);
                    String type = vacation.get("type").getAsString();
                    Integer typeindex = Integer.valueOf(type);
                    tv_title.setText("休假：" + types[typeindex]);
                    tv_add_time.setText(vacation.get("submitdate").getAsString());
                    tv_type.setText(types[typeindex]);
                    String signstate = vacation.get("signstate").getAsString();
                    // 0 未审批 1 不同意 2同意
                    Integer signstateindex = Integer.valueOf(signstate);
                    String result = "0";
                    if (signstateindex == 0)
                        result = (getResources()
                                .getStringArray(R.array.vacationstate))[signstateindex];
                    else if (signstateindex == 1) {
                        String signresult = vacation.get("signresult").getAsString();
                        Integer signresultindex = Integer.valueOf(signresult);
                        result = (getResources()
                                .getStringArray(R.array.vacationstate))[signresultindex + 1];
                    }
                    tv_state.setText(result);
                    tv_state2.setText(result);
                    tv_begin.setText(vacation.get("begindate").getAsString());
                    tv_end.setText(vacation.get("enddate").getAsString());
                    String days =vacation.get("vacationdays").getAsString();
                    tv_day.setText(days.substring(0, days.length() - 2) + "天");
                    tv_reason.setText(vacation.get("desc").getAsString());
                    String signresult = vacation.get("signcontent").getAsString();
                    if (signresult != null && !signresult.equals("")) {
                        tv_result.setText(signresult);
                    }
                    tv_man.setText(vacation.get("signuser").getAsString());
                    String signDate = vacation.get("signdate").getAsString();
                    if (signDate != null && !signDate.equalsIgnoreCase("")) {
                        String[] ds = signDate.split(" ");
                        tv_checktime.setText(ds[0]);
                    }
                } else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
