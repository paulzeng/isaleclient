package com.zjrc.isale.client.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.DateUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.Date;
import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：差旅审批详情
 */
public class TravelSubmitDetailActivity extends BaseActivity {

	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private TextView tv_title;
	private TextView tv_add_time;
	private TextView tv_state;
	private TextView tv_place;
	private TextView tv_begin_date;
	private TextView tv_end_date;
	private TextView tv_travel_days;
	private TextView tv_reason;
	private TextView tv_man;
	private RelativeLayout rl_bottom;
	private Button ok;
	private Button cancel;
	private String id;
	private TextView tv_audit_man;
	private TextView tv_audit_state;
	private TextView tv_audti_result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.travel_submit_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.travel_submit_detail);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_add_time = (TextView) findViewById(R.id.tv_add_time);
		tv_state = (TextView) findViewById(R.id.tv_state);
		tv_begin_date = (TextView) findViewById(R.id.tv_begin_date);
		tv_end_date = (TextView) findViewById(R.id.tv_end_date);
		tv_place = (TextView) findViewById(R.id.tv_place);
		tv_man = (TextView) findViewById(R.id.tv_travel_man);
		tv_reason = (TextView) findViewById(R.id.tv_travel_reason);
		tv_travel_days = (TextView) findViewById(R.id.tv_travel_days);
		tv_audit_man = (TextView) findViewById(R.id.tv_audit_man);
		tv_audit_state = (TextView) findViewById(R.id.tv_sign_state);
		tv_audti_result = (TextView) findViewById(R.id.tv_result);
		ok = (Button) findViewById(R.id.btn_ok);
		cancel = (Button) findViewById(R.id.btn_cancel);
		rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		id = bundle.getString("corptravelid");
		sendQuery();
		ok.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("operate", "modify");
					bundle.putString("travelid", id);
					intent.putExtras(bundle);
					intent.setClass(TravelSubmitDetailActivity.this,
							TravelSubmitActivity.class);
					startActivity(intent);
				}
			}
		});
		cancel.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					showAlertDialog("提示", "确定删除此差旅申请吗？",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									requestDelete(id);
								}
							}, "确定", "取消", new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									alertDialog.dismiss();
								}
							});
				}
			}
		});
		addReceiver();
	}

	private RefreshEventReceiver receiver;

	private void addReceiver() {
		receiver = new RefreshEventReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.TRAVEL_DELETE_ACTION);
		filter.addAction(Constant.TRAVEL_MODIFY_ACTION);
		registerReceiver(receiver, filter);
	}

	private class RefreshEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Constant.TRAVEL_DELETE_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_MODIFY_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_AUDIT_ACTION)) {
				sendQuery();
			}
		}
	}

	private void sendQuery() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("corptravelid", XmlValueUtil.encodeString(id));
        request("travel!detail?code=" + Constant.TRAVEL_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	public void requestDelete(String id) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("operate", "delete");
            params.put("date", "");
            params.put("province", "");
            params.put("city", "");
            params.put("zone", "");
            params.put("desc", "");
            params.put("days", "");
            params.put("begindate", "");
            params.put("enddate", "");
            params.put("corptravelid", id);
            request("travel!submit?code=" + Constant.TRAVEL_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	@Override
	public void onRecvData(final XmlNode response) {
//		if (response != null) {
//			String functionno = response.getText("root.functionno");
//			if (functionno.equalsIgnoreCase(Constant.TRAVEL_QUERY)) {
//				tv_title.setText((response.getChildNodeText("province") != null ? response
//						.getChildNodeText("province") : "")
//						+ (response.getChildNodeText("city") != null ? response
//								.getChildNodeText("city") : "")
//						+ (response.getChildNodeText("zone") != null ? response
//								.getChildNodeText("zone") : ""));
//				tv_place.setText((response.getChildNodeText("province") != null ? response
//						.getChildNodeText("province") : "")
//						+ (response.getChildNodeText("city") != null ? response
//								.getChildNodeText("city") : "")
//						+ (response.getChildNodeText("zone") != null ? response
//								.getChildNodeText("zone") : ""));
//				String dateStr = response.getChildNodeText("date");
//				Date date = DateUtil.str2Date(dateStr);
//				tv_add_time.setText(DateUtil.date2Str(date, getResources()
//						.getString(R.string.year_mouth_day_name_format))
//						+ "申请");
//				tv_begin_date.setText(response.getChildNodeText("begindate"));
//				tv_end_date.setText(response.getChildNodeText("enddate"));
//				tv_travel_days.setText(response.getChildNodeText("days")
//						.replace(".0", " 天"));
//				tv_reason.setText(response.getChildNodeText("desc"));
//				String state = response.getChildNodeText("state");
//				if (state.equalsIgnoreCase("0")) { // (0为未审批，1为审批通过，2为审批未通过，3为已开始,4已结束)
//					tv_state.setText("未审批");
//				} else if (state.equalsIgnoreCase("2")) {
//					tv_state.setText("不同意");
//					rl_bottom.setVisibility(View.GONE);
//					tv_audit_man.setText(response.getChildNodeText("signuser"));
//					tv_audit_state.setText("已审批");
//					tv_audti_result.setText(response
//							.getChildNodeText("signresult"));
//				}
//				ISaleApplication application = ISaleApplication.getInstance();
//				tv_man.setText(XmlValueUtil.encodeString(application
//						.getConfig().getUsername()));
//			} else if (functionno.equalsIgnoreCase(Constant.TRAVEL_SUBMIT)) {
//				Toast.makeText(TravelSubmitDetailActivity.this, "差旅任务删除成功!",
//						Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent();
//				intent.setAction(Constant.TRAVEL_DELETE_ACTION);
//				sendBroadcast(intent);
//				finish();
//			}
//		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TRAVEL_QUERY.equals(code)) {
                JsonObject travel = response.getAsJsonObject("body");
                if (travel != null) {
                    tv_title.setText((travel.get("province") != null ? travel.get("province").getAsString() : "") + (travel.get("province") != null ? travel.get("city").getAsString() : "") + (travel.get("province") != null ? travel.get("zone").getAsString() : ""));
                    tv_place.setText((travel.get("province") != null ? travel.get("province").getAsString() : "") + (travel.get("province") != null ? travel.get("city").getAsString() : "") + (travel.get("province") != null ? travel.get("zone").getAsString() : ""));
                    String dateStr = travel.get("date").getAsString();
                    Date date = DateUtil.str2Date(dateStr);
                    tv_add_time.setText(DateUtil.date2Str(date, getResources()
                            .getString(R.string.year_mouth_day_name_format))
                            + "申请");
                    tv_begin_date.setText(travel.get("begindate").getAsString());
                    tv_end_date.setText(travel.get("enddate").getAsString());
                    tv_travel_days.setText(travel.get("days").getAsString()
                            .replace(".0", " 天"));
                    tv_reason.setText(travel.get("desc").getAsString());
                    String state = travel.get("state").getAsString();
                    if (state.equalsIgnoreCase("0")) { // (0为未审批，1为审批通过，2为审批未通过，3为已开始,4已结束)
                        tv_state.setText("未审批");
                    } else if (state.equalsIgnoreCase("2")) {
                        tv_state.setText("不同意");
                        rl_bottom.setVisibility(View.GONE);
                        tv_audit_man.setText(travel.get("signuser").getAsString());
                        tv_audit_state.setText("已审批");
                        tv_audti_result.setText(travel.get("signresult").getAsString());
                    }
                    ISaleApplication application = ISaleApplication.getInstance();
                    tv_man.setText(XmlValueUtil.encodeString(application
                            .getConfig().getUsername()));
                } else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
            }else if(Constant.TRAVEL_SUBMIT.equals(code)){
                Toast.makeText(TravelSubmitDetailActivity.this, "差旅任务删除成功!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Constant.TRAVEL_DELETE_ACTION);
                sendBroadcast(intent);
                finish();
            }
        }
    }
}
