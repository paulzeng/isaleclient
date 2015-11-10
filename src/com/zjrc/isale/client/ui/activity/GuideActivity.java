package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.ArrayList;

public class GuideActivity extends BaseActivity {
	private ViewPager viewpager;
	private PagerAdapter adapter;
	private ArrayList<View> viewList;
	private String ssoid;

	private String ecCode;
	
	private String comeFrom="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		Intent intent = this.getIntent();
		if (intent != null) {
			Bundle bundle =intent.getExtras();
			ssoid = bundle.getString("token");
			ecCode = bundle.getString("ecCode");
			comeFrom= bundle.getString("comeFrom");
		} else {
			ssoid = "";
			ecCode = "";
		}
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		View view1 = getLayoutInflater().inflate(R.layout.viewpager_item, null);
		view1.setBackgroundResource(R.drawable.guide_page_1);
		View view2 = getLayoutInflater().inflate(R.layout.viewpager_item, null);
		view2.setBackgroundResource(R.drawable.guide_page_2);
		View view3 = getLayoutInflater().inflate(R.layout.viewpager_item, null);
		view3.setBackgroundResource(R.drawable.guide_page_3);
		View view4 = getLayoutInflater().inflate(R.layout.viewpager_item, null);
		view4.setBackgroundResource(R.drawable.guide_page_4);
		Button btn_start = (Button) view4.findViewById(R.id.btn_viewpager);
		if (comeFrom.equalsIgnoreCase("StartActivity")) {
			btn_start.setVisibility(View.VISIBLE);
			btn_start.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ISaleApplication application = (ISaleApplication) getApplication();
					application.getConfig().setFirstLogin(false);
					application.writeConfig(application.getConfig());
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("ssoid", ssoid);
					bundle.putString("ecCode", ecCode);
					intent.putExtras(bundle);
					intent.setClass(GuideActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
			});
		}else{
			btn_start.setVisibility(View.GONE);
		}
		viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);
		viewList.add(view4);
	
		adapter = new PagerAdapter() {  
			  
            @Override  
            public boolean isViewFromObject(View arg0, Object arg1) {  
  
                return arg0 == arg1;  
            }  
  
            @Override  
            public int getCount() {  
  
                return viewList.size();  
            }  
  
            @Override  
            public void destroyItem(ViewGroup container, int position,  
                    Object object) {  
                container.removeView(viewList.get(position));  
  
            }  
  
            @Override  
            public int getItemPosition(Object object) {  
  
                return super.getItemPosition(object);  
            }  
  
            @Override  
            public CharSequence getPageTitle(int position) {  
  
                return "";  
            }  
  
            @Override  
            public Object instantiateItem(ViewGroup container, int position) {  
                container.addView(viewList.get(position));  
                return viewList.get(position);  
            }  
  
        };
        viewpager.setAdapter(adapter);
	}

	@Override
	public void onRecvData(XmlNode response) {
		// TODO Auto-generated method stub

	}
    @Override
    public void onRecvData(JsonObject response) {

    }
}
