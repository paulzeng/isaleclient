package com.zjrc.isale.client.ui.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：联系人详情界面
 */
public class ContactDetailActivity extends BaseActivity implements OnClickListener {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private ImageView iv_contactimage;
	private TextView tv_contactname;
	private TextView tv_contactphoneno;
	private TextView tv_dept;
	private TextView tv_usertype;
	private ImageView iv_message;
	private ImageView iv_call;
	private ImageView iv_ctd;

    private String phoneno;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.contact_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.contact_list);
		
		iv_contactimage = (ImageView) findViewById(R.id.iv_contactimage);
		tv_contactname = (TextView) findViewById(R.id.tv_contactname);
		tv_contactphoneno = (TextView) findViewById(R.id.tv_contactphoneno);
		tv_dept = (TextView) findViewById(R.id.tv_dept);
		tv_usertype = (TextView) findViewById(R.id.tv_usertype);
		
		iv_message = (ImageView) findViewById(R.id.iv_message);
		iv_message.setOnClickListener(this);
		iv_call = (ImageView) findViewById(R.id.iv_call);
		iv_call.setOnClickListener(this);
		iv_ctd = (ImageView) findViewById(R.id.iv_ctd);
		iv_ctd.setOnClickListener(this);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		request(bundle.getString("contactid"));
	}

	@Override
	public void onClick(View v) {
		if (CommonUtils.isNotFastDoubleClick()) {
			switch (v.getId()) {
			case R.id.iv_message:
				if (!TextUtils.isEmpty(phoneno)) {
					Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phoneno, null));
					startActivity(intent);
				} else {
                    Toast.makeText(this, "联系方式不能为空！", Toast.LENGTH_SHORT).show();
                }
				break;
			case R.id.iv_call:
				if (!TextUtils.isEmpty(phoneno)) {
					Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + phoneno));
					startActivity(intent);
				} else {
                    Toast.makeText(this, "联系方式不能为空！", Toast.LENGTH_SHORT).show();
                }
				break;
			case R.id.iv_ctd:
				break;
			}
		}
	}

	private void request(String contactid) {
		Map<String, String> params = new HashMap<String, String>();
        params.put("userid", contactid);
        request("contact!detail?code=" + Constant.CONTACT_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	@Override
	public void onRecvData(XmlNode response) {
	}
	
    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.CONTACT_DETAIL.equals(code)) {//通讯录员工详情
            	JsonObject contact = response.getAsJsonObject("body");
                if(contact != null) {
                	String sex = contact.get("sex").getAsString();
                    if ("女".equals(sex)) {
//                        iv_contactimage.setImageResource(R.drawable.v2_contact_head_female);
                    }
    				tv_contactname.setText(contact.get("name").getAsString());
    				phoneno = contact.get("mobile").getAsString();
    				if (TextUtils.isEmpty(phoneno)) {
    					phoneno = contact.get("phone").getAsString();
    				}
    				tv_contactphoneno.setText(TextUtils.isEmpty(phoneno) ? "暂无联系方式" : phoneno);
    				tv_dept.setText(contact.get("dept").getAsString());
    				int index = 0;
    				if (!TextUtils.isEmpty(contact.get("type").getAsString())) {
    					index = Integer.valueOf(contact.get("type").getAsString());
    				}
    				tv_usertype.setText(getResources().getStringArray(R.array.usertypes)[index]);
                }
            }
        }
    }
}
