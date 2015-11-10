package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：新增工作汇报界面
 */

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
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.widgets.CustomDatePicker;
import com.zjrc.isale.client.ui.widgets.CustomPopupDialog;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkReportSubmitActivity extends BaseActivity {
	private TextView tv_titlebar_title;
	private Button btn_titlebar_back;

	private EditText et_title;

	private TextView tv_type;

	private TextView tv_begindate;

	private TextView tv_enddate;

	private EditText et_this_cycle;

	private EditText et_next_cycle;

	private Button btn_ok;

	private String[] types;
	CustomDatePicker datePicker;

	private int type = 10;
	private CustomPopupDialog workreport_typeDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.workreport_submit);
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

		tv_titlebar_title.setText(R.string.work_report);

		tv_type = (TextView) findViewById(R.id.tv_workreport_type);

		et_title = (EditText) findViewById(R.id.tv_workreport_title);

		tv_begindate = (TextView) findViewById(R.id.tv_workreport_begindate);

		tv_enddate = (TextView) findViewById(R.id.tv_workreport_enddate);

		et_this_cycle = (EditText) findViewById(R.id.et_this_cycle);

		et_next_cycle = (EditText) findViewById(R.id.et_next_cycle);

		btn_ok = (Button) findViewById(R.id.btn_check);

		types = getResources().getStringArray(R.array.workreporttypes);
		tv_type.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (workreport_typeDialog == null) {
						workreport_typeDialog = new CustomPopupDialog(
								WorkReportSubmitActivity.this, "汇报类型", types,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_type.setText(((TextView) view).getText());
										workreport_typeDialog.dismiss();
										type = position;
									}
								});
					}
					workreport_typeDialog.show();
				}
			}
		});

		tv_begindate.setText(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date()));

		tv_begindate.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					datePicker = new CustomDatePicker(
							WorkReportSubmitActivity.this, "请选择开始时间",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										tv_begindate.setText(datePicker.getYear()
												+ "-" + datePicker.getMonth() + "-"
												+ datePicker.getDay());
										String sEndDate = tv_enddate.getText()
												.toString();
										if (!"".equalsIgnoreCase(sEndDate)) {
											String sBeginDate = tv_begindate
													.getText().toString();
											SimpleDateFormat sdf = new SimpleDateFormat(
													"yyyy-MM-dd");
											try {
												Date dBegindate = sdf
														.parse(sBeginDate);
												Date dEnddate = sdf.parse(sEndDate);
												int iDays = (int) ((dEnddate.getTime() - 
														dBegindate.getTime()) / 1000 / 60 / 60 / 24) + 1;
												if (iDays <= 0) {
													showAlertDialog(
															"提示", "结束日期不能小于开始日期，请重新选择!",
															new View.OnClickListener() {
																@Override
																public void onClick(View arg0) {
																	alertDialog.cancel();
																}
															}, "确定", null, null);
													tv_enddate.requestFocus();
												}
											} catch (Exception e) {
											}
										}
										datePicker.dismiss();
									}
								}
							}, new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									datePicker.dismiss();
								}
							}, tv_begindate.getText().toString());
					datePicker.show();

					break;
				default:
					break;
				}
				return false;
			}
		});

		tv_enddate.setText(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date()));

		tv_enddate.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					datePicker = new CustomDatePicker(
							WorkReportSubmitActivity.this, "请选择結束时间",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										tv_enddate.setText(datePicker.getYear()
												+ "-" + datePicker.getMonth() + "-"
												+ datePicker.getDay());
										String sBeginDate = tv_begindate.getText().toString();
										if (!"".equalsIgnoreCase(sBeginDate)) {
											String sEndDate = tv_enddate.getText().toString();
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
																public void onClick(View arg0) {
																	alertDialog.cancel();
																}
															}, "确定", null, null);
													tv_enddate.requestFocus();
												}
											} catch (Exception e) {
												
											}
										}
										datePicker.dismiss();
									}
								}
							}, new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									datePicker.dismiss();
								}
							}, tv_enddate.getText().toString());
					datePicker.show();

					break;
				default:
					break;
				}
				return false;
			}
		});

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

		tv_titlebar_title.requestFocus();
	}

	private void sendSubmit() {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            params.put("title", et_title.getText().toString());
            params.put("type", String.valueOf(type));
            params.put("begindate", tv_begindate.getText().toString());
            params.put("enddate", tv_enddate.getText().toString());
            params.put("content", et_this_cycle.getText().toString());
            params.put("plan", et_next_cycle.getText().toString());
            params.put("corpworkreportid", "");
            request("workReport!submit?code=" + Constant.WORKREPORT_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	private boolean validityInput() {
		if (type == 10) {
			showAlertDialog("提示", "汇报类型不能为空，请选择汇报类型",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_type.requestFocus();
			return false;
		}
		String sTitle = et_title.getText().toString();
		if ("".equalsIgnoreCase(sTitle)) {
			showAlertDialog("提示", "汇报标题不能为空,请输入汇报标题!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_title.requestFocus();
			return false;
		}
		String sBeginDate = tv_begindate.getText().toString();
		if ("".equalsIgnoreCase(sBeginDate)) {
			showAlertDialog("提示", "开始日期不能为空,请输入开始日期!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_begindate.requestFocus();
			return false;
		}
		String sEndDate = tv_enddate.getText().toString();
		if ("".equalsIgnoreCase(sEndDate)) {
			showAlertDialog("提示", "结束日期不能为空,请输入结束日期!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_enddate.requestFocus();
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
			tv_enddate.requestFocus();
			return false;
		}
		String sSummary = et_this_cycle.getText().toString();
		if ("".equalsIgnoreCase(sSummary)) {
			showAlertDialog("提示", "本期总结不能为空,请输入本期总结!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_this_cycle.requestFocus();
			return false;
		}
		String sPlan = et_next_cycle.getText().toString();
		if ("".equalsIgnoreCase(sPlan)) {
			showAlertDialog("提示", "下期计划不能为空,请输入下期计划!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_next_cycle.requestFocus();
			return false;
		}

		return true;
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.WORKREPORT_SUBMIT.equals(code)) {//工作汇报上报
            	Toast.makeText(this, "工作汇报上报成功!", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setAction(Constant.WORKREPORT_ADD_ACTION);
				sendBroadcast(intent);
				finish();
            }
        }
    }
}
