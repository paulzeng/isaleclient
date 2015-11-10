package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;
import com.zjrc.isale.client.ui.widgets.CustomPopupDialog;
import com.zjrc.isale.client.util.xml.XmlNode;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：休假筛选
 */
public class VacationSearchActivity extends BaseActivity {
	private Button btn_titlebar_back;
	private Button search;
	private TextView tv_attence_type;
	private TextView tv_titlebar_title;
	private String[] types;
	private int index;
	private CustomPopupDialog terminalTypeDialog = null;
	private CustomAlertDialog alertDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.vacation_search);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.vacation_search_title);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);

		btn_titlebar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		types = getResources().getStringArray(R.array.queryvacationtypes);
		tv_attence_type = (TextView) findViewById(R.id.tv_vacation_type);
		tv_attence_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (terminalTypeDialog == null) {
						terminalTypeDialog = new CustomPopupDialog(
								VacationSearchActivity.this, "休假类型", types,
								new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent,
											View view, int position, long id) {
										tv_attence_type.setText(((TextView) view)
												.getText());
										terminalTypeDialog.dismiss();
										index = position;
									}
								});
					}
					terminalTypeDialog.show();
				}
			}
		});

		search = (Button) findViewById(R.id.btn_check);
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (!tv_attence_type.getText().equals("")) {
						Intent intent = getIntent();
						intent.putExtra("type", index);
						setResult(Constant.RESULT_VACATION_SEARCH, intent);
						finish();
					} else {
						alertDialog = new CustomAlertDialog(
								VacationSearchActivity.this);
						alertDialog.show();
						alertDialog.setTitle("提示");
						alertDialog.setMessage("请选择休假类型");
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
            index = 0;
            tv_attence_type.setText(types[index]);
        }

	}

	@Override
	public void onRecvData(XmlNode response) {

	}
    @Override
    public void onRecvData(JsonObject response) {

    }

}
