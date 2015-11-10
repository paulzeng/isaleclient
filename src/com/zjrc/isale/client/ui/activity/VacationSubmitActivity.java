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
 * @功能描述：休假申请
 */
public class VacationSubmitActivity extends BaseActivity {
	private TextView tv_titlebar_title;
	private Button btn_titlebar_back;
	private Button btn_ok;

	private TextView tv_vacation_type;
	private TextView tv_vacation_begindate;
	private TextView tv_vacation_enddate;
	private TextView tv_vacation_days;
	private TextView tv_vacation_man;
	private EditText et_vacation_reason;
	private String type = "0";
	private String auditid = "";
	private CustomPopupDialog vacationTypeDialog = null;
	private CustomPopupDialog auditManDialog = null;
	private String[] types;
	private String[] lsName;
	ArrayList<Audit> areas;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.vacation_submit);
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

		tv_titlebar_title.setText(R.string.vacation_submit);

		tv_vacation_type = (TextView) findViewById(R.id.tv_vacation_type);
		tv_vacation_begindate = (TextView) findViewById(R.id.tv_vacation_begindate);
		tv_vacation_enddate = (TextView) findViewById(R.id.tv_vacation_enddate);
		tv_vacation_days = (TextView) findViewById(R.id.tv_vacation_days);
		tv_vacation_man = (TextView) findViewById(R.id.tv_vacation_man);
        if (!needAudit) {
            findViewById(R.id.rl_vacation_man).setVisibility(View.GONE);
        }
		et_vacation_reason = (EditText) findViewById(R.id.et_vacation_reason);

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
		vacationadd();

	}

	CustomDatePicker datePicker;

	private void vacationadd() {
		addAuditList();

		types = getResources().getStringArray(R.array.vacationtypes);

		tv_vacation_type.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (vacationTypeDialog == null) {
						vacationTypeDialog = new CustomPopupDialog(
								VacationSubmitActivity.this, "休假类型", types,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_vacation_type.setText(((TextView) view)
												.getText());
										vacationTypeDialog.dismiss();
										type = String.valueOf(position);
									}
								});
					}
					vacationTypeDialog.show();
				}
			}
		});

