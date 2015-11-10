package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.widgets.CustomDatePicker;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;
import com.zjrc.isale.client.util.DateUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：执行差旅界面
 */
public class TravelExecutionActivity extends BaseActivity {

	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private RelativeLayout rl_submit_msg;
	private RelativeLayout rl_audit_msg;
	private RelativeLayout rl_execution_msg;
	private RelativeLayout rl_submit_info;
	private RelativeLayout rl_audit_info;
	private RelativeLayout ll_bottom;
	private TextView tv_destination;
	private TextView tv_arrive_date;
	private TextView tv_arrive_place;
	private TextView tv_leave_date;
	private TextView tv_leave_place;
	private TextView tv_arrive_date_title;
	private TextView tv_arrive_place_title;
	private TextView tv_leave_date_title;
	private TextView tv_leave_place_title;
	private TextView tv_submit_place;
	private TextView tv_submit_begin_date;
	private TextView tv_submit_end_date;
	private TextView tv_submit_actual_begin_date;
	private TextView tv_submit_actual_end_date;
	private TextView tv_submit_days;;
	private TextView tv_submit_reason;
	private TextView tv_submit_man;
	private TextView tv_audit_man;
	private TextView tv_audit_state;
	private TextView tv_audit_result;

	private Button btn_ok;
	private Button btn_cancel;
	String operate = "start";
	String actual_date = "";
	private String arrivetraceid;
	private String leavetraceid;
	private String state = "0";
	private String id = "";
	private boolean isSubmitInfoVisible = false;
	private boolean isAuditInfoVisible = false;
	private boolean isExecutionInfoVisible = false;

	// 定位相关
	private LocationClient mLocClient;

