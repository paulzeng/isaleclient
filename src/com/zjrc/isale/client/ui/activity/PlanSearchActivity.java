package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;
import com.zjrc.isale.client.ui.widgets.CustomDatePicker;
import com.zjrc.isale.client.ui.widgets.CustomPopupDialog;
import com.zjrc.isale.client.util.xml.XmlNode;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：拜访筛选
 */
public class PlanSearchActivity extends BaseActivity {
	private Button btn_titlebar_back;
	private Button search;
	private TextView tv_plan_status;
	private EditText tv_plan_customer;// 网点
	private TextView tv_plan_date;// 任务时间
	private TextView tv_plan_type;
	private TextView tv_titlebar_title;
	private String[] planstatus;
	private String[] plantype;

	private int index;
	private int type;
	private CustomPopupDialog planStatusTypeDialog = null;
	private CustomPopupDialog planTypeDialog = null;

	private CustomAlertDialog alertDialog = null;
	private String terminalid = "";
	private String terminalname = "";

	// btn_terminalcodeselect.setOnClickListener(new View.OnClickListener()
	// {
	// @Override
	// public void onClick(View arg0) {
	// Intent openCameraIntent = new Intent(PlanListActivity.this,
	// CaptureActivity.class);
	// startActivityForResult(openCameraIntent, Constant.BARCODE_SCAN);
	// }
	// });
	//
	// btn_add.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// Intent intent = new Intent();
	// Bundle bundle = new Bundle();
	// bundle.putString("operate", "insert");
	// bundle.putString("planid", "");
	// bundle.putString("terminalid","");
	// bundle.putString("terminalname","");
	// intent.putExtras(bundle);
	// intent.setClass(PlanListActivity.this, PlanSubmitActivity.class);
	// startActivityForResult(intent, Constant.PLAN_ADD);
	// }
	// });

	// et_plandate.setOnTouchListener(new View.OnTouchListener() {
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:

	// break;
	// default:
	// break;
	// }
	// return false;
	// }
	// });
	private CustomDatePicker datePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.plan_search);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.plan_search);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);

		btn_titlebar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		planstatus = getResources().getStringArray(R.array.planstatus);
		tv_plan_status = (TextView) findViewById(R.id.tv_plan_status);
		tv_plan_status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (planStatusTypeDialog == null) {
						planStatusTypeDialog = new CustomPopupDialog(
								PlanSearchActivity.this, "拜访状态", planstatus,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_plan_status.setText(((TextView) view)
												.getText());
										planStatusTypeDialog.dismiss();
										index = position;
									}
								});
					}
					planStatusTypeDialog.show();
				}
			}
		});

		tv_plan_customer = (EditText) findViewById(R.id.tv_plan_customer);

		tv_plan_date = (TextView) findViewById(R.id.tv_plan_date);
		tv_plan_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					datePicker = new CustomDatePicker(PlanSearchActivity.this,
							"请选择拜访时间", new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							tv_plan_date.setText(datePicker.getYear() + "-"
									+ datePicker.getMonth() + "-"
									+ datePicker.getDay());
							datePicker.dismiss();
							
						}
					}, new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							datePicker.dismiss();
						}
					},tv_plan_date.getText().toString());
					datePicker.show();
				}

			}
		});

		plantype = getResources().getStringArray(R.array.plantype);
		tv_plan_type = (TextView) findViewById(R.id.tv_plan_type);
		tv_plan_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (planTypeDialog == null) {
						planTypeDialog = new CustomPopupDialog(
								PlanSearchActivity.this, "拜访类型", plantype,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_plan_type.setText(((TextView) view)
												.getText());
										planTypeDialog.dismiss();
										type = position;
									}
								});
					}
					planTypeDialog.show();
				}
			}
		});

		search = (Button) findViewById(R.id.btn_check);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (!tv_plan_status.getText().equals("")
							|| !tv_plan_customer.getText().equals("")
							|| !tv_plan_date.getText().equals("")
							|| !tv_plan_type.getText().equals("")) {
						Intent intent = getIntent();
						intent.putExtra("planstatus", index);
						intent.putExtra("type", type);
						intent.putExtra("date", tv_plan_date.getText().toString());
						intent.putExtra("terminalname", tv_plan_customer.getText()
								.toString());
						setResult(Constant.RESULT_PLAN_SEARCH, intent);
						finish();
					} else {
						alertDialog = new CustomAlertDialog(PlanSearchActivity.this);
						alertDialog.show();
						alertDialog.setTitle("提示");
						alertDialog.setMessage("请填选至少一种筛选项");
						alertDialog.setOnPositiveListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								alertDialog.dismiss();
							}
						});
						alertDialog.getBtn_negative().setVisibility(View.GONE);
					}
				}
			}
		});

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            terminalname = "";
            tv_plan_customer.setText(terminalname);
            tv_plan_date.setText("");
            type = 0;
            tv_plan_type.setText(plantype[type]);
            index = 0;
            tv_plan_status.setText(planstatus[index]);
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.RESULT_TEMINAL_SELECT) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				terminalid = bundle.getString("terminalid");
				terminalname = bundle.getString("terminalname");
				tv_plan_customer.setText(terminalname);
			}
		}
	}

	@Override
	public void onRecvData(XmlNode response) {

	}
    @Override
    public void onRecvData(JsonObject response) {

    }

}
