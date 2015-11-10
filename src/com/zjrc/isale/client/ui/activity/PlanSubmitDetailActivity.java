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
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：拜访申请详情
 */
public class PlanSubmitDetailActivity extends BaseActivity {

	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private TextView tv_title;
	private TextView tv_add_time;
	private TextView tv_state;
	private TextView tv_terminal;
	private TextView tv_time;
	private TextView tv_content;
	private TextView tv_man;
	private TextView tv_man_title;
	private TextView tv_type;
	private TextView tv_state2;
	private TextView tv_audit_time;
	private TextView tv_result;
	private TextView tv_audit_content;
	private TextView tv_audit_time_title;
	private TextView tv_result_title;
	private TextView tv_audit_content_title;
	private RelativeLayout ll_bottom;

	private Button ok;
	private Button cancel;
	private String id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.plan_submit_detail);
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
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_terminal = (TextView) findViewById(R.id.tv_terminal);
		tv_state2 = (TextView) findViewById(R.id.tv_state_2);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_man = (TextView) findViewById(R.id.tv_man);
		tv_audit_time = (TextView) findViewById(R.id.tv_audit_time);
		tv_audit_content = (TextView) findViewById(R.id.tv_audit_content);
		tv_result = (TextView) findViewById(R.id.tv_result);
		tv_man_title = (TextView) findViewById(R.id.tv_man_title);
		tv_audit_time_title = (TextView) findViewById(R.id.tv_audit_time_title);
		tv_audit_content_title = (TextView) findViewById(R.id.tv_audit_content_title);
		tv_result_title = (TextView) findViewById(R.id.tv_result_title);
		tv_type= (TextView) findViewById(R.id.tv_type);
		ok = (Button) findViewById(R.id.btn_ok);
		cancel = (Button) findViewById(R.id.btn_cancel);

		ll_bottom = (RelativeLayout) findViewById(R.id.ll_bottom);

		if (!needAudit) {
			findViewById(R.id.rl_state).setVisibility(View.GONE);
			findViewById(R.id.rl_man).setVisibility(View.GONE);
			findViewById(R.id.rl_audit_time).setVisibility(View.GONE);
			findViewById(R.id.rl_result).setVisibility(View.GONE);
			findViewById(R.id.rl_audit_content).setVisibility(View.GONE);
		}

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		tv_titlebar_title.setText(R.string.plan_detail);
		id = bundle.getString("planid");
		sendQuery();
		addReceiver();
	}

	private RefreshEventReceiver receiver;

	private void addReceiver() {
		receiver = new RefreshEventReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.PLAN_MODIFY_ACTION);
		filter.addAction(Constant.PLAN_EXECUTION_ACTION);
		filter.addAction(Constant.PLAN_DELAY_ACTION);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

		private void sendQuery() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("planid", id);
        request("plan!detail?code=" + Constant.PLAN_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
    }

    public void requestDeletePlan(String id) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("operate", "delete");
            params.put("userid",XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("terminalid","");
            params.put("plandate", "");
            params.put("plancontent", "");
            params.put("planstate", "0");
            params.put("plancomment", "");
            params.put("planid", XmlValueUtil.encodeString(id));
            request("plan!submit?code=" + Constant.PLAN_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	@Override
	public void onRecvData(final XmlNode response) {
		if (response != null) {
			String functionno = response.getText("root.functionno");

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class RefreshEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Constant.PLAN_MODIFY_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_EXECUTION_ACTION)) {
				finish();
			} else if (action.equalsIgnoreCase(Constant.PLAN_DELAY_ACTION)) {
				Intent intent2 = new Intent();
				intent2.setClass(PlanSubmitDetailActivity.this,
						PlanDelayDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("planid", id);
				intent2.putExtras(bundle);
				startActivity(intent2);
				finish();
			}
		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.PLAN_QUERY.equals(code)) {
                JsonObject plan = response.getAsJsonObject("body");
                String signState = plan.get("signstate").getAsString();
                String signResult =plan.get("signresult").getAsString();
                String state =plan.get("planstate").getAsString();
                String plantype =plan.get("plantype").getAsString();
                if (plantype.equalsIgnoreCase("0")) {// 临时任务
                    tv_type.setText("临时任务");
                    if (state.equalsIgnoreCase("0")) {// 未执行
                        tv_state.setText("未执行");
                        tv_state2.setText("未执行");
                        ok.setText("执行");
                        cancel.setText("取消拜访");
                        ll_bottom.setVisibility(View.VISIBLE);
                        ok.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if (CommonUtils.isNotFastDoubleClick()) {
                                    Intent intent = new Intent();
                                    intent.setClass(PlanSubmitDetailActivity.this,
                                            PlanPatrolSubmitActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("planid", id);
                                    intent.putExtras(bundle);
                                    startActivityForResult(intent,
                                            Constant.PLANPATROL_SUBMIT);
                                }
                            }
                        });

                        cancel.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if (CommonUtils.isNotFastDoubleClick()) {
                                    Intent intent = new Intent();
                                    intent.setClass(PlanSubmitDetailActivity.this,
                                            PlanDelaySubmitActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("operate", "submit");
                                    bundle.putString("planid", id);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        });
                    } else {// 已执行
                        ll_bottom.setVisibility(View.GONE);
                        tv_state.setText("已执行");
                    }

                    if (signState.equalsIgnoreCase("0")) {// 待审批
                        tv_state2.setText("未审批");
                        tv_man.setVisibility(View.GONE);
                        tv_man_title.setVisibility(View.GONE);
                        tv_audit_time.setVisibility(View.GONE);
                        tv_audit_content.setVisibility(View.GONE);
                        tv_result.setVisibility(View.GONE);
                        tv_audit_time_title.setVisibility(View.GONE);
                        tv_audit_content_title.setVisibility(View.GONE);
                        tv_result_title.setVisibility(View.GONE);
                    } else {// 已审批

                        tv_state2.setText("已审批");
                        if (plan.get("signresult").getAsString().equals("1")) {
                            tv_result.setText("同意");
                        } else {
                            tv_result.setText("不同意");
                        }

                        tv_man.setText(plan.get("signuser").getAsString());
                        String signDate =plan.get("signdate").getAsString() ;
                        if (signDate!=null&&!signDate.equalsIgnoreCase("")) {
                            String []ds= signDate.split(" ");
                            tv_audit_time.setText(ds[0]);
                        }
                        tv_audit_content.setText(plan.get("signcontent").getAsString());
                    }

                } else {// 常规任务
                    tv_type.setText("常规任务");
                    if (signState.equalsIgnoreCase("0")) {// 待审批
                        tv_state.setText("未审批");
                        tv_state2.setText("未审批");
                        tv_man.setVisibility(View.GONE);
                        tv_man_title.setVisibility(View.GONE);
                        tv_audit_time.setVisibility(View.GONE);
                        tv_audit_content.setVisibility(View.GONE);
                        tv_result.setVisibility(View.GONE);
                        tv_audit_time_title.setVisibility(View.GONE);
                        tv_audit_content_title.setVisibility(View.GONE);
                        tv_result_title.setVisibility(View.GONE);
                        ll_bottom.setVisibility(View.VISIBLE);
                        ok.setText("修改");
                        cancel.setText("删除");

                        ok.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if (CommonUtils.isNotFastDoubleClick()) {
                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("operate", "modify");
                                    bundle.putString("planid", id);
                                    intent.putExtras(bundle);
                                    intent.setClass(PlanSubmitDetailActivity.this,
                                            PlanSubmitActivity.class);
                                    startActivityForResult(intent,
                                            Constant.PLAN_ADD);
                                }
                            }
                        });
                        cancel.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if (CommonUtils.isNotFastDoubleClick()) {
                                    showAlertDialog("提示", "确认删除此任务吗？",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View arg0) {
                                                    requestDeletePlan(id);
                                                    alertDialog.cancel();
                                                }
                                            }, "确认", "取消",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View arg0) {
                                                    alertDialog.cancel();
                                                }
                                            });
                                }
                            }
                        });

                    } else if (signState.equalsIgnoreCase("1")) {// 已审核
                        tv_state2.setText("已审批");
                        if (signResult.equalsIgnoreCase("1")) {// 审核通过
                            if (state.equalsIgnoreCase("0")) {// 未执行
                                tv_state.setText("未执行");
                                if (plan.get("signresult").getAsString()
                                        .equals("1")) {
                                    tv_result.setText("同意");
                                } else {
                                    tv_result.setText("不同意");
                                }
                                ok.setText("执行");
                                cancel.setText("取消拜访");
                                ll_bottom.setVisibility(View.VISIBLE);
                                ok.setOnClickListener(new Button.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        if (CommonUtils.isNotFastDoubleClick()) {
                                            Intent intent = new Intent();
                                            intent.setClass(
                                                    PlanSubmitDetailActivity.this,
                                                    PlanPatrolSubmitActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("planid", id);
                                            intent.putExtras(bundle);
                                            startActivityForResult(intent,
                                                    Constant.PLANPATROL_SUBMIT);
                                        }
                                    }
                                });

                                cancel.setOnClickListener(new Button.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        if (CommonUtils.isNotFastDoubleClick()) {
                                            Intent intent = new Intent();
                                            intent.setClass(
                                                    PlanSubmitDetailActivity.this,
                                                    PlanDelaySubmitActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("operate", "submit");
                                            bundle.putString("planid", id);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            } else if (state.equalsIgnoreCase("1")) {// 已执行

                            } else if (state.equalsIgnoreCase("2")) {// 已推迟

                            } else if (state.equalsIgnoreCase("3")) {

                            }
                        } else {// 审核未通过
                            tv_state.setText("不同意");
                            tv_state2.setText("已审批");
                            tv_result.setText("不同意");
                            ll_bottom.setVisibility(View.GONE);
                        }
                        tv_man.setText(plan.get("signuser").getAsString());
                        String signDate =plan.get("signdate").getAsString();
                        String []ds= signDate.split(" ");
                        tv_audit_time.setText(ds[0]);
                        if (plan.get("signresult").getAsString().equals("1")) {
                            tv_result.setText("同意");
                        } else {
                            tv_result.setText("不同意");
                        }
                        tv_audit_content.setText(plan.get("signcontent").getAsString());

                    }
                }
                tv_title.setText(plan.get("terminalname").getAsString());
                tv_add_time.setText(plan.get("plandate").getAsString()
                        + "申请");
                tv_time.setText(plan.get("plandate").getAsString() );
                tv_terminal.setText(plan.get("terminalname").getAsString() );
                tv_content.setText(plan.get("plancontent").getAsString() );
            }else if(Constant.PLAN_SUBMIT.equals(code)){
                    Toast.makeText(PlanSubmitDetailActivity.this, "拜访任务删除成功!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Constant.PLAN_DELETE_ACTION);
                    sendBroadcast(intent);
                    finish();
            }
        }
    }
}
