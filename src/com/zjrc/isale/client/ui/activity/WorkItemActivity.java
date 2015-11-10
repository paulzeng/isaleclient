package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：工作汇报详情界面
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.DateUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkItemActivity extends BaseActivity {
	private TextView tv_titlebar_title;
	private Button btn_titlebar_back;
	
	private TextView tv_type;
	
	private TextView tv_title;
	
	private TextView tv_time;

	private TextView tv_begindate;
	
	private TextView tv_enddate;
	
	private TextView tv_this_cycle;
	
	private TextView tv_next_cycle;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.workreport_item);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		
		tv_titlebar_title.setText(R.string.work_reportview);

		tv_type = (TextView)findViewById(R.id.tv_type);
		
		tv_title = (TextView)findViewById(R.id.tv_title);
		
		tv_time = (TextView) findViewById(R.id.tv_add_time);
		
		tv_begindate = (TextView)findViewById(R.id.tv_begin);
		
		tv_enddate = (TextView)findViewById(R.id.tv_end);
		
		tv_this_cycle = (TextView)findViewById(R.id.tv_this_cycle);
		
		tv_next_cycle = (TextView)findViewById(R.id.tv_next_cycle);
		
		
		/*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.workreporttypes,R.layout.spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_type.setAdapter(adapter); */
		
		Intent intent = getIntent();
		String id = intent.getStringExtra("corpworkreportid");
		request(id);
	}
	
	private void request(String id){
		ISaleApplication application = (ISaleApplication)getApplication();
		if (application!=null){		
			Map<String, String> params = new HashMap<String, String>();
			params.put("corpworkreportid", id);
            request("workReport!detail?code=" + Constant.WORKREPORT_ITEM, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}		
	}

	@Override
	public void onRecvData(XmlNode response) {
	}
	
    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.WORKREPORT_ITEM.equals(code)) {//工作汇报详情
            	JsonObject workreport = response.getAsJsonObject("body");
                if(workreport != null) {
    				String [] types = this.getResources().getStringArray(R.array.workreporttypes);
    				int index = Integer.valueOf(workreport.get("type").getAsString());
    				tv_title.setText(workreport.get("title").getAsString());
    				tv_type.setText(types[index]);
    				String dateStr = workreport.get("submitdate").getAsString();
    				Date date = DateUtil.str2Date(dateStr);
    				tv_time.setText(DateUtil.date2Str(date, getResources()
    						.getString(R.string.year_mouth_day_name_format)));
    				tv_begindate.setText(workreport.get("begindate").getAsString());
    				tv_enddate.setText(workreport.get("enddate").getAsString());
    				tv_this_cycle.setText(workreport.get("content").getAsString());
    				tv_next_cycle.setText(workreport.get("plan").getAsString());
                }
            }
        }
    }
}
