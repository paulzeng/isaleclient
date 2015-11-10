package com.zjrc.isale.client.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.util.xml.XmlNode;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 贺彬.陈浩
 * 功能描述：个人信息fragment
 */
public class ProfileFragment extends BaseFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sample, container, false);
		((TextView) v.findViewById(R.id.text)).setText("个人信息");
		return v;
	}

	@Override
	public void onRecvData(XmlNode response) {

	}

    @Override
    public void onRecvData(JsonObject response) {

    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void reSet_TitleBar_Main() {
		iv_title_add.setVisibility(View.GONE);
		iv_title_selector.setVisibility(View.GONE);
		tv_titlebar_title.setText("个人信息");
        btn_titlebar_filter.setVisibility(View.GONE);
	}
	@Override
	public void reSet_TitleBar_Right_Btn(boolean isMenuOpen) {
		if (isMenuOpen) {
			iv_title_add.setVisibility(View.VISIBLE);
			iv_title_add.setBackgroundResource(R.drawable.v2_title_close);
		} else {
			iv_title_add.setVisibility(View.GONE);
		}
	}
}
