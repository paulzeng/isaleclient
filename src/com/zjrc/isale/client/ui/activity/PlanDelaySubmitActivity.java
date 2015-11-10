package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.widgets.CustomPopupDialog;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：拜访计划推迟
 */
public class PlanDelaySubmitActivity extends BaseActivity {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private TextView tv_delay_type;
	private EditText et_delay_content;

	private RelativeLayout rl_submit_msg;
	private RelativeLayout rl_submit_info;
	private RelativeLayout rl_audit_msg;
	private RelativeLayout rl_audit_info;

	private TextView tv_terminal;
	private TextView tv_date;
	private TextView tv_content;
	private TextView tv_state;
	private TextView tv_result;

	private Button btn_ok;

	private String planid;
	private String[] types;
	private int type;
	CustomPopupDialog plandelaystatesTypeDialog;

	private boolean isSubmitInfoVisible = false;
	private boolean isAuditInfoVisible = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.plandelay_submit);
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

		btn_ok = (Button) findViewById(R.id.btn_check);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		planid = bundle.getString("planid");
		types = getResources().getStringArray(R.array.planstates);

		rl_submit_msg = (RelativeLayout) findViewById(R.id.rl_submit_msg);
		rl_submit_info = (RelativeLayout) findViewById(R.id.rl_submit_info);
		rl_audit_msg = (RelativeLayout) findViewById(R.id.rl_audit_msg);
		rl_audit_info = (RelativeLayout) findViewById(R.id.rl_audit_info);
		et_delay_content = (EditText) findViewById(R.id.et_plan_delay_content);
		tv_terminal = (TextView) findViewById(R.id.tv_terminal);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_state = (TextView) findViewById(R.id.tv_state_2);
		tv_result = (TextView) findViewById(R.id.tv_result);

		tv_titlebar_title.setText(R.string.plandelay_submit);
		tv_delay_type = (TextView) findViewById(R.id.tv_delay_type);
		rl_submit_msg.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (!isSubmitInfoVisible) {
						rl_submit_info.setVisibility(View.VISIBLE);
						isSubmitInfoVisible = true;
					} else {
						rl_submit_info.setVisibility(View.GONE);
						isSubmitInfoVisible = false;
					}
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
                	if (CommonUtils.isNotFastDoubleClick()) {
                		if (!isAuditInfoVisible) {
                			rl_audit_info.setVisibility(View.VISIBLE);
                			isAuditInfoVisible = true;
                		} else {
                			rl_audit_info.setVisibility(View.GONE);
                			isAuditInfoVisible = false;
                		}
                	}
                }
            });
        }

		sendQuery();

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
		tv_delay_type.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (plandelaystatesTypeDialog == null) {
						plandelaystatesTypeDialog = new CustomPopupDialog(
								PlanDelaySubmitActivity.this, "取消类型", types,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_delay_type.setText(((TextView) view)
												.getText());
										plandelaystatesTypeDialog.dismiss();
										type = position + 2;
									}
								});
					}
					plandelaystatesTypeDialog.show();
				}

			}
		});
	}

	private void sendQuery() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("planid", XmlValueUtil.encodeString(planid));
        request("plan!detail?code=" + Constant.PLAN_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	private void sendSubmit() {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("operate", "delay");
        params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                .getUserid()));
        params.put("terminalid", "");
        params.put("plandate", "");
        params.put("plancontent", "");
        params.put("planstate", type+"");
        params.put("plancomment", XmlValueUtil.encodeString(et_delay_content.getText()
                .toString()));
        params.put("planid", XmlValueUtil.encodeString(planid));
        request("plan!submit?code=" + Constant.PLAN_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private boolean validityInput() {
		String sDate = et_delay_content.getText().toString();
		if ("".equalsIgnoreCase(sDate)) {
			showAlertDialog("提示", "取消原因不能为空,请输入取消原因!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_delay_content.requestFocus();
			return false;
		}
		if (type == 0) {
			showAlertDialog("提示", "延期类型不能为空,请延期类型!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_delay_type.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public void onRecvData(XmlNode response) {
		if (response != null) {
			String functionno = response.getText("root.functionno");

		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.PLAN_QUERY.equals(code)) {
                JsonObject plan = response.getAsJsonObject("body");
                String signState =plan.get("signstate").getAsString();
                if (signState.equalsIgnoreCase("0")) {
                    tv_state.setText("未审批");
                } else {
                    tv_state.setText("已审批");
                    tv_result.setText(plan.get("signcontent").getAsString());
                }
                tv_date.setText(plan.get("plandate").getAsString());
                tv_terminal.setText(plan.get("terminalname").getAsString());
                tv_content.setText(plan.get("plancontent").getAsString());
            }else if(Constant.PLAN_SUBMIT.equals(code)){
                    Toast.makeText(this, "拜访计划取消成功!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Constant.PLAN_DELAY_ACTION);
                    sendBroadcast(intent);
                    finish();
            }
        }
    }
}
