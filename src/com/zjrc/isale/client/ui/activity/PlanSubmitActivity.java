package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：拜访计划新增修改界面
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
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
import java.util.Map;

public class PlanSubmitActivity extends BaseActivity {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private Button btn_ok;

	private String operate;

	private String id;

	private String terminalid;

	private String terminalcode;

	private String terminalname;

	private TextView tv_plan_date;;

	private EditText et_plan_reason;

	private TextView tv_plan_man;

	private TextView tv_plan_type;

	private String auditid = "";

	private TextView tv_plan_customer;
	private CustomPopupDialog auditManDialog = null;
	private CustomPopupDialog planTypeDialog;
	private String[] lsName;
	private String[] types = { "常规", "临时" };
	private int type = 2;// 1常规/0临时
	ArrayList<Audit> areas=new ArrayList<Audit>();
	CustomDatePicker datePicker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.plan_submit);
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
		tv_plan_man = (TextView) findViewById(R.id.tv_plan_man);
		et_plan_reason = (EditText) findViewById(R.id.et_plan_reason);
		tv_plan_date = (TextView) findViewById(R.id.tv_plan_date);
		tv_plan_type = (TextView) findViewById(R.id.tv_plan_type);
		tv_plan_date.setText(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date()));

		tv_plan_date.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					datePicker = new CustomDatePicker(PlanSubmitActivity.this,
							"请输入拜访时间", new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									tv_plan_date.setText(datePicker.getYear()
											+ "-" + datePicker.getMonth() + "-"
											+ datePicker.getDay());
									datePicker.dismiss();

								}
							}, new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									datePicker.dismiss();
								}
							}, tv_plan_date.getText().toString());
					datePicker.show();

					break;
				default:
					break;
				}
				return false;
			}
		});
		tv_plan_customer = (TextView) findViewById(R.id.tv_plan_customer);
		btn_ok = (Button) findViewById(R.id.btn_check);

        if (!needAudit) {
            findViewById(R.id.rl_plan_man).setVisibility(View.GONE);
        }

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

		tv_plan_customer.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("terminalid", terminalid);
					intent.putExtras(bundle);
					intent.setClass(PlanSubmitActivity.this,
							TerminalSelectActivity.class);
					startActivityForResult(intent, Constant.RESULT_TEMINAL_SELECT);
				}
			}
		});
		tv_plan_type.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (planTypeDialog == null) {
						planTypeDialog = new CustomPopupDialog(
								PlanSubmitActivity.this, "任务类型", types,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_plan_type.setText(((TextView) view)
												.getText());
										planTypeDialog.dismiss();
										if (position == 1) {
											type = 0;
										} else {
											type = 1;
										}
									}
								});
					}
					planTypeDialog.show();
				}
			}
		});

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		operate = bundle.getString("operate");

		if (operate.equalsIgnoreCase("insert")) {
			id = "";
			terminalid = "";
			terminalname = "";
			tv_titlebar_title.setText(R.string.plan_submit);

			tv_plan_customer.setOnClickListener(new TextView.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (CommonUtils.isNotFastDoubleClick()) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("terminalid", terminalid);
						intent.putExtras(bundle);
						intent.setClass(PlanSubmitActivity.this,
								TerminalSelectActivity.class);
						startActivityForResult(intent,
								Constant.RESULT_TEMINAL_SELECT);
					}
				}
			});

		} else if (operate.equalsIgnoreCase("modify")) {
			id = bundle.getString("planid");
			tv_titlebar_title.setText(R.string.plan_modify);
			sendQuery();
		}
        if (needAudit) {
            addAuditList();
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.BARCODE_SCAN) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				String scanResult = bundle.getString("result");
				if (scanResult != null) {
					terminalcode = scanResult;
					sendQueryTerminalDetail(terminalcode);
				}
			}
		} else if (requestCode == Constant.RESULT_TEMINAL_SELECT) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				terminalid = bundle.getString("terminalid");
				terminalname = bundle.getString("terminalname");
				tv_plan_customer.setText(terminalname);
			}
		}
	}

	private void sendQueryTerminalDetail(String code) {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null && application.getConfig() != null
				&& application.getConfig().getUserid() != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("terminalid", "");
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("terminalcode", code);
            request("terminal!detail?code=" + Constant.TERMINAL_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	private void sendQuery() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("planid", id);
        request("plan!detail?code=" + Constant.PLAN_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	private void sendSubmit() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("operate", XmlValueUtil.encodeString(operate));
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("terminalid", XmlValueUtil.encodeString(terminalid));
            params.put("plandate", XmlValueUtil.encodeString(tv_plan_date.getText()
                    .toString()));
            params.put("plancontent", XmlValueUtil.encodeString(et_plan_reason.getText()
                    .toString()) );
            params.put("planstate", "0");
            params.put("plancomment", "");
            params.put("planid",  XmlValueUtil.encodeString(id));
            params.put("audituserid",auditid);
            params.put("plantype",type+"");
            request("plan!submit?code=" + Constant.PLAN_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private boolean validityInput() {
		String sDate = tv_plan_date.getText().toString();
		if ("".equalsIgnoreCase(sDate)) {
			showAlertDialog("提示", "拜访日期不能为空,请输入拜访日期!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_plan_date.requestFocus();
			return false;
		}
		if (terminalid != null && terminalid.equalsIgnoreCase("")) {
			showAlertDialog("提示", "拜访网点不能为空,请选择拜访网点!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_plan_man.requestFocus();
			return false;
		}
		if (type != 1 && type != 0) {
			showAlertDialog("提示", "任务类型不能为空,请选择任务类型!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_plan_type.requestFocus();
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
			tv_plan_man.requestFocus();
			return false;
		}
		String sContent = et_plan_reason.getText().toString();
		if ("".equalsIgnoreCase(sContent)) {
			showAlertDialog("提示", "拜访内容不能为空,请输入拜访内容!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_plan_reason.requestFocus();
			return false;
		}

		return true;
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

	private void addAuditList() {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null && application.getConfig().getNeedaudit()) {
			areas = application.getAudit();
			if (areas != null) {
				lsName = new String[areas.size()];
				for (int i = 0; i < areas.size(); i++) {
					lsName[i] = areas.get(i).getUsername();
				}
			}
		}
		tv_plan_man.setHint("请选择审批人");
		tv_plan_man.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (auditManDialog == null) {
						auditManDialog = new CustomPopupDialog(
								PlanSubmitActivity.this, "审批人", lsName,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_plan_man.setText(((TextView) view)
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

	private void selectAudit(String audituserid) {
		for (int j = 0; j < areas.size(); j++) {
			if (audituserid.compareTo(areas.get(j).getUserid()) == 0) {
				tv_plan_man.setText(lsName[j]);
				auditid = audituserid;
				break;
			}
		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.PLAN_QUERY.equals(code)) {
                if (operate.equalsIgnoreCase("modify")) {
                    JsonObject plan = response.getAsJsonObject("body");
                    tv_plan_date.setText(plan.get("plandate").getAsString());
                    terminalid =plan.get("terminalid").getAsString();
                    terminalname =plan.get("terminalname").getAsString();
                    type = Integer.valueOf(plan.get("plantype").getAsString());
                    if (type == 1) {
                        tv_plan_type.setText("常规");
                    } else {
                        tv_plan_type.setText("临时");
                    }
                    tv_plan_customer.setText(terminalname);
                    et_plan_reason.setText(plan.get("plancontent").getAsString());
                    if (needAudit) {
                        String audituserid = plan.get("audituserid").getAsString();
                        if (audituserid != null && !audituserid.equals("")) {
                            selectAudit(audituserid);
                        }
                    }
                }
            } else if(Constant.PLAN_SUBMIT.equals(code)){
            	Intent intent = new Intent();
            	if (operate.equalsIgnoreCase("insert")) {
                    Toast.makeText(this, "计划上报成功!", Toast.LENGTH_SHORT).show();
                    intent.setAction(Constant.PLAN_ADD_ACTION);
                } else if (operate.equalsIgnoreCase("modify")) {
                    Toast.makeText(this, "计划修改成功!", Toast.LENGTH_SHORT).show();
                    intent.setAction(Constant.PLAN_MODIFY_ACTION);
                }
                sendBroadcast(intent);
                finish();
            } else if (Constant.TERMINAL_DETAIL.equals(code)) {
            	JsonObject backorder = response.getAsJsonObject("body");
                if(backorder != null) {
                    terminalid = backorder.get("id").getAsString();
    				terminalname = backorder.get("name").getAsString();
    				tv_plan_customer.setText(terminalname);
                }
            }
        }
    }
}
