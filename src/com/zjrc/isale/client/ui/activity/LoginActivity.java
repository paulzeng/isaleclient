package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：系统登录界面
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Area;
import com.zjrc.isale.client.bean.Audit;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.bean.TerminalType;
import com.zjrc.isale.client.util.DESedeUtilsForServer;
import com.zjrc.isale.client.util.ScreenAdaptation;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends BaseActivity implements OnTouchListener {

	private EditText et_phoneno;

	private EditText et_password;

	private CheckBox cb_savepassword;

	private TextView tv_forget_password;

	private Button btn_login; // 登录按钮

	private TextView tv_version;

	private TextView tv_imsi;

	private long exittime = 0;

	private CustomFindPasswordDialog findPasswordDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(this);
		ViewGroup viewgroup = (ViewGroup) inflater
				.inflate(R.layout.login, null);
		ScreenAdaptation.SubViewAdaption(viewgroup);
		setContentView(viewgroup);
		et_phoneno = (EditText) findViewById(R.id.et_phoneno);
		et_password = (EditText) findViewById(R.id.et_password);
		cb_savepassword = (CheckBox) findViewById(R.id.cb_savepassword);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnTouchListener(this);
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			et_phoneno.setText(application.getConfig().getPhoneno());
			if (application.getConfig().getSavepassword()) {
				et_password.setText(application.getConfig().getPassword());
			}
			cb_savepassword.setChecked(application.getConfig()
					.getSavepassword());
		}
		tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
		tv_forget_password.setText(Html.fromHtml("<u>忘记密码</u>"));
		// 忘记密码
		tv_forget_password.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (findPasswordDialog == null) {
					findPasswordDialog = new CustomFindPasswordDialog(
							LoginActivity.this);
				}
				findPasswordDialog.show();
			}
		});
		tv_version = (TextView) findViewById(R.id.tv_version);
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			tv_version.setText("版本号：V" + pi.versionName);
		} catch (Exception e) {
			tv_version.setText("版本号：V" + R.string.login_version);
		}
		tv_imsi = (TextView) findViewById(R.id.tv_imsi);
		TelephonyManager telephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telephonymanager.getSubscriberId();
		if (imsi != null) {
			StringBuilder sb = new StringBuilder();
			int length = imsi.length();
			int index = 0;
			int x = 4;
			while (index < length) {
				x = (length - index <= 4) ? (length - index) : 4;
				sb.append(imsi.substring(index, index + x) + " ");
				index += x;
			}
			tv_imsi.setText("IMSI: " + sb.toString());
		} else {
			tv_imsi.setText("IMSI: 无法获取");
		}
		Intent intent = this.getIntent();

		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String ssoid = bundle.getString("ssoid");
				String ecCode = bundle.getString("ecCode");

				if (ssoid != null && !"".equalsIgnoreCase(ssoid)) {// 有参数并不为空
					requestSSOIDLogin(ssoid);
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exittime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次返回键关闭程序",
						Toast.LENGTH_SHORT).show();
				exittime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btn_login:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (validityInput()) {
					ISaleApplication application = (ISaleApplication) getApplication();
					if (application != null) {
						application.getConfig().setPhoneno(
								et_phoneno.getText().toString());
						application.getConfig().setPassword(
								et_password.getText().toString());
						application.getConfig().setSavepassword(
								cb_savepassword.isChecked());
						application.writeConfig(application.getConfig());
					}

                    TelephonyManager telephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String imsi = telephonymanager.getSubscriberId();

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("phoneno", et_phoneno.getText().toString());
                    String password = et_password.getText().toString();
                    params.put("password", DESedeUtilsForServer.encrypt(password));
//                    params.put("password", et_password.getText().toString());
                    params.put("devicecode",  imsi==null?"":imsi);
                    params.put("devicetype", "0");
                    request("login!login?code=" + Constant.LOGIN, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return false;
	}

	private boolean validityInput() {
		String sPhoneNo = et_phoneno.getText().toString();
		if ("".equalsIgnoreCase(sPhoneNo)) {
			showAlertDialog("提示", "手机号码不能为空,请输入手机号码!",
					new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_phoneno.requestFocus();
			return false;
		}
		String sPassword = et_password.getText().toString();
		if ("".equalsIgnoreCase(sPassword)) {
			showAlertDialog("提示", "密码不能为空,请输入密码!", new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null, null);
			et_password.requestFocus();
			return false;
		}
		/**
		 * 获取SIM卡的IMSI码 SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile
		 * Subscriber Identification Number）是区别移动用户的标志，
		 * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
		 * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
		 * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
		 * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可 46001 46002
		 * 中国移动(因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号) 46001
		 * 中国联通 46003 中国电信
		 * 
		 * TelephonyManager telephonymanager = (TelephonyManager)
		 * getSystemService(Context.TELEPHONY_SERVICE); String imsi =
		 * telephonymanager.getSubscriberId();// if(imsi!=null){
		 * if(imsi.startsWith("46000") || imsi.startsWith("46002")){
		 * 
		 * }else{ Builder b = new
		 * AlertDialog.Builder(this).setTitle("提示").setMessage
		 * ("本客户端专为中国移动用户定制，您无法使用!"); b.setPositiveButton("确定", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int whichButton) { dialog.cancel(); }
		 * }).show(); return false; } }
		 */
		return true;
	}

	/**
	 * 发送获取网点类型请求
	 */
	public void requestTerminalTypes() {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("companyid", application.getConfig().getCompanyid());
            request("terminalType!list?code=" + Constant.TERMINALTYPE_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	/**
	 * 发送获取所分配区域请求
	 */
	public void requestSSOIDLogin(String ssoid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ssoid", ssoid);
        request("login!ssoidLogin?code=" + Constant.LOGIN_BYSSOID, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.LOGIN.equals(code) || Constant.LOGIN_BYSSOID.equals(code)) {
                ISaleApplication application = (ISaleApplication) getApplication();
                if (application != null) {
                    Gson g = new Gson();
                    JsonObject body = response.getAsJsonObject("body");
                    application.getConfig().setCompanyid(body.get("companyid").getAsString());
                    application.getConfig().setUserid(body.get("userid").getAsString());
                    application.getConfig().setUsername(body.get("username").getAsString());
                    application.getConfig().setClientversion(body.get("clientversion").getAsString());
                    application.getConfig().setClientdownloadurl(body.get("clientdownloadurl").getAsString());
                    application.getConfig().setNeedaudit(body.get("needaudit").getAsString());
                    application.getConfig().setIsaudit(body.get("isaudit").getAsString());
                    application.getConfig().setApitoken(body.get("apitoken").getAsString());
                    ArrayList<Area> areaList = new ArrayList<Area>();
                    JsonArray areas = body.getAsJsonArray("areas");
                    for (JsonElement areaElem : areas) {
                        JsonObject areaObj = (JsonObject) areaElem;
                        Area area = new Area();
                        area.setId(areaObj.get("id").getAsString());
                        area.setName(areaObj.get("name").getAsString());
                        area.setParentid(areaObj.get("parentid").getAsString());
                        areaList.add(area);
                    }
                    application.setAreas(areaList);
                    application.getConfig().setAreas(g.toJson(areaList));
                    ArrayList<Audit> auditList = new ArrayList<Audit>();
                    JsonArray audits = body.getAsJsonArray("audits");
                    for (JsonElement auditElem : audits) {
                        JsonObject auditObj = (JsonObject) auditElem;
                        Audit audit = new Audit();
                        audit.setUserid(auditObj.get("userid").getAsString());
                        audit.setUsername(auditObj.get("username").getAsString());
                        auditList.add(audit);
                    }
                    application.setAudit(auditList);
                    application.getConfig().setAudits(g.toJson(auditList));
                }
                requestTerminalTypes();
            } else if (Constant.TERMINALTYPE_LIST.equals(code)) {
                Gson g = new Gson();
                JsonArray types = response.getAsJsonArray("body");
                if (types != null && types.size() > 0) {
                    ArrayList<TerminalType> typeList = new ArrayList<TerminalType>();
                    for (JsonElement typeElem : types) {
                        JsonObject typeObj = (JsonObject) typeElem;
                        TerminalType type = new TerminalType();
                        type.setId(typeObj.get("id").getAsString());
                        type.setName(typeObj.get("name").getAsString());
                        typeList.add(type);
                    }
                    ISaleApplication application = (ISaleApplication) getApplication();
                    if (application != null) {
                        application.setTerminaltypes(typeList);
                        application.getConfig().setTerminalTypes(g.toJson(typeList));
                    }
                }
                ISaleApplication application = (ISaleApplication) getApplication();
                application.writeConfig(application.getConfig());
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            } else if (Constant.PASSWORD_GET.equals(code)) {
                Toast.makeText(this, "登录密码将以短信的形式下发到您手机上，请注意查收!", Toast.LENGTH_SHORT).show();
                findPasswordDialog.hide();
            }
        }
    }

    public class CustomFindPasswordDialog extends Dialog implements
			View.OnClickListener {
		private EditText et_phoneno;
		private Button btn_getpassword;

		private boolean isclick = false;

		public CustomFindPasswordDialog(Context context) {
			super(context);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.find_password);
			getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));

			et_phoneno = (EditText) findViewById(R.id.et_phoneno);

			btn_getpassword = (Button) findViewById(R.id.btn_getpassword);
			btn_getpassword.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			if (!isclick) {
				isclick = true;
				forgetpassword();
				isclick = false;
			}
		}

		/**
		 * 发送忘记密码请求获取密码
		 */
		private void forgetpassword() {
			String sPhoneNo = et_phoneno.getText().toString().trim();
			if ("".equalsIgnoreCase(sPhoneNo)) {
				showAlertDialog("提示", "手机号码不能为空,请输入手机号码!",
						new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								alertDialog.cancel();
							}
						}, "确定", null, null);
				et_phoneno.requestFocus();
				return;
			}
			TelephonyManager telephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = telephonymanager.getSubscriberId();

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("phoneno", sPhoneNo);
            params.put("devicecode", imsi==null?"":imsi);
            request("login!getPassword?code=" + Constant.PASSWORD_GET, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

}
