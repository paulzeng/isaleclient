package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Audit;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.bean.TerminalType;
import com.zjrc.isale.client.ui.widgets.CustomDatePicker;
import com.zjrc.isale.client.ui.widgets.CustomPopupDialog;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：差旅申请
 */
public class TravelSubmitActivity extends BaseActivity {
	private TextView tv_titlebar_title;
	private Button btn_titlebar_back;
	private Button btn_ok;

	private TextView tv_travel_terminal;
	private TextView tv_travel_begindate;
	private TextView tv_travel_enddate;
	private TextView tv_travel_days;
	private TextView tv_travel_man;
	private EditText et_travel_reason;
	private String auditid = "";
	private CustomPopupDialog auditManDialog = null;
	private String[] lsName;
	ArrayList<Audit> areas;
	private String travelid = "";
	String operate = "insert";
	String audit = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.travel_submit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);

		tv_titlebar_title.setText(R.string.travel_submit);

		tv_travel_terminal = (TextView) findViewById(R.id.tv_terminal);
		tv_travel_begindate = (TextView) findViewById(R.id.tv_travel_begindate);
		tv_travel_enddate = (TextView) findViewById(R.id.tv_travel_enddate);
		tv_travel_days = (TextView) findViewById(R.id.tv_travel_days);
		tv_travel_man = (TextView) findViewById(R.id.tv_travel_man);
		et_travel_reason = (EditText) findViewById(R.id.et_travel_reason);

        if (!needAudit) {
            findViewById(R.id.rl_travel_man).setVisibility(View.GONE);
        }

		btn_ok = (Button) findViewById(R.id.btn_check);
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (validityInput()) {
						sendSubmit();
					}
				}
			}
		});
		traveladd();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		operate = bundle.getString("operate");
		travelid = bundle.getString("travelid");
		if (operate.equalsIgnoreCase("modify")) {
			sendQuery();
		}
	}
	private void sendQuery() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("corptravelid", XmlValueUtil.encodeString(travelid));
        request("travel!detail?code=" + Constant.TRAVEL_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	String terminalid = "";
	CustomDatePicker datePicker;

	private void traveladd() {
		addAuditList();

		// types = getResources().getStringArray(R.array.traveltypes);

		tv_travel_terminal.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					intent.setClass(TravelSubmitActivity.this,
							TravelDestinationActivity.class);
					startActivityForResult(intent, Constant.TRAVEL_DESTINATION);
				}
			}
		});

		tv_travel_begindate.setText(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date()));
		tv_travel_begindate.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					datePicker = new CustomDatePicker(TravelSubmitActivity.this,
							"请选择开始时间", new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							tv_travel_begindate.setText(datePicker.getYear()
									+ "-" + datePicker.getMonth() + "-"
									+ datePicker.getDay());
							String sEndDate = tv_travel_enddate.getText().toString();
							if (!"".equalsIgnoreCase(sEndDate)) {
								String sBeginDate = tv_travel_begindate
										.getText().toString();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								try {
									Date dBegindate = sdf.parse(sBeginDate);
									Date dEnddate = sdf.parse(sEndDate);
									int iDays = (int) ((dEnddate.getTime() - dBegindate
											.getTime()) / 1000 / 60 / 60 / 24) + 1;
									if (iDays <= 0) {
										showAlertDialog("提示",
												"结束日期不能小于开始日期，请重新选择!",
												new View.OnClickListener() {
											@Override
											public void onClick(View arg0) {
												alertDialog.cancel();
											}
										}, "确定", null, null);
										tv_travel_enddate.requestFocus();
									} else {
										tv_travel_days.setText("" + iDays + " 天");
									}
									
								} catch (Exception e) {
								}
							}
							datePicker.dismiss();
						}
					}, new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							datePicker.dismiss();
						}
					}, tv_travel_begindate.getText().toString());
					datePicker.show();
				}
			}
		});

		tv_travel_enddate.setText(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date()));

		tv_travel_enddate.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					datePicker = new CustomDatePicker(TravelSubmitActivity.this,
							"请选择結束时间", new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							tv_travel_enddate.setText(datePicker.getYear()
									+ "-" + datePicker.getMonth() + "-"
									+ datePicker.getDay());
							String sBeginDate = tv_travel_begindate.getText().toString();
							if (!"".equalsIgnoreCase(sBeginDate)) {
								String sEndDate = tv_travel_enddate.getText().toString();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								try {
									Date dBegindate = sdf.parse(sBeginDate);
									Date dEnddate = sdf.parse(sEndDate);
									int iDays = (int) ((dEnddate.getTime() - dBegindate
											.getTime()) / 1000 / 60 / 60 / 24) + 1;
									
									if (iDays <= 0) {
										showAlertDialog("提示",
												"结束日期不能小于开始日期，请重新选择!",
												new View.OnClickListener() {
											@Override
											public void onClick(View arg0) {
												alertDialog.cancel();
											}
										}, "确定", null, null);
										tv_travel_enddate.requestFocus();
									} else {
										tv_travel_days.setText("" + iDays + " 天");
									}
								} catch (Exception e) {
								}
							}
							datePicker.dismiss();
						}
					}, new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							datePicker.dismiss();
						}
					}, tv_travel_enddate.getText().toString());
					datePicker.show();
				}
			}
		});
		tv_travel_days.setText("1 天");
		tv_titlebar_title.requestFocus();
	}

	TerminalType province = new TerminalType("", "");
	TerminalType city = new TerminalType("", "");
	TerminalType zone = new TerminalType("", "");

	private void sendSubmit() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("operate", XmlValueUtil.encodeString(operate));
            params.put("province",XmlValueUtil.encodeString(!province.getName().equalsIgnoreCase("")?province.getName():""));
            params.put("city",  XmlValueUtil.encodeString(!city.getName().equalsIgnoreCase("")?city.getName():""));
            params.put("zone",  XmlValueUtil.encodeString(!zone.getName().equalsIgnoreCase("")?zone.getName():""));
            params.put("desc", XmlValueUtil.encodeString(et_travel_reason.getText()
                    .toString()));
            String days = tv_travel_days.getText().toString();
            params.put("days", XmlValueUtil.encodeString(days.substring(0,
                    days.length() - 2)));
            params.put("begindate", XmlValueUtil.encodeString(tv_travel_begindate.getText()
                    .toString()));
            params.put("enddate", XmlValueUtil.encodeString(tv_travel_enddate.getText()
                    .toString()));
            params.put("corptravelid", XmlValueUtil.encodeString(travelid));
            params.put("audituserid", auditid);
            request("travel!submit?code=" + Constant.TRAVEL_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private boolean validityInput() {
		String sBeginDate = tv_travel_begindate.getText().toString();
		if ("".equalsIgnoreCase(sBeginDate)) {
			showAlertDialog("提示", "开始日期不能为空,请输入开始日期!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_travel_begindate.requestFocus();
			return false;
		}
		String sEndDate = tv_travel_enddate.getText().toString();
		if ("".equalsIgnoreCase(sEndDate)) {
			showAlertDialog("提示", "结束日期不能为空,请输入结束日期!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_travel_enddate.requestFocus();
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date begindate;
		Date enddate;
		try {
			begindate = sdf.parse(sBeginDate);
			enddate = sdf.parse(sEndDate);
		} catch (Exception e) {
			begindate = new Date();
			enddate = new Date();
		}
		if (enddate.getTime() < begindate.getTime()) {
			showAlertDialog("提示", "结束日期不能小于开始日期，请选择结束日期!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_travel_enddate.requestFocus();
			return false;
		}
		String d = tv_travel_days.getText().toString();
		String sDays = d.substring(0, d.length() - 2);
		if ("".equalsIgnoreCase(sDays)) {
			showAlertDialog("提示", "差旅天数不能为空,请输入差旅天数!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_travel_days.requestFocus();
			return false;
		}

		if (Double.valueOf(sDays) > 999) {
			showAlertDialog("提示", "最长差旅天数不能超过999天,请重新输入差旅天数!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_travel_days.requestFocus();
			return false;
		}
		String sReason = et_travel_reason.getText().toString();
		if ("".equalsIgnoreCase(sReason)) {
			showAlertDialog("提示", "差旅缘由不能为空,请输入差旅缘由!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_travel_reason.requestFocus();
			return false;
		}

		if ("".equalsIgnoreCase(tv_travel_terminal.getText().toString())) {
			showAlertDialog("提示", "目的地不能为空，请选择差旅目的地!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_travel_terminal.requestFocus();
			return false;
		}
		if (needAudit && "".equalsIgnoreCase(auditid)) {
			showAlertDialog("提示", "审批人不能为空,请选择审批人!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_travel_man.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public void onRecvData(XmlNode response) {
//		if (response != null) {
//			String functionno = response.getText("root.functionno");
//			if (functionno.equalsIgnoreCase(Constant.TRAVEL_SUBMIT)) {
//				Intent intent = new Intent();
//				if (operate.equalsIgnoreCase("insert")) {
//					Toast.makeText(this, "差旅申请上报成功!", Toast.LENGTH_SHORT)
//							.show();
//					intent.setAction(Constant.TRAVEL_ADD_ACTION);
//				} else {
//					Toast.makeText(this, "差旅申请修改成功!", Toast.LENGTH_SHORT)
//							.show();
//					intent.setAction(Constant.TRAVEL_MODIFY_ACTION);
//				}
//				// intent.setAction(Constant.HOMEPAGE_REFRESH_ACTION);
//				sendBroadcast(intent);
//				finish();
//			} else if (functionno.equalsIgnoreCase(Constant.TRAVEL_QUERY)) {
//				if (operate.equalsIgnoreCase("modify")) {
//					tv_travel_begindate.setText(response
//							.getChildNodeText("begindate"));
//					tv_travel_enddate.setText(response
//							.getChildNodeText("enddate"));
//					tv_travel_days.setText(response.getChildNodeText("days")
//							.replace(".0", " 天"));
//					et_travel_reason.setText(response.getChildNodeText("desc"));
//					selectAudit(response.getChildNodeText("auditusername"));
//					tv_travel_terminal.setText((response.getChildNodeText("province") != null ? response
//							.getChildNodeText("province") : "")
//							+ (response.getChildNodeText("city") != null ? response
//									.getChildNodeText("city") : "")
//							+ (response.getChildNodeText("zone") != null ? response
//									.getChildNodeText("zone") : ""));
//					province = new TerminalType("",
//							(response.getChildNodeText("province") != null ? response
//									.getChildNodeText("province") : ""));
//					city = new TerminalType("",
//							(response.getChildNodeText("city") != null ? response
//									.getChildNodeText("city") : ""));
//					zone = new TerminalType("",
//							(response.getChildNodeText("zone") != null ? response
//									.getChildNodeText("zone") : ""));
//					tv_travel_man.setText(audit);
//				}
//			}
//		}
	}

	private void selectAudit(String sName) {
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null && application.getConfig().getNeedaudit()) {
			if (areas != null) {
				int iIndex = 0;
				for (Audit at : areas) {
					if (sName.equalsIgnoreCase(at.getUsername())) {
						break;
					}
					iIndex++;
				}
				if(iIndex != areas.size()){
					auditid = areas.get(iIndex).getUserid();
					audit = areas.get(iIndex).getUsername();
				}
			}
		}
	}

	private void addAuditList() {
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null && application.getConfig().getNeedaudit()) {
			areas = application.getAudit();
			if (areas != null) {
				lsName = new String[areas.size()];
				for (int i = 0; i < areas.size(); i++) {
					lsName[i] = areas.get(i).getUsername();
				}
				// auditid = areas.get(0).getUserid();
			}
		}
		tv_travel_man.setHint("请选择审批人");
		tv_travel_man.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (auditManDialog == null) {
						auditManDialog = new CustomPopupDialog(
								TravelSubmitActivity.this, "审批人", lsName,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_travel_man.setText(((TextView) view)
												.getText());
										auditManDialog.dismiss();
										auditid = areas.get(position).getUserid();
									}
								});
					}
					auditManDialog.show();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.TRAVEL_DESTINATION) {
			if (resultCode == Constant.RESULT_TRAVEL_DESTINATION) {
				Bundle bundle = data.getExtras();
				province = (TerminalType) bundle.getSerializable("province");
				city = (TerminalType) bundle.getSerializable("city");
				zone = (TerminalType) bundle.getSerializable("zone");
				tv_travel_terminal.setText(province.getName() + " "
						+ city.getName() + " " + zone.getName());
			}
		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TRAVEL_QUERY.equals(code)) {
                if (operate.equalsIgnoreCase("modify")) {
                    JsonObject travel = response.getAsJsonObject("body");
                    if (travel != null) {
                        tv_travel_begindate.setText(travel.get("begindate").getAsString());
                        tv_travel_enddate.setText(travel.get("enddate").getAsString());
                        tv_travel_days.setText(travel.get("days").getAsString()
                                .replace(".0", " 天"));
                        et_travel_reason.setText(travel.get("desc").getAsString());
                        selectAudit(travel.get("auditusername").getAsString());
                        tv_travel_terminal.setText((travel.get("province") != null ? travel.get("province").getAsString() : "") + (travel.get("province") != null ? travel.get("city").getAsString() : "") + (travel.get("province") != null ? travel.get("zone").getAsString() : ""));
                        province = new TerminalType("",
                                (travel.get("province") != null ? travel.get("province").getAsString() : ""));
                        city = new TerminalType("",
                                (travel.get("city") != null ? travel.get("city").getAsString() : ""));
                        zone = new TerminalType("",
                                (travel.get("zone") != null ? travel.get("zone").getAsString() : ""));
                        tv_travel_man.setText(audit);
                    } else {
                        Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                    }


                }


            }else if(Constant.TRAVEL_SUBMIT.equals(code)){
                Intent intent = new Intent();
                if (operate.equalsIgnoreCase("insert")) {
                    Toast.makeText(this, "差旅申请上报成功!", Toast.LENGTH_SHORT)
                            .show();
                    intent.setAction(Constant.TRAVEL_ADD_ACTION);
                } else {
                    Toast.makeText(this, "差旅申请修改成功!", Toast.LENGTH_SHORT)
                            .show();
                    intent.setAction(Constant.TRAVEL_MODIFY_ACTION);
                }
                // intent.setAction(Constant.HOMEPAGE_REFRESH_ACTION);
                sendBroadcast(intent);
                finish();
            }
        }
    }
}
