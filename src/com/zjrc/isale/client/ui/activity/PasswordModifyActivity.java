package com.zjrc.isale.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：密码修改界面
 */
public class PasswordModifyActivity extends BaseActivity {

	private TextView tv_titlebar_title;	
	private Button btn_titlebar_back;
	
	private EditText et_oldpassword;
	private EditText et_newpassword;
	private EditText et_confirmpassword;
	private Button btn_ok;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.password_modify);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);
		
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.password_modify);
		tv_titlebar_title.requestFocus();

		et_oldpassword = (EditText)findViewById(R.id.et_oldpassword);
		
		et_newpassword = (EditText)findViewById(R.id.et_newpassword);
		
		et_confirmpassword = (EditText)findViewById(R.id.et_confirmpassword);
		
		btn_ok = (Button)findViewById(R.id.btn_ok);
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
	}
	
	private void sendSubmit() {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            params.put("password", et_oldpassword.getText().toString());
            params.put("newpassword", et_newpassword.getText().toString());
            request("login!modifyPassword?code=" + Constant.PASSWORD_MODIFY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}
	
	private boolean validityInput() {
		String sOldPassword = et_oldpassword.getText().toString();
		if ("".equalsIgnoreCase(sOldPassword)) {
			alert("当前密码不能为空,请输入当前密码!");

			et_oldpassword.requestFocus();
			return false;
		}
		String sNewPassword = et_newpassword.getText().toString();
		if ("".equalsIgnoreCase(sNewPassword)) {
			alert("新密码不能为空,请输入新密码!");

			et_newpassword.requestFocus();
			return false;
		}
		String sConfirmPassword = et_confirmpassword.getText().toString();
		if ("".equalsIgnoreCase(sConfirmPassword)) {
			alert("密码确认不能为空,请确认新密码!");
			
			et_confirmpassword.requestFocus();
			return false;
		}
		if (!sConfirmPassword.equalsIgnoreCase(sNewPassword)) {
			alert("两次输入的新密码不一致,请确认新密码!");

			et_newpassword.setText("");
			et_confirmpassword.setText("");
			et_newpassword.requestFocus();
			return false;
		}
		return true;
	}
	
	private void alert(String msg) {
		showAlertDialog("提示",msg, new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				alertDialog.cancel();
			}
		}, "确定", null, null);
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.PASSWORD_MODIFY.equals(code)) {
                Toast.makeText(this, "密码修改成功!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
