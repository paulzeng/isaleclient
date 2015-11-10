package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;
import com.zjrc.isale.client.util.xml.XmlNode;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：反馈筛选
 */
public class SuggestionSearchActivity extends BaseActivity {
	private Button btn_titlebar_back;
	private Button search;
	private TextView tv_titlebar_title;
	private CustomAlertDialog alertDialog = null;
	private EditText et_searchkey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.travel_search);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.suggestion_search);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		et_searchkey = (EditText) findViewById(R.id.et_place);
		et_searchkey.setHint(getResources().getString(R.string.suggestion_search_hint));
		search = (Button) findViewById(R.id.btn_check);
		search.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (!et_searchkey.getText().toString().equals("")) {
						Intent intent = getIntent();
						intent.putExtra("key", et_searchkey.getText().toString());
						setResult(Activity.RESULT_OK, intent);
						finish();
					} else {
						// alertDialog = new CustomAlertDialog(
						// TravelSearchActivity.this);
						// alertDialog.show();
						// alertDialog.setTitle("提示");
						// alertDialog.setMessage("请输入目的地关键词");
						// alertDialog.setOnPositiveListener(new OnClickListener() {
						//
						// @Override
						// public void onClick(View arg0) {
						// alertDialog.dismiss();
						// }
						// });
						// alertDialog.getBtn_negative().setVisibility(View.GONE);
						// }
						Intent intent = getIntent();
						intent.putExtra("key", "");
						setResult(Activity.RESULT_OK, intent);
						finish();
					}
				}
			}
		});

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            et_searchkey.setText("");
        }

	}

	@Override
	public void onRecvData(XmlNode response) {

	}
    @Override
    public void onRecvData(JsonObject response) {

    }
}