	private MyLocationListenner myListener = new MyLocationListenner();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.travel_execution);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.travel_execution);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		rl_submit_msg = (RelativeLayout) findViewById(R.id.rl_submit_msg);
		rl_audit_msg = (RelativeLayout) findViewById(R.id.rl_audit_msg);
		rl_execution_msg = (RelativeLayout) findViewById(R.id.rl_execution_msg);
		rl_submit_info = (RelativeLayout) findViewById(R.id.rl_submit_info);
		rl_audit_info = (RelativeLayout) findViewById(R.id.rl_audit_info);
		ll_bottom = (RelativeLayout) findViewById(R.id.ll_bottom);
		tv_destination = (TextView) findViewById(R.id.tv_travel_destination);
		tv_arrive_date = (TextView) findViewById(R.id.tv_travel_arrive_date);
		tv_arrive_place = (TextView) findViewById(R.id.tv_travel_arrive_place);
		tv_leave_date = (TextView) findViewById(R.id.tv_travel_leave_date);
		tv_leave_place = (TextView) findViewById(R.id.tv_travel_leave_place);
		tv_arrive_date_title = (TextView) findViewById(R.id.tv_travel_arrive_date_title);
		tv_arrive_place_title = (TextView) findViewById(R.id.tv_travel_arrive_place_title);
		tv_leave_date_title = (TextView) findViewById(R.id.tv_travel_leave_date_title);
		tv_leave_place_title = (TextView) findViewById(R.id.tv_travel_leave_place_title);
		tv_submit_place = (TextView) findViewById(R.id.tv_place);
		tv_submit_begin_date = (TextView) findViewById(R.id.tv_begin_date);
		tv_submit_end_date = (TextView) findViewById(R.id.tv_end_date);
		tv_submit_actual_begin_date = (TextView) findViewById(R.id.tv_actual_begin_date);
		tv_submit_actual_end_date = (TextView) findViewById(R.id.tv_actual_end_date);
		tv_submit_days = (TextView) findViewById(R.id.tv_travel_days);
		tv_submit_reason = (TextView) findViewById(R.id.tv_travel_reason);
		tv_submit_man = (TextView) findViewById(R.id.tv_travel_man);
		tv_audit_man = (TextView) findViewById(R.id.tv_audit_man);
		tv_audit_state = (TextView) findViewById(R.id.tv_state);
		tv_audit_result = (TextView) findViewById(R.id.tv_result);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		Bundle bundle = getIntent().getExtras();
		state = bundle.getString("state");
		id = bundle.getString("corptravelid");

		rl_submit_msg.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!isSubmitInfoVisible) {
					rl_submit_info.setVisibility(View.VISIBLE);
					isSubmitInfoVisible = true;
				} else {
					rl_submit_info.setVisibility(View.GONE);
					isSubmitInfoVisible = false;
				}

			}
		});

		if (!needAudit) {
			rl_audit_msg.setVisibility(View.GONE);
		} else {
			rl_audit_msg
					.setOnClickListener(new RelativeLayout.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (!isAuditInfoVisible) {
								rl_audit_info.setVisibility(View.VISIBLE);
								isAuditInfoVisible = true;
							} else {
								rl_audit_info.setVisibility(View.GONE);
								isAuditInfoVisible = false;
							}
						}
					});
		}

		sendQuery();
	}

	private void sendQuery() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("corptravelid", XmlValueUtil.encodeString(id));
        request("travel!detail?code=" + Constant.TRAVEL_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	private void updateTravel() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("actualenddate",XmlValueUtil.encodeString(actual_date));
            params.put("operate",XmlValueUtil.encodeString(operate));
            params.put("corptravelid", XmlValueUtil.encodeString(id));
            request("travel!submitActualEndDate?code=" + Constant.TRAVEL_UPDATE, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private void sendTraceSubmit(String longitude, String latitude,
			String address) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("travelid", XmlValueUtil.encodeString(id));
            params.put("type", XmlValueUtil.encodeString(type) );
            params.put("longitude", XmlValueUtil.encodeString(longitude));
            params.put("latitude", XmlValueUtil.encodeString(latitude));
            params.put("address", XmlValueUtil.encodeString(address));
            request("travelTrace!submit?code=" + Constant.TRAVELTRACE_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private void sendTravelQuery(String id) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("corptraveltraceid", XmlValueUtil.encodeString(id));
            request("travelTrace!detail?code=" + Constant.TRAVELTRACE_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	CustomDatePicker datePicker;

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
//				tv_submit_place
//						.setText((response.getChildNodeText("province") != null ? response
//								.getChildNodeText("province") : "")
//								+ (response.getChildNodeText("city") != null ? response
//										.getChildNodeText("city") : "")
//								+ (response.getChildNodeText("zone") != null ? response
//										.getChildNodeText("zone") : ""));
//				tv_submit_begin_date.setText(response
//						.getChildNodeText("begindate"));
//				tv_submit_end_date
//						.setText(response.getChildNodeText("enddate"));
//				tv_submit_actual_begin_date.setText(response
//						.getChildNodeText("actualbegindate"));
//				tv_submit_actual_end_date.setText(response
//						.getChildNodeText("actualenddate"));
//				String days = response.getChildNodeText("days");
//				tv_submit_days.setText(days.replace(".0", " 天"));
//				tv_submit_reason.setText(response.getChildNodeText("desc"));
//				ISaleApplication application = ISaleApplication.getInstance();
//				tv_submit_man.setText(XmlValueUtil.encodeString(application
//						.getConfig().getUsername()));
//				tv_audit_result
//						.setText(response.getChildNodeText("signresult"));
//				tv_audit_man.setText(response.getChildNodeText("signuser"));
//				tv_audit_state.setText("已审批");
//
//				state = response.getChildNodeText("state");
//				if (state.equals("1")) {// 未开始
//					isSubmitInfoVisible = true;
//					isAuditInfoVisible = false;
//					isExecutionInfoVisible = false;
//					btn_ok.setText("开 始");
//					btn_cancel.setVisibility(View.GONE);
//					btn_ok.setOnClickListener(new Button.OnClickListener() {
//
//						@Override
//						public void onClick(View arg0) {
//
//							datePicker = new CustomDatePicker(
//									TravelExecutionActivity.this,
//									"请输入差旅实际结束时间", new View.OnClickListener() {
//
//										@Override
//										public void onClick(View arg0) {
//											actual_date = datePicker.getYear()
//													+ "-"
//													+ datePicker.getMonth()
//													+ "-" + datePicker.getDay();
//											// tv_travel_enddate.setText(date);
//											String sBeginDate=DateUtil.getCurDateStr(getResources().getString(R.string.year_mouth_day_name_format));
////											String sBeginDate = tv_submit_begin_date
////													.getText().toString();
//											if (!"".equalsIgnoreCase(sBeginDate)) {
//												String sEndDate = actual_date;
//												SimpleDateFormat sdf = new SimpleDateFormat(
//														"yyyy-MM-dd");
//												try {
//													Date dBegindate = sdf
//															.parse(sBeginDate);
//													Date dEnddate = sdf
//															.parse(sEndDate);
//													int iDays = (int) ((dEnddate
//															.getTime() - dBegindate
//															.getTime()) / 1000 / 60 / 60 / 24) + 1;
//
//													if (iDays <= 0) {
//
//														showAlertDialog(
//																"提示",
//																"实际结束日期不能小于当前日期，请重新选择!",
//																new View.OnClickListener() {
//																	@Override
//																	public void onClick(
//																			View arg0) {
//																		alertDialog
//																				.cancel();
//																	}
//																}, "确定", null,
//																null);
//														// tv_travel_enddate.requestFocus();
//													} else {
//
//														// tv_travel_days.setText(""
//														// +
//														// iDays
//														// + " 天");
//
//														updateTravel();
//													}
//
//												} catch (Exception e) {
//
//												}
//
//												datePicker.dismiss();
//
//											}
//										}
//									}, new View.OnClickListener() {
//
//										@Override
//										public void onClick(View arg0) {
//											datePicker.dismiss();
//										}
//									}, actual_date);
//							datePicker.show();
//						}
//					});
//				} else if (state.equals("5") || state.equals("6")) {// 已离开/已结束
//																	// 跳转差旅详情页面
//					Intent intent = new Intent();
//					intent.setClass(TravelExecutionActivity.this,
//							TravelDetailActivity.class);
//					Bundle bundle = new Bundle();
//					bundle.putString("corptravelid", id);
//					intent.putExtras(bundle);
//					startActivity(intent);
//					finish();
//				} else {// 已开始/已到达 3,4
//					isExecutionInfoVisible = true;
//					isAuditInfoVisible = false;
//					isSubmitInfoVisible = false;
//					arrivetraceid = response.getChildNodeText("arrivetraceid");
//					leavetraceid = response.getChildNodeText("leavetraceid");
//
//					if (arrivetraceid != null && leavetraceid != null) {
//						ll_bottom.setVisibility(View.GONE);
//						tv_arrive_date.setText(response
//								.getChildNodeText("arrivetracedate"));
//						tv_arrive_place.setText(response
//								.getChildNodeText("arrivetraceaddress"));
//						tv_leave_date.setText(response
//								.getChildNodeText("leavetracedate"));
//						tv_leave_place.setText(response
//								.getChildNodeText("leavetraceaddress"));
//						tv_arrive_date_title.setHintTextColor(getResources()
//								.getColor(R.color.v2_text));
//						tv_arrive_place_title.setHintTextColor(getResources()
//								.getColor(R.color.v2_text));
//						tv_leave_date_title.setHintTextColor(getResources()
//								.getColor(R.color.v2_text));
//						tv_leave_place_title.setHintTextColor(getResources()
//								.getColor(R.color.v2_text));
//					} else if (arrivetraceid != null && leavetraceid == null) {
//						ll_bottom.setVisibility(View.VISIBLE);
//						tv_arrive_date.setText(response
//								.getChildNodeText("arrivetracedate"));
//						tv_arrive_place.setText(response
//								.getChildNodeText("arrivetraceaddress"));
//						btn_ok.setText("离开");
//						btn_cancel.setText("设置实际结束时间");
//						type = "1";
//						operate = "update";
//						tv_arrive_date_title.setHintTextColor(getResources()
//								.getColor(R.color.v2_text));
//						tv_arrive_place_title.setHintTextColor(getResources()
//								.getColor(R.color.v2_text));
//					} else {
//						ll_bottom.setVisibility(View.VISIBLE);
//						btn_ok.setText("到达");
//						btn_cancel.setText("设置实际结束时间");
//						operate = "update";
//						type = "0";
//					}
//
//					btn_ok.setOnClickListener(new Button.OnClickListener() {
//
//						@Override
//						public void onClick(View arg0) {
//							locationed = false;
//							mLocClient = new LocationClient(
//									getApplicationContext());
//							mLocClient.registerLocationListener(myListener);
//							mLocClient.start();
//							LocationClientOption option = new LocationClientOption();
//							option.setOpenGps(true);
//							option.setAddrType("all");// 返回的定位结果包含地址信息
//							option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
//							option.setScanSpan(1000);// 设置发起定位请求的间隔时间为4000ms
//							option.disableCache(true);// 禁止启用缓存定位
//							option.setPoiNumber(1); // 最多返回POI个数
//							option.setPoiDistance(1000); // poi查询距离
//							option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
//							mLocClient.setLocOption(option);
//							mLocClient.requestLocation();
//							islocation = true;
//							progressdialog = new CustomProgressDialog(
//									TravelExecutionActivity.this,
//									new View.OnClickListener() {
//										@Override
//										public void onClick(View v) {
//											if (islocation) {
//												mLocClient
//														.unRegisterLocationListener(myListener);
//												mLocClient.stop();
//											}
//											finish();
//										}
//									});
//							progressdialog.show();
//							progressdialog.setMessage("正在定位,请稍等...");
//						}
//					});
//
//					btn_cancel.setOnClickListener(new Button.OnClickListener() {
//
//						@Override
//						public void onClick(View arg0) {
//
//							datePicker = new CustomDatePicker(
//									TravelExecutionActivity.this,
//									"请输入差旅实际结束时间", new View.OnClickListener() {
//
//										@Override
//										public void onClick(View arg0) {
//											actual_date = datePicker.getYear()
//													+ "-"
//													+ datePicker.getMonth()
//													+ "-" + datePicker.getDay();
//											// tv_travel_enddate.setText(date);
////											String sBeginDate = tv_submit_actual_begin_date
////													.getText().toString();
//											String sBeginDate=DateUtil.getCurDateStr(getResources().getString(R.string.year_mouth_day_name_format));
//
//											if (!"".equalsIgnoreCase(sBeginDate)) {
//												String sEndDate = actual_date;
//												SimpleDateFormat sdf = new SimpleDateFormat(
//														"yyyy-MM-dd");
//												try {
//													Date dBegindate = sdf
//															.parse(sBeginDate);
//													Date dEnddate = sdf
//															.parse(sEndDate);
//													int iDays = (int) ((dEnddate
//															.getTime() - dBegindate
//															.getTime()) / 1000 / 60 / 60 / 24) + 1;
//
//													if (iDays <= 0) {
//
//														showAlertDialog(
//																"提示",
//																"实际结束日期不能小于当前日期，请重新选择!",
//																new View.OnClickListener() {
//																	@Override
//																	public void onClick(
//																			View arg0) {
//																		alertDialog
//																				.cancel();
//																	}
//																}, "确定", null,
//																null);
//														// tv_travel_enddate.requestFocus();
//													} else {
//
//														// tv_travel_days.setText(""
//														// +
//														// iDays
//														// + " 天");
//
//														updateTravel();
//													}
//
//												} catch (Exception e) {
//
//												}
//
//												datePicker.dismiss();
//
//											}
//										}
//									}, new View.OnClickListener() {
//
//										@Override
//										public void onClick(View arg0) {
//											datePicker.dismiss();
//										}
//									}, actual_date);
//							datePicker.show();
//						}
//					});
//
//				}
//				if (isAuditInfoVisible) {
//					rl_audit_info.setVisibility(View.VISIBLE);
//				} else
//					rl_audit_info.setVisibility(View.GONE);
//				if (isSubmitInfoVisible) {
//					rl_submit_info.setVisibility(View.VISIBLE);
//				} else
//					rl_submit_info.setVisibility(View.GONE);
//				if (isExecutionInfoVisible) {
//					rl_execution_msg.setVisibility(View.VISIBLE);
//				} else
//					rl_execution_msg.setVisibility(View.GONE);
//
//			}
//		}
	}

	private boolean islocation;
	private boolean locationed;
	private String arrive_longitude = "";
	private String arrive_latitude = "";
	private String leave_longitude = "";
	private String leave_latitude = "";
	private String arrive_place = "";
	private String leave_place = "";
	private CustomProgressDialog progressdialog;

	private String type;

	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			locationed = true;
			if (type.equalsIgnoreCase("0")) {// 到达
				arrive_longitude = String.valueOf(location.getLongitude());
				arrive_latitude = String.valueOf(location.getLatitude());
				if (location.getLocType() == BDLocation.TypeGpsLocation) {
					arrive_place = location.getAddrStr();
					sendTraceSubmit(arrive_longitude, arrive_latitude,
							arrive_place);
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					arrive_place = location.getAddrStr();
					sendTraceSubmit(arrive_longitude, arrive_latitude,
							arrive_place);
				} else {
					arrive_place = "暂时无法定位";
					showAlertDialog("提示", "定位失败，重新尝试定位还是继续登记？",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									alertDialog.cancel();
									locationed = false;
									mLocClient = new LocationClient(
											getApplicationContext());
									mLocClient
											.registerLocationListener(myListener);
									mLocClient.start();
									LocationClientOption option = new LocationClientOption();
									option.setOpenGps(true);
									option.setAddrType("all");// 返回的定位结果包含地址信息
									option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
									option.setScanSpan(1000);// 设置发起定位请求的间隔时间为4000ms
									option.disableCache(true);// 禁止启用缓存定位
									option.setPoiNumber(1); // 最多返回POI个数
									option.setPoiDistance(1000); // poi查询距离
									option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
									mLocClient.setLocOption(option);
									mLocClient.requestLocation();
									islocation = true;
									progressdialog = new CustomProgressDialog(
											TravelExecutionActivity.this,
											new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													if (islocation) {
														mLocClient
																.unRegisterLocationListener(myListener);
														mLocClient.stop();
													}
													finish();
												}
											});
									progressdialog.show();
									progressdialog.setMessage("正在定位,请稍等...");
								}
							}, "重试", "继续登记", new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									alertDialog.cancel();
									sendTraceSubmit(arrive_longitude,
											arrive_latitude, arrive_place);
								}
							});

				}

			} else if (type.equalsIgnoreCase("1")) {// 离开
				leave_longitude = String.valueOf(location.getLongitude());
				leave_latitude = String.valueOf(location.getLatitude());
				if (location.getLocType() == BDLocation.TypeGpsLocation) {
					leave_place = location.getAddrStr();
					sendTraceSubmit(leave_longitude, leave_latitude,
							leave_place);
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					leave_place = location.getAddrStr();
					sendTraceSubmit(leave_longitude, leave_latitude,
							leave_place);
				} else {
					leave_place = "暂时无法定位";
					showAlertDialog("提示", "定位失败，重新尝试定位还是继续登记？",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									alertDialog.cancel();
									locationed = false;
									mLocClient = new LocationClient(
											getApplicationContext());
									mLocClient
											.registerLocationListener(myListener);
									mLocClient.start();
									LocationClientOption option = new LocationClientOption();
									option.setOpenGps(true);
									option.setAddrType("all");// 返回的定位结果包含地址信息
									option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
									option.setScanSpan(1000);// 设置发起定位请求的间隔时间为4000ms
									option.disableCache(true);// 禁止启用缓存定位
									option.setPoiNumber(1); // 最多返回POI个数
									option.setPoiDistance(1000); // poi查询距离
									option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
									mLocClient.setLocOption(option);
									mLocClient.requestLocation();
									islocation = true;
									progressdialog = new CustomProgressDialog(
											TravelExecutionActivity.this,
											new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													if (islocation) {
														mLocClient
																.unRegisterLocationListener(myListener);
														mLocClient.stop();
													}
													finish();
												}
											});
									progressdialog.show();
									progressdialog.setMessage("正在定位,请稍等...");
								}
							}, "重试", "继续登记", new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									sendTraceSubmit(leave_longitude,
											leave_latitude, leave_place);
									alertDialog.cancel();
								}
							});
				}
			}
			mLocClient.stop();
			islocation = false;
			if (progressdialog != null) {
				progressdialog.cancel();
				progressdialog = null;
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TRAVEL_QUERY.equals(code)) {
                JsonObject travel = response.getAsJsonObject("body");
                if(travel!=null) {
                    tv_destination
                            .setText((travel.get("province") != null ? travel.get("province").getAsString() : "") + (travel.get("province") != null ? travel.get("city").getAsString() : "") + (travel.get("province") != null ? travel.get("zone").getAsString() : ""));
                    tv_submit_place
                            .setText((travel.get("province") != null ? travel.get("province").getAsString() : "") + (travel.get("province") != null ? travel.get("city").getAsString() : "") + (travel.get("province") != null ? travel.get("zone").getAsString() : ""));
                    tv_submit_begin_date.setText(travel.get("begindate").getAsString());
                    tv_submit_end_date
                            .setText(travel.get("enddate").getAsString());
                    tv_submit_actual_begin_date.setText(travel.get("actualbegindate").getAsString());
                    tv_submit_actual_end_date.setText(travel.get("actualenddate").getAsString());
                    String days =travel.get("days").getAsString();
                    tv_submit_days.setText(days.replace(".0", " 天"));
                    tv_submit_reason.setText(travel.get("desc").getAsString());
                    ISaleApplication application = ISaleApplication.getInstance();
                    tv_submit_man.setText(XmlValueUtil.encodeString(application
                            .getConfig().getUsername()));
                    tv_audit_result
                            .setText(travel.get("signresult").getAsString());
                    tv_audit_man.setText(travel.get("signuser").getAsString());
                    tv_audit_state.setText("已审批");

                    state = travel.get("state").getAsString();
                    if (state.equals("1")) {// 未开始
                        isSubmitInfoVisible = true;
                        isAuditInfoVisible = false;
                        isExecutionInfoVisible = false;
                        btn_ok.setText("开 始");
                        btn_cancel.setVisibility(View.GONE);
                        btn_ok.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {

                                datePicker = new CustomDatePicker(
                                        TravelExecutionActivity.this,
                                        "请输入差旅实际结束时间", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        actual_date = datePicker.getYear()
                                                + "-"
                                                + datePicker.getMonth()
                                                + "-" + datePicker.getDay();
                                        // tv_travel_enddate.setText(date);
                                        String sBeginDate=DateUtil.getCurDateStr(getResources().getString(R.string.year_mouth_day_name_format));
//											String sBeginDate = tv_submit_begin_date
//													.getText().toString();
                                        if (!"".equalsIgnoreCase(sBeginDate)) {
                                            String sEndDate = actual_date;
                                            SimpleDateFormat sdf = new SimpleDateFormat(
                                                    "yyyy-MM-dd");
                                            try {
                                                Date dBegindate = sdf
                                                        .parse(sBeginDate);
                                                Date dEnddate = sdf
                                                        .parse(sEndDate);
                                                int iDays = (int) ((dEnddate
                                                        .getTime() - dBegindate
                                                        .getTime()) / 1000 / 60 / 60 / 24) + 1;

                                                if (iDays <= 0) {

                                                    showAlertDialog(
                                                            "提示",
                                                            "实际结束日期不能小于当前日期，请重新选择!",
                                                            new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(
                                                                        View arg0) {
                                                                    alertDialog
                                                                            .cancel();
                                                                }
                                                            }, "确定", null,
                                                            null);
                                                    // tv_travel_enddate.requestFocus();
                                                } else {

                                                    // tv_travel_days.setText(""
                                                    // +
                                                    // iDays
                                                    // + " 天");

                                                    updateTravel();
                                                }

                                            } catch (Exception e) {

                                            }

                                            datePicker.dismiss();

                                        }
                                    }
                                }, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        datePicker.dismiss();
                                    }
                                }, actual_date);
                                datePicker.show();
                            }
                        });
                    } else if (state.equals("5") || state.equals("6")) {// 已离开/已结束
                        // 跳转差旅详情页面
                        Intent intent = new Intent();
                        intent.setClass(TravelExecutionActivity.this,
                                TravelDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("corptravelid", id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else {// 已开始/已到达 3,4
                        isExecutionInfoVisible = true;
                        isAuditInfoVisible = false;
                        isSubmitInfoVisible = false;
                        try{
                        	arrivetraceid = travel.get("arrivetraceid").getAsString();
                        } catch (Exception e) {
                        	arrivetraceid = null;
                        }
                        try{
                        	leavetraceid = travel.get("leavetraceid").getAsString();
                        } catch (Exception e) {
                        	leavetraceid = null;
                        }

                        if (arrivetraceid != null && leavetraceid != null) {
                            ll_bottom.setVisibility(View.GONE);
                            tv_arrive_date.setText(travel.get("arrivetracedate").getAsString());
                            tv_arrive_place.setText(travel.get("arrivetraceaddress").getAsString());
                            tv_leave_date.setText(travel.get("leavetracedate").getAsString());
                            tv_leave_place.setText(travel.get("leavetraceaddress").getAsString());
                            tv_arrive_date_title.setHintTextColor(getResources()
                                    .getColor(R.color.v2_text));
                            tv_arrive_place_title.setHintTextColor(getResources()
                                    .getColor(R.color.v2_text));
                            tv_leave_date_title.setHintTextColor(getResources()
                                    .getColor(R.color.v2_text));
                            tv_leave_place_title.setHintTextColor(getResources()
                                    .getColor(R.color.v2_text));
                        } else if (arrivetraceid != null && leavetraceid == null) {
                            ll_bottom.setVisibility(View.VISIBLE);
                            tv_arrive_date.setText(travel.get("arrivetracedate").getAsString());
                            tv_arrive_place.setText(travel.get("arrivetraceaddress").getAsString());
                            btn_ok.setText("离开");
                            btn_cancel.setText("设置实际结束时间");
                            type = "1";
                            operate = "update";
                            tv_arrive_date_title.setHintTextColor(getResources()
                                    .getColor(R.color.v2_text));
                            tv_arrive_place_title.setHintTextColor(getResources()
                                    .getColor(R.color.v2_text));
                        } else {
                            ll_bottom.setVisibility(View.VISIBLE);
                            btn_ok.setText("到达");
                            btn_cancel.setText("设置实际结束时间");
                            operate = "update";
                            type = "0";
                        }

                        btn_ok.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                locationed = false;
                                mLocClient = new LocationClient(
                                        getApplicationContext());
                                mLocClient.registerLocationListener(myListener);
                                mLocClient.start();
                                LocationClientOption option = new LocationClientOption();
                                option.setOpenGps(true);
                                option.setAddrType("all");// 返回的定位结果包含地址信息
                                option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
                                option.setScanSpan(1000);// 设置发起定位请求的间隔时间为4000ms
                                option.disableCache(true);// 禁止启用缓存定位
                                option.setPoiNumber(1); // 最多返回POI个数
                                option.setPoiDistance(1000); // poi查询距离
                                option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
                                mLocClient.setLocOption(option);
                                mLocClient.requestLocation();
                                islocation = true;
                                progressdialog = new CustomProgressDialog(
                                        TravelExecutionActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (islocation) {
                                                    mLocClient
                                                            .unRegisterLocationListener(myListener);
                                                    mLocClient.stop();
                                                }
                                                finish();
                                            }
                                        });
                                progressdialog.show();
                                progressdialog.setMessage("正在定位,请稍等...");
                            }
                        });

                        btn_cancel.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {

                                datePicker = new CustomDatePicker(
                                        TravelExecutionActivity.this,
                                        "请输入差旅实际结束时间", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        actual_date = datePicker.getYear()
                                                + "-"
                                                + datePicker.getMonth()
                                                + "-" + datePicker.getDay();
                                        // tv_travel_enddate.setText(date);
//											String sBeginDate = tv_submit_actual_begin_date
//													.getText().toString();
                                        String sBeginDate=DateUtil.getCurDateStr(getResources().getString(R.string.year_mouth_day_name_format));

                                        if (!"".equalsIgnoreCase(sBeginDate)) {
                                            String sEndDate = actual_date;
                                            SimpleDateFormat sdf = new SimpleDateFormat(
                                                    "yyyy-MM-dd");
                                            try {
                                                Date dBegindate = sdf
                                                        .parse(sBeginDate);
                                                Date dEnddate = sdf
                                                        .parse(sEndDate);
                                                int iDays = (int) ((dEnddate
                                                        .getTime() - dBegindate
                                                        .getTime()) / 1000 / 60 / 60 / 24) + 1;

                                                if (iDays <= 0) {

                                                    showAlertDialog(
                                                            "提示",
                                                            "实际结束日期不能小于当前日期，请重新选择!",
                                                            new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(
                                                                        View arg0) {
                                                                    alertDialog
                                                                            .cancel();
                                                                }
                                                            }, "确定", null,
                                                            null);
                                                    // tv_travel_enddate.requestFocus();
                                                } else {

                                                    // tv_travel_days.setText(""
                                                    // +
                                                    // iDays
                                                    // + " 天");

                                                    updateTravel();
                                                }

                                            } catch (Exception e) {

                                            }

                                            datePicker.dismiss();

                                        }
                                    }
                                }, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        datePicker.dismiss();
                                    }
                                }, actual_date);
                                datePicker.show();
                            }
                        });

                    }
                    if (isAuditInfoVisible) {
                        rl_audit_info.setVisibility(View.VISIBLE);
                    } else
                        rl_audit_info.setVisibility(View.GONE);
                    if (isSubmitInfoVisible) {
                        rl_submit_info.setVisibility(View.VISIBLE);
                    } else
                        rl_submit_info.setVisibility(View.GONE);
                    if (isExecutionInfoVisible) {
                        rl_execution_msg.setVisibility(View.VISIBLE);
                    } else
                        rl_execution_msg.setVisibility(View.GONE);

                }else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }

            }else if(Constant.TRAVELTRACE_SUBMIT.equals(code)){
                Intent intent = new Intent();
                if (type.equalsIgnoreCase("0")) {
                    Toast.makeText(this, "到达登记成功!", Toast.LENGTH_SHORT).show();
                    intent.setAction(Constant.TRAVEL_ARRIVE_ACTION);
                } else if (type.equalsIgnoreCase("1")) {
                    Toast.makeText(this, "离开登记成功!", Toast.LENGTH_SHORT).show();
                    intent.setAction(Constant.TRAVEL_LEAVE_ACTION);
                }
                sendBroadcast(intent);
                sendQuery();
            } else if(Constant.TRAVEL_UPDATE.equals(code)){
                if (response.get("result").getAsString().equalsIgnoreCase("0")) {
                    if (operate.equalsIgnoreCase("start")) {
                            Toast.makeText(this, "差旅开始成功!", Toast.LENGTH_SHORT)
                                    .show();
                            operate = "update";
                            state = "started";
                            btn_cancel.setVisibility(View.VISIBLE);
                            btn_cancel.setText("修改结束时间");
                            btn_ok.setText("到达");
                            type = "0";
                            sendQuery();
                            rl_execution_msg.setVisibility(View.VISIBLE);
                            Intent intent = new Intent();
                            intent.setAction(Constant.TRAVEL_START_ACTION);
                            sendBroadcast(intent);
                    } else
                    {
                            // tv_submit_actual_end_date.setText(response
                            // .getChildNodeText("actualenddate"));
                            sendQuery();
                            Toast.makeText(this, "差旅结束时间修改成功!", Toast.LENGTH_SHORT)
                                    .show();
                    }
                }

            }

        }
    }
}