//		tv_vacation_begindate.setText(new SimpleDateFormat("yyyy-MM-dd")
//				.format(new Date()));
		tv_vacation_begindate
				.setOnClickListener(new TextView.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (CommonUtils.isNotFastDoubleClick()) {
							datePicker = new CustomDatePicker(
									VacationSubmitActivity.this, "请输入休假开始时间",
									new View.OnClickListener() {
										
										@Override
										public void onClick(View arg0) {
											tv_vacation_begindate
											.setText(datePicker.getYear()
													+ "-"
													+ datePicker.getMonth()
													+ "-"
													+ datePicker.getDay());
											String sEndDate = tv_vacation_enddate
													.getText().toString();
											if (!"".equalsIgnoreCase(sEndDate)) {
												String sBeginDate = tv_vacation_begindate
														.getText().toString();
												SimpleDateFormat sdf = new SimpleDateFormat(
														"yyyy-MM-dd");
												try {
													Date dBegindate = sdf.parse(sBeginDate);
													Date dEnddate = sdf.parse(sEndDate);
													int iDays = (int) ((dEnddate.getTime() - 
															dBegindate.getTime()) / 1000 / 60 / 60 / 24) + 1;
													if (iDays <= 0) {
														showAlertDialog(
																"提示", "结束日期不能小于开始日期，请重新选择!",
																new View.OnClickListener() {
																	@Override
																	public void onClick(
																			View arg0) {
																		alertDialog.cancel();
																	}
																}, "确定", null, null);
														tv_vacation_enddate
														.requestFocus();
													} else {
														tv_vacation_days.setText(""
																+ iDays + " 天");
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
									}, tv_vacation_begindate.getText().toString());
							datePicker.show();
						}
					}
				});

//		tv_vacation_enddate.setText(new SimpleDateFormat("yyyy-MM-dd")
//				.format(new Date()));
		
		tv_vacation_enddate.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					datePicker = new CustomDatePicker(VacationSubmitActivity.this,
							"请输入休假结束时间", new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							tv_vacation_enddate.setText(datePicker
									.getYear()
									+ "-"
									+ datePicker.getMonth()
									+ "-"
									+ datePicker.getDay());
							String sBeginDate = tv_vacation_begindate
									.getText().toString();
							if (!"".equalsIgnoreCase(sBeginDate)) {
								String sEndDate = tv_vacation_enddate
										.getText().toString();
								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyy-MM-dd");
								try {
									Date dBegindate = sdf.parse(sBeginDate);
									Date dEnddate = sdf.parse(sEndDate);
									int iDays = (int) ((dEnddate.getTime() - dBegindate
											.getTime()) / 1000 / 60 / 60 / 24) + 1;
									if (iDays <= 0) {
										showAlertDialog("提示", "结束日期不能小于开始日期，请重新选择!",
												new View.OnClickListener() {
											@Override
											public void onClick(View arg0) {
												alertDialog.cancel();
											}
										}, "确定", null, null);
										tv_vacation_enddate.requestFocus();
									} else {
										tv_vacation_days.setText("" + iDays + " 天");
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
					}, tv_vacation_enddate.getText().toString());
					datePicker.show();
				}
			}
		});
		tv_vacation_days.setText("1 天");
		tv_titlebar_title.requestFocus();
	}

	private void sendSubmit() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid",XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("type", XmlValueUtil.encodeString(type));
            params.put("desc", XmlValueUtil.encodeString(et_vacation_reason.getText()
                    .toString()));
            String d = tv_vacation_days.getText().toString();
            String sDays = d.substring(0, d.length() - 2);
            params.put("days", XmlValueUtil.encodeString(sDays) );
            params.put("begindate", XmlValueUtil.encodeString(tv_vacation_begindate.getText()
                    .toString()));
            params.put("enddate", XmlValueUtil.encodeString(tv_vacation_enddate.getText()
                    .toString()));
            params.put("corpvacationid", "");
            params.put("audituserid", auditid);
            request("vacation!submit?code=" + Constant.VACATION_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private boolean validityInput() {
		String stype = tv_vacation_type.getText().toString();
		if ("".equalsIgnoreCase(stype)) {
			showAlertDialog("提示", "休假类型不能为空,请选择休假类型!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_vacation_type.requestFocus();
			return false;
		}
		String sBeginDate = tv_vacation_begindate.getText().toString();
		if ("".equalsIgnoreCase(sBeginDate)) {
			showAlertDialog("提示", "开始日期不能为空,请选择开始日期!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_vacation_begindate.requestFocus();
			return false;
		}
		String sEndDate = tv_vacation_enddate.getText().toString();
		if ("".equalsIgnoreCase(sEndDate)) {
			showAlertDialog("提示", "结束日期不能为空,请选择结束日期!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_vacation_enddate.requestFocus();
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
			tv_vacation_enddate.requestFocus();
			return false;
		}
		String d = tv_vacation_days.getText().toString();
		String sDays = d.substring(0, d.length() - 2);
		if ("".equalsIgnoreCase(sDays)) {
			showAlertDialog("提示", "请假天数不能为空,请输入请假天数!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_vacation_days.requestFocus();
			return false;
		}

		if (Double.valueOf(sDays) > 999) {
			showAlertDialog("提示", "最长请假天数不能超过999天,请重新输入请假天数!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_vacation_days.requestFocus();
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
			tv_vacation_man.requestFocus();
			return false;
		}
		String sReason = et_vacation_reason.getText().toString();
		if ("".equalsIgnoreCase(sReason)) {
			showAlertDialog("提示", "请假缘由不能为空,请输入请假缘由!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_vacation_reason.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public void onRecvData(XmlNode response) {
//		if (response != null) {
//			String functionno = response.getText("root.functionno");
//			if (functionno.equalsIgnoreCase(Constant.VACATION_SUBMIT)) {
//				Toast.makeText(this, "请假申请上报成功!", Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent();
//				intent.setAction(Constant.VACATION_ADD_ACTION);
//				sendBroadcast(intent);
//				finish();
//			}
//		}
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
			}
		}
		tv_vacation_man.setHint("请选择审批人");
		tv_vacation_man.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (auditManDialog == null) {
						auditManDialog = new CustomPopupDialog(
								VacationSubmitActivity.this, "审批人", lsName,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_vacation_man.setText(((TextView) view)
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
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.VACATION_SUBMIT.equals(code)) {
                Toast.makeText(this, "请假申请上报成功!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Constant.VACATION_ADD_ACTION);
                sendBroadcast(intent);
                finish();
            }
        }
    }
}
