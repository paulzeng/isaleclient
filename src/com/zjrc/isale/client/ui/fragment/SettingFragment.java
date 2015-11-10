package com.zjrc.isale.client.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.ui.activity.GuideActivity;
import com.zjrc.isale.client.ui.activity.LoginActivity;
import com.zjrc.isale.client.ui.activity.PasswordModifyActivity;
import com.zjrc.isale.client.ui.activity.ShareActivity;
import com.zjrc.isale.client.util.UpdateUtil;
import com.zjrc.isale.client.util.xml.XmlNode;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 贺彬,陈浩
 * 功能描述：设置fragment
 */
public class SettingFragment extends BaseFragment implements OnClickListener {
    private TextView tv_version;
    private TextView tv_2dcode;
    private TextView tv_modify_password;
    private LinearLayout ll_checkversion;
    private TextView tv_alreadylasestversion;
    private TextView tv_notlasestversion;
    private TextView tv_newbieguard;
    private Button bt_logout;

    private View v;// 缓存页面

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null) {
            v = inflater.inflate(R.layout.v2_fragment_settings, container, false);
            tv_version = (TextView) v.findViewById(R.id.tv_version);

            float fCurVersion;
            try {
                PackageManager pm = getActivity().getPackageManager();
                PackageInfo pi = pm.getPackageInfo(getActivity().getPackageName(), 0);
                fCurVersion = Float.parseFloat(pi.versionName);
                tv_version.setText("版本 V" + pi.versionName);
            } catch (Exception e) {
                fCurVersion = (float) 1.00;
                tv_version.setText("版本 V" + fCurVersion);
            }
            float fLastVersion;
            try {
                ISaleApplication application = (ISaleApplication) getActivity().getApplication();
                fLastVersion = Float.parseFloat(application.getConfig().getClientversion());
            } catch (Exception e) {
                fLastVersion = (float) 1.00;
            }


            tv_alreadylasestversion = (TextView) v.findViewById(R.id.tv_alreadylasestversion);
            tv_notlasestversion = (TextView) v.findViewById(R.id.tv_notlasestversion);

            if (fCurVersion == fLastVersion) {
                tv_alreadylasestversion.setVisibility(View.VISIBLE);
            } else if (fCurVersion < fLastVersion) {
                tv_notlasestversion.setVisibility(View.VISIBLE);
            }

            tv_2dcode = (TextView) v.findViewById(R.id.tv_2dcode);
            tv_2dcode.setOnClickListener(this);

            tv_modify_password = (TextView) v.findViewById(R.id.tv_modify_password);
            tv_modify_password.setOnClickListener(this);

            ll_checkversion = (LinearLayout) v.findViewById(R.id.ll_checkversion);
            ll_checkversion.setOnClickListener(this);

            tv_newbieguard = (TextView) v.findViewById(R.id.tv_newbieguard);
            tv_newbieguard.setOnClickListener(this);

            bt_logout = (Button) v.findViewById(R.id.bt_logout);
            bt_logout.setOnClickListener(this);
        }

        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null) {
            parent.removeView(v);// 先移除
        }

        return v;
    }

    @Override
    public void onClick(View v) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		final FragmentActivity activity = SettingFragment.this.getActivity();
    		switch (v.getId()) {
    		case R.id.tv_2dcode:
    			startActivity(new Intent(activity, ShareActivity.class));
    			break;
    		case R.id.tv_modify_password:
    			startActivity(new Intent(activity, PasswordModifyActivity.class));
    			break;
    		case R.id.ll_checkversion:
                new UpdateUtil(activity).checkUpdate(false, true);
    			break;
    		case R.id.tv_newbieguard:
    			// TODO
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("ssoid", "");
				bundle.putString("ecCode", "");
				bundle.putString("comeFrom", "Setting");
				intent.putExtras(bundle);
				intent.setClass(getActivity(), GuideActivity.class);
				startActivity(intent);
    			break;
    		case R.id.bt_logout:
    			showAlertDialog("确认", "你确实要注销系统？", new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					Intent intent = new Intent();
    					intent.setClass(activity,
    							LoginActivity.class);
    					startActivity(intent);
    					activity.finish();
    					alertDialog.cancel();
    				}
    			}, "是", new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					alertDialog.cancel();
    				}
    			}, "否");
    			break;
    		}
        }
    }

    @Override
    public void onRecvData(XmlNode response) {
    }

    @Override
    public void reSet_TitleBar_Main() {
        iv_title_add.setVisibility(View.GONE);
        iv_title_selector.setVisibility(View.GONE);
        tv_titlebar_title.setText("设置");
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
    @Override
    public void onRecvData(JsonObject response) {

    }
}
