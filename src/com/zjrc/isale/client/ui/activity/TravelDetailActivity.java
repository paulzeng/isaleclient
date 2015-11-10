package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.DateUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：差旅详情
 */
public class TravelDetailActivity extends BaseActivity {

	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private TextView tv_destination;
	private TextView tv_time;
	private TextView tv_place;
	private TextView tv_actual_begin_date;
	private TextView tv_actual_end_date;
	private TextView tv_actual_days;
	private TextView tv_arrive_date;
	private TextView tv_arrive_place;
	private TextView tv_leave_date;
	private TextView tv_leave_place;
	private TextView tv_plan_begin_date;
	private TextView tv_plan_end_date;
	private TextView tv_plan_days;
	private TextView tv_plan_man;
	private TextView tv_plan_reason;
	private TextView tv_audit_man;
	private TextView tv_audit_state;
	private TextView tv_audti_result;

	private String id = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.travel_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_destination = (TextView) findViewById(R.id.tv_title);
		tv_time = (TextView) findViewById(R.id.tv_add_time);
		tv_place = (TextView) findViewById(R.id.tv_address);
		tv_actual_begin_date = (TextView) findViewById(R.id.tv_actual_begindate);
		tv_actual_end_date = (TextView) findViewById(R.id.tv_actual_end);
		tv_actual_days = (TextView) findViewById(R.id.tv_actual_days);
		tv_arrive_date = (TextView) findViewById(R.id.tv_arrive_date);
		tv_arrive_place = (TextView) findViewById(R.id.tv_arrive_place);
		tv_leave_date = (TextView) findViewById(R.id.tv_leave_date);
		tv_leave_place = (TextView) findViewById(R.id.tv_leave_place);
		tv_plan_begin_date = (TextView) findViewById(R.id.tv_plan_begindate);
		tv_plan_end_date = (TextView) findViewById(R.id.tv_plan_enddate);
		tv_plan_days = (TextView) findViewById(R.id.tv_plan_days);
		tv_plan_man = (TextView) findViewById(R.id.tv_plan_man);
		tv_plan_reason = (TextView) findViewById(R.id.tv_plan_reason);
		tv_audit_man = (TextView) findViewById(R.id.tv_audit_man);
		tv_audit_state = (TextView) findViewById(R.id.tv_state);
		tv_audti_result = (TextView) findViewById(R.id.tv_result);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		id = bundle.getString("corptravelid");
		tv_titlebar_title.setText(R.string.travel_detail);
		sendQuery();
	}

	private void sendQuery() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("corptravelid", XmlValueUtil.encodeString(id));
        request("travel!detail?code=" + Constant.TRAVEL_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	@Override
	public void onRecvData(XmlNode response) {
//		if (response != null) {
//			String functionno = response.getText("root.functionno");
//			if (functionno.equalsIgnoreCase(Constant.TRAVEL_QUERY)) {
//				tv_destination
//						.setText((response.getChildNodeText("province") != null ? response
//								.getChildNodeText("province") : "")
//								+ (response.getChildNodeText("city") != null ? response
//										.getChildNodeText("city") : "")
//								+ (response.getChildNodeText("zone") != null ? response
//										.getChildNodeText("zone") : ""));
//				tv_place.setText((response.getChildNodeText("province") != null ? response
//						.getChildNodeText("province") : "")
//						+ (response.getChildNodeText("city") != null ? response
//								.getChildNodeText("city") : "")
//						+ (response.getChildNodeText("zone") != null ? response
//								.getChildNodeText("zone") : ""));
//				String dateStr = response.getChildNodeText("date");
//				Date date = DateUtil.str2Date(dateStr);
//				tv_time.setText(DateUtil.date2Str(date, getResources()
//						.getString(R.string.year_mouth_day_name_format))
//						+ "申请");
//				tv_actual_begin_date.setText(response
//						.getChildNodeText("actualbegindate"));
//				tv_actual_end_date.setText(response
//						.getChildNodeText("actualenddate"));
//				// tv_actual_days= (TextView) findViewById(R.id.tv_actual_days);
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				Date dBegindate;
//				try {
//					dBegindate = sdf.parse(response
//							.getChildNodeText("actualbegindate"));
//					Date dEnddate = sdf.parse(response
//							.getChildNodeText("actualenddate"));
//					int iDays = (int) ((dEnddate.getTime() - dBegindate
//							.getTime()) / 1000 / 60 / 60 / 24) + 1;
//					tv_actual_days.setText(iDays + " 天");
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				tv_arrive_date.setText(response
//						.getChildNodeText("arrivetracedate"));
//				tv_arrive_place.setText(response
//						.getChildNodeText("arrivetraceaddress"));
//
//				tv_leave_date.setText(response
//						.getChildNodeText("leavetracedate"));
//				tv_leave_place.setText(response
//						.getChildNodeText("leavetraceaddress"));
//
//				tv_plan_begin_date.setText(response
//						.getChildNodeText("begindate"));
//				tv_plan_end_date.setText(response.getChildNodeText("enddate"));
//
//				String days = response.getChildNodeText("days");
//				tv_plan_days.setText(days.replace(".0", " 天"));
//
//				tv_plan_man.setText(response.getChildNodeText("username"));
//				tv_plan_reason.setText(response.getChildNodeText("desc"));
//
//				tv_audit_man
//						.setText(response.getChildNodeText("signuser"));
//				tv_audit_state.setText("已审批");
//				tv_audti_result
//						.setText(response.getChildNodeText("signresult"));
//
//			}
//		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TRAVEL_QUERY.equals(code)) {
                JsonObject travel = response.getAsJsonObject("body");
                if(travel!=null) {
                    tv_destination.setText((travel.get("province") != null ? travel.get("province").getAsString() : "") + (travel.get("province") != null ? travel.get("city").getAsString() : "") + (travel.get("province") != null ? travel.get("zone").getAsString() : ""));
                    tv_place.setText((travel.get("province") != null ? travel.get("province").getAsString() : "") + (travel.get("province") != null ? travel.get("city").getAsString() : "") + (travel.get("province") != null ? travel.get("zone").getAsString() : ""));
                    String dateStr = travel.get("date").getAsString();
                    Date date = DateUtil.str2Date(dateStr);
                    tv_time.setText(DateUtil.date2Str(date, getResources()
                            .getString(R.string.year_mouth_day_name_format))
                            + "申请");
                    tv_actual_begin_date.setText(travel.get("actualbegindate").getAsString());
                    tv_actual_end_date.setText(travel.get("actualenddate").getAsString());
                    // tv_actual_days= (TextView) findViewById(R.id.tv_actual_days);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dBegindate;
                    try {
                        dBegindate = sdf.parse(travel.get("actualbegindate").getAsString());
                        Date dEnddate = sdf.parse(travel.get("actualenddate").getAsString());
                        int iDays = (int) ((dEnddate.getTime() - dBegindate
                                .getTime()) / 1000 / 60 / 60 / 24) + 1;
                        tv_actual_days.setText(iDays + " 天");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    
                    String arrivetracedate = null;
                    String arrivetraceaddress = null;
                    String leavetracedate = null;
                    String leavetraceaddress = null;
                    try {
                    	arrivetracedate = travel.get("arrivetracedate").getAsString();
                    	arrivetraceaddress = travel.get("arrivetraceaddress").getAsString();
                    } catch (Exception e) {
                    	arrivetracedate = "";
                    	arrivetraceaddress = "";
                    }
                    tv_arrive_date.setText(arrivetracedate);
                    tv_arrive_place.setText(arrivetraceaddress);
                    
                    try {
                    	leavetracedate = travel.get("leavetracedate").getAsString();
            			leavetraceaddress = travel.get("leavetraceaddress").getAsString();
                    } catch (Exception e) {
                    	leavetracedate = "";
            			leavetraceaddress = "";
                    }
                    tv_leave_date.setText(leavetracedate);
                    tv_leave_place.setText(leavetraceaddress);
                    
                    tv_plan_begin_date.setText(travel.get("begindate").getAsString());
                    tv_plan_end_date.setText(travel.get("enddate").getAsString());
                    String days = travel.get("days").getAsString();
                    tv_plan_days.setText(days.replace(".0", " 天"));
                    tv_plan_man.setText(travel.get("username").getAsString());
                    tv_plan_reason.setText(travel.get("desc").getAsString());
                    tv_audit_man
                            .setText(travel.get("signuser").getAsString());
                    tv_audit_state.setText("已审批");
                    tv_audti_result
                            .setText(travel.get("signresult").getAsString());
                }else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
