package com.zjrc.isale.client.ui.activity;

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
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;
import com.zjrc.isale.client.ui.widgets.CustomDatePicker;
import com.zjrc.isale.client.ui.widgets.CustomPopupDialog;
import com.zjrc.isale.client.util.xml.XmlNode;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：汇报筛选
 */
public class WorkreportSearchActivity extends BaseActivity {
	private Button btn_titlebar_back;
	private Button search;

	private EditText et_workreport_name;// 网点
	private TextView tv_workreport_begin_date;// 任务开始时间
	private TextView tv_workreport_type;

	private TextView tv_titlebar_title;
	private String[] workreporttype;

	private int type=-1;
	private CustomPopupDialog workreportTypeDialog = null;
	// btn_terminalcodeselect.setOnClickListener(new View.OnClickListener()
	// {
	// @Override
	// public void onClick(View arg0) {
	// Intent openCameraIntent = new Intent(workreportListActivity.this,
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
	// bundle.putString("workreportid", "");
	// bundle.putString("terminalid","");
	// bundle.putString("terminalname","");
	// intent.putExtras(bundle);
	// intent.setClass(workreportListActivity.this,
	// workreportSubmitActivity.class);
	// startActivityForResult(intent, Constant.workreport_ADD);
	// }
	// });

	// et_workreportdate.setOnTouchListener(new View.OnTouchListener() {
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
	CustomDatePicker datePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.workreport_search);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.work_report_search);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);

		btn_titlebar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		et_workreport_name = (EditText) findViewById(R.id.et_workreport_name);

		tv_workreport_begin_date = (TextView) findViewById(R.id.tv_workreport_begin_date);
		tv_workreport_begin_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				datePicker = new CustomDatePicker(
						WorkreportSearchActivity.this, "请输入汇报时间",
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								tv_workreport_begin_date.setText(datePicker
										.getYear()
										+ "-"
										+ datePicker.getMonth()
										+ "-"
										+ datePicker.getDay());
								datePicker.dismiss();

							}
						}, new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								datePicker.dismiss();
							}
						}, tv_workreport_begin_date.getText().toString());
				datePicker.show();

			}
		});

        String[] temp = getResources().getStringArray(R.array.workreporttypes);
        workreporttype = new String[temp.length + 1];
        workreporttype[0] = "全部";
        System.arraycopy(temp, 0, workreporttype, 1, temp.length);
		tv_workreport_type = (TextView) findViewById(R.id.tv_workreport_type);
		tv_workreport_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (workreportTypeDialog == null) {
					workreportTypeDialog = new CustomPopupDialog(
							WorkreportSearchActivity.this, "汇报类型",
							workreporttype, new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									tv_workreport_type
											.setText(((TextView) view)
													.getText());
									workreportTypeDialog.dismiss();
									type = position;
								}
							});
				}
				workreportTypeDialog.show();
			}
		});

		search = (Button) findViewById(R.id.btn_check);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!et_workreport_name.getText().equals("")
						|| !tv_workreport_begin_date.getText().equals("")
						|| !tv_workreport_type.getText().equals("")) {
					Intent intent = getIntent();
					intent.putExtra("title", et_workreport_name.getText()
							.toString());
					intent.putExtra("type", type);
					intent.putExtra("begindate", tv_workreport_begin_date
							.getText().toString());
					setResult(Constant.RESULT_WORKREPORT_SEARCH, intent);
					finish();
				} else {
					alertDialog = new CustomAlertDialog(
							WorkreportSearchActivity.this);
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
		});

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            et_workreport_name.setText(bundle.getString("title"));
//            type = bundle.getInt("typeindex", 0);
//            tv_workreport_type.setText(workreporttype[type]);
//            tv_workreport_begin_date.setText(bundle.getString("begindate"));
//        }
        type = 0;
        tv_workreport_type.setText(workreporttype[type]);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	public void onRecvData(XmlNode response) {

	}
    @Override
    public void onRecvData(JsonObject response) {

    }

}
