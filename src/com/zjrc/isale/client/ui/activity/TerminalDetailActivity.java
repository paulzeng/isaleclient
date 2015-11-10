package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Area;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.bean.TerminalType;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：我的客户详情界面
 */
public class TerminalDetailActivity extends BaseActivity implements OnClickListener {
	private TextView tv_titlebar_title;
	private Button btn_titlebar_back;

	private TextView tv_terminalname;
	private TextView tv_terminaldate;
	private TextView tv_terminalcode;
	private TextView tv_terminaltype;
	private TextView tv_terminalarea;
	private TextView tv_terminaladdress;
	private Button btn_map;
	private TextView tv_terminalzip;
	private TextView tv_terminalplacesize;
	private TextView tv_terminalemployeenum;
	private TextView tv_terminalsalenum;
	private TextView tv_terminalphone;
	private TextView tv_terminalfax;
	private TextView tv_terminalcontactman;
	private TextView tv_terminalcontactpost;
	private TextView tv_terminalcontactphone;
	private TextView tv_terminalcontactmobile;
	private TextView tv_terminalemail;
	private TextView tv_terminalstate;
	private Button btn_modify;
	private Button btn_delete;
	
	private String terminalid = "";
	
	private String longitude;
	private String latitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.terminal_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);
		
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText("客户详情");

		tv_terminalname = (TextView) findViewById(R.id.tv_terminalname);
		tv_terminaldate = (TextView) findViewById(R.id.tv_terminaldate);
		tv_terminalcode = (TextView) findViewById(R.id.tv_terminalcode);
		tv_terminaltype = (TextView) findViewById(R.id.tv_terminaltype);
		tv_terminalarea = (TextView) findViewById(R.id.tv_terminalarea);
		tv_terminaladdress = (TextView) findViewById(R.id.tv_terminaladdress);
		btn_map = (Button) findViewById(R.id.btn_map);
		tv_terminalzip = (TextView) findViewById(R.id.tv_terminalzip);
		tv_terminalplacesize = (TextView) findViewById(R.id.tv_terminalplacesize);
		tv_terminalemployeenum = (TextView) findViewById(R.id.tv_terminalemployeenum);
		tv_terminalsalenum = (TextView) findViewById(R.id.tv_terminalsalenum);
		tv_terminalphone = (TextView) findViewById(R.id.tv_terminalphone);
		tv_terminalfax = (TextView) findViewById(R.id.tv_terminalfax);
		tv_terminalcontactman = (TextView) findViewById(R.id.tv_terminalcontactman);
		tv_terminalcontactpost = (TextView) findViewById(R.id.tv_terminalcontactpost);
		tv_terminalcontactphone = (TextView) findViewById(R.id.tv_terminalcontactphone);
		tv_terminalcontactmobile = (TextView) findViewById(R.id.tv_terminalcontactmobile);
		tv_terminalemail = (TextView) findViewById(R.id.tv_terminalemail);
		tv_terminalstate = (TextView) findViewById(R.id.tv_terminalstate);
		btn_modify = (Button) findViewById(R.id.btn_modify);
		btn_delete = (Button) findViewById(R.id.btn_delete);
		
		btn_map.setOnClickListener(this);
		btn_modify.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		
		Bundle bundle = getIntent().getExtras();
		terminalid = bundle.getString("terminalid");
		sendQueryDetail();
	}

	@Override
	public void onClick(View v) {
		if (CommonUtils.isNotFastDoubleClick()) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_map:
				intent.setClass(TerminalDetailActivity.this, BaiduMapActivity.class);
				intent.putExtra("longitude", longitude);
				intent.putExtra("latitude", latitude);
				intent.putExtra("terminalname", tv_terminalname.getText());
				startActivity(intent);
				break;
			case R.id.btn_modify:
				intent.setClass(TerminalDetailActivity.this, TerminalSubmitActivity.class);
				intent.putExtra("operate", "modify");
				intent.putExtra("terminalid", terminalid);
				startActivityForResult(intent, 0);
				break;
			case R.id.btn_delete:
				showAlertDialog("确认", "确定删除？", new OnClickListener() {
					@Override
					public void onClick(View view) {
						requestDelete(terminalid);
					}
				}, "确定", "取消", new OnClickListener() {
					@Override
					public void onClick(View view) {
						alertDialog.cancel();
					}
				});
				break;
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                sendQueryDetail();
                // 修改成功，通知列表刷新
                setResult(RESULT_OK);
            }
        }
	}
	
	private void sendQueryDetail() {
		Map<String, String> params = new HashMap<String, String>();
        params.put("terminalid", terminalid);
        params.put("companyid", "");
        params.put("terminalcode", "");
        request("terminal!detail?code=" + Constant.TERMINAL_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	public void requestDelete(String id) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("operate","delete");
            params.put("companyid",XmlValueUtil.encodeString(application.getConfig().getCompanyid()));
            params.put("userid",XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("id", XmlValueUtil.encodeString(id));
            params.put("code", "");
            params.put("name","");
            params.put("typeid","");
            params.put("areaid","");
            params.put("address","");
            params.put("zip","");
            params.put("longitude","");
            params.put("latitude","");
            params.put("placesize","");
            params.put("employeenum","");
            params.put("salenum","");
            params.put("phone","");
            params.put("mobile","");
            params.put("fax","");
            params.put("email","");
            params.put("contactman","");
            params.put("contactpost","");
            params.put("contactphone","");
            params.put("contactmobile","");
            params.put("state","");
            params.put("fileid","");
            request("terminal!submit?code=" + Constant.TERMINAL_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	@Override
	public void onRecvData(XmlNode response) {
	}
	
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TERMINAL_SUBMIT.equals(code)) {
                Toast.makeText(this, "删除客户成功!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else if (Constant.TERMINAL_DETAIL.equals(code)) {//网点详情
            	JsonObject backorder = response.getAsJsonObject("body");
                if(backorder != null) {
                	ISaleApplication application = (ISaleApplication) getApplication();
    				if (application != null) {
    					int iposition = 0;
    					String itext = "";

    					tv_terminalname.setText(backorder.get("name").getAsString());
    					tv_terminaldate.setText(backorder.get("createdate").getAsString());
    					tv_terminalcode.setText(backorder.get("code").getAsString());
    					for (int i = 0; i < application.getTerminaltypes().size(); i++) {
    						TerminalType terminaltype = application.getTerminaltypes().get(i);
    						if (terminaltype.getId().equalsIgnoreCase(backorder.get("typeid").getAsString())) {
    							iposition = i;
    							itext = terminaltype.getName();
    							break;
    						}
    					}
    					tv_terminaltype.setText(itext);
    					tv_terminaltype.setTag(iposition);
    					for (int i = 0; i < application.getAreas().size(); i++) {
    						Area area = application.getAreas().get(i);
    						if (area.getId().equalsIgnoreCase(backorder.get("areaid").getAsString())) {
    							iposition = i;
    							itext = area.getName();
    							break;
    						}
    					}
    					tv_terminalarea.setText(itext);
    					tv_terminalarea.setTag(iposition);
    					tv_terminaladdress.setText(backorder.get("address").getAsString());
    					longitude = backorder.get("longitude").getAsString();
    					latitude = backorder.get("latitude").getAsString();
    					if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)) {
    						btn_map.setVisibility(View.VISIBLE);
    					} else {
    						btn_map.setVisibility(View.GONE);
    					}
    					tv_terminalzip.setText(backorder.get("zip").getAsString());
    					try {
    						iposition = Integer.valueOf(backorder.get("placesize").getAsString());
    					} catch (Exception e1) {
    						iposition = -1;
    					}
    					String[] placesizes = getResources().getStringArray(R.array.terminalplacesizes);
    					if (iposition != -1 && iposition < placesizes.length) {
    						tv_terminalplacesize.setText(placesizes[iposition]);
    						tv_terminalplacesize.setTag(iposition);
    					}
    					try {
    						iposition = Integer.valueOf(backorder.get("employeenum").getAsString());
    					} catch (Exception e1) {
    						iposition = -1;
    					}
    					String[] employeenums = getResources().getStringArray(R.array.terminalemployeenums);
    					if (iposition != -1 && iposition < employeenums.length) {
    						tv_terminalemployeenum.setText(employeenums[iposition]);
    						tv_terminalemployeenum.setTag(iposition);
    					}
    					try {
    						iposition = Integer.valueOf(backorder.get("salenum").getAsString());
    					} catch (Exception e1) {
    						iposition = -1;
    					}
    					String[] salenums = getResources().getStringArray(R.array.terminalsalenums);
    					if (iposition != -1 && iposition < salenums.length) {
    						tv_terminalsalenum.setText(salenums[iposition]);
    						tv_terminalsalenum.setTag(iposition);
    					}
    					tv_terminalphone.setText(backorder.get("phone").getAsString());
    					tv_terminalfax.setText(backorder.get("fax").getAsString());
    					tv_terminalemail.setText(backorder.get("email").getAsString());
    					tv_terminalcontactman.setText(backorder.get("contactman").getAsString());
    					tv_terminalcontactpost.setText(backorder.get("contactpost").getAsString());
    					tv_terminalcontactphone.setText(backorder.get("contactphone").getAsString());
    					tv_terminalcontactmobile.setText(backorder.get("contactmobile").getAsString());
    					try {
    						iposition = Integer.valueOf(backorder.get("state").getAsString());
    					} catch (Exception e1) {
    						iposition = -1;
    					}
    					String[] states = getResources().getStringArray(R.array.terminalstates);
    					if (iposition != -1 && iposition < states.length) {
    						tv_terminalstate.setText(states[iposition]);
    						tv_terminalstate.setTag(iposition);
    					}
    				}
                }
            }
        }
    }
}