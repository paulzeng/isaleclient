package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.task.FileDownloadTask;
import com.zjrc.isale.client.task.IDownloadEventListener;
import com.zjrc.isale.client.util.DateUtil;
import com.zjrc.isale.client.util.FileUtil;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：反馈详情
 */
public class SuggestionDetailActivity extends BaseActivity {
	Button btn_titlebar_back;
	TextView tv_titlebar_title;

	private TextView tv_title;
	private TextView tv_state;
	private TextView tv_add_time;
	private TextView tv_suggestion_type;
	private TextView tv_suggestion_content;
	private ImageView iv_suggestion_pic;
	private TextView tv_suggestion_state;
	private TextView tv_suggestion_dispose_man;
	private TextView tv_suggestion_dispose_date;
	private TextView tv_suggestion_dispose_result;
	private TextView tv_suggestion_dispose_content;
	private RelativeLayout rl_suggestion_dispose_man;
	private RelativeLayout rl_suggestion_dispose_date;
	private RelativeLayout rl_suggestion_dispose_result;
	private RelativeLayout rl_suggestion_dispose_content;
	private FileDownloadTask downloadtask;
	private String photofilepath = "";
	private String photoid = "";
	private Button btn_map;
	private TextView tv_place;
	private String longitude="";
	private String latitude="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.suggestion_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(getResources().getString(
				R.string.suggestion_detail));
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_state = (TextView) findViewById(R.id.tv_state);
		tv_add_time = (TextView) findViewById(R.id.tv_add_time);
		tv_suggestion_type = (TextView) findViewById(R.id.tv_suggestion_type);
		tv_suggestion_content = (TextView) findViewById(R.id.tv_suggestion_content);
		iv_suggestion_pic = (ImageView) findViewById(R.id.iv_suggestion_pic);
		tv_suggestion_state = (TextView) findViewById(R.id.tv_suggestion_state);
		tv_suggestion_dispose_man = (TextView) findViewById(R.id.tv_suggestion_dispose_man);
		tv_suggestion_dispose_date = (TextView) findViewById(R.id.tv_suggestion_dispose_date);
		tv_suggestion_dispose_result = (TextView) findViewById(R.id.tv_suggestion_dispose_result);
		tv_suggestion_dispose_content = (TextView) findViewById(R.id.tv_suggestion_dispose_content);
		rl_suggestion_dispose_man = (RelativeLayout) findViewById(R.id.rl_suggestion_dispose_man);
		rl_suggestion_dispose_date = (RelativeLayout) findViewById(R.id.rl_suggestion_dispose_date);
		rl_suggestion_dispose_result = (RelativeLayout) findViewById(R.id.rl_suggestion_dispose_result);
		rl_suggestion_dispose_content = (RelativeLayout) findViewById(R.id.rl_suggestion_dispose_content);
		tv_place = (TextView) findViewById(R.id.tv_place);

		btn_map = (Button) findViewById(R.id.btn_map);
		suggestionid = getIntent().getExtras().getString("suggestionid");
		sendQueryDetail();
	}

	private String suggestionid = "";

	/**
	 * 查询投诉建议详情
	 */
	private void sendQueryDetail() {
		Map<String, String> params = new HashMap<String, String>();
        params.put("suggestionid", suggestionid);
        request("suggestion!detail?code=" + Constant.SUGGESTION_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}


	@Override
	public void onRecvData(XmlNode response) {
	}

	private IDownloadEventListener downloadeventlistener = new IDownloadEventListener() {

		@Override
		public void onFinish(String filetype, String filename) {
			if (filetype.equalsIgnoreCase("suggestion")) {
				photofilepath = filename;
				iv_suggestion_pic.setImageBitmap(BitmapFactory
						.decodeFile(photofilepath));
				iv_suggestion_pic.setOnClickListener(new ImageView.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setClass(SuggestionDetailActivity.this, ImageViewActivity.class);
						intent.putExtra("imagefilename", photofilepath);
						startActivity(intent);
					}
				});
			}
		}

		@Override
		public void onFail(String filetype, String message) {
			if (filetype.equalsIgnoreCase("suggestion")) {
				iv_suggestion_pic.setImageResource(R.drawable.v2_default_break);
				iv_suggestion_pic
						.setOnClickListener(new ImageView.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									ISaleApplication application = (ISaleApplication) getApplication();
									if (application != null) {
										FileDownloadTask downloadtask = new FileDownloadTask(
												application,
												SuggestionDetailActivity.this,
												"suggestion", photoid,
												downloadeventlistener, false);
										downloadtask.execute();
									}
								}
							}
						});
			}
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (downloadtask != null) {
			downloadtask.cancelTask();
		}
	}

    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.SUGGESTION_DETAIL.equals(code)) {//投诉建议详情
            	JsonObject suggestion = response.getAsJsonObject("body");
                if(suggestion != null) {
                	String signState = suggestion.get("signstate").getAsString();
    				tv_title.setText(suggestion.get("title").getAsString());
    				String dateStr = suggestion.get("date").getAsString();
    				Date date = DateUtil.str2Date(dateStr);
    				tv_add_time.setText(DateUtil.date2Str(date, getResources()
    						.getString(R.string.year_mouth_day_name_format)));
    				String type = suggestion.get("type").getAsString();
    				if (type.equalsIgnoreCase("0")) {
    					tv_suggestion_type.setText("投诉");
    				} else {
    					tv_suggestion_type.setText("建议");
    				}
    				tv_suggestion_content.setText(suggestion.get("content").getAsString() == null ? "无" : suggestion.get("content").getAsString());
    				if (signState.equalsIgnoreCase("0")) {
    					tv_state.setText("未处理");
    					tv_suggestion_state.setText("未处理");
    					rl_suggestion_dispose_content.setVisibility(View.GONE);
    					rl_suggestion_dispose_date.setVisibility(View.GONE);
    					rl_suggestion_dispose_man.setVisibility(View.GONE);
    					rl_suggestion_dispose_result.setVisibility(View.GONE);
    				} else {
    					tv_state.setText("已处理");
    					tv_suggestion_state.setText("已处理");
    					rl_suggestion_dispose_content.setVisibility(View.VISIBLE);
    					rl_suggestion_dispose_date.setVisibility(View.VISIBLE);
    					rl_suggestion_dispose_man.setVisibility(View.VISIBLE);
    					rl_suggestion_dispose_result.setVisibility(View.VISIBLE);
    					tv_suggestion_dispose_man.setText(suggestion.get("signuser").getAsString() == null ? "无"
    							: suggestion.get("signuser").getAsString());
    					String signDate = suggestion.get("signdate").getAsString();
    					String []ds= signDate.split(" ");
    					tv_suggestion_dispose_date.setText(signDate == null ? "无"
    							: ds[0]);
    					String[] audit_signresult = getResources().getStringArray(
    							R.array.suggestion_signresult);
    					int index = (suggestion.get("signresult").getAsString() == null ? -1
    					: Integer.valueOf(suggestion.get("signresult").getAsString()));
    					if (index==0) {
    						index=1;
    					}else if(index==1){
    						index=0;
    					}
    					if (index==-1) {
    						tv_suggestion_dispose_result.setText("无");
    					}else {
    						tv_suggestion_dispose_result.setText(audit_signresult[index]);
    					}
    					tv_suggestion_dispose_content.setText(suggestion.get("signcontent").getAsString() == null ? "无"
    							: suggestion.get("signcontent").getAsString());
    				}

    				tv_place.setText(suggestion.get("address").getAsString());
    				btn_map.setVisibility(View.VISIBLE);
    				longitude = suggestion.get("longitude").getAsString();
    				latitude = suggestion.get("latitude").getAsString();
    				btn_map.setOnClickListener(new Button.OnClickListener() {

    					@Override
    					public void onClick(View arg0) {
    						if (CommonUtils.isNotFastDoubleClick()) {
    							Intent intent = new Intent();
    							intent.setClass(SuggestionDetailActivity.this,
    									BaiduMapActivity.class);
    							intent.putExtra("longitude", longitude);
    							intent.putExtra("latitude", latitude);
    							intent.putExtra("terminalname", tv_place.getText().toString());
    							startActivity(intent);
    						}
    					}
    				});
    				
    				photoid = suggestion.get("picfileid").getAsString();
    				photofilepath = suggestion.get("picfilename").getAsString();
    				if (photoid != null && !photoid.equalsIgnoreCase("")) {
    					iv_suggestion_pic.setVisibility(View.VISIBLE);

    					if (photofilepath != null
    							&& !photofilepath.equalsIgnoreCase("")) {// 下载地址不为空，尝试本地缓存取图
    							final String path = FileUtil.hasPicCached(photofilepath);
    							if (path != null && !path.equalsIgnoreCase("")) {// 下载地址存在
    							iv_suggestion_pic.setImageBitmap(BitmapFactory
    									.decodeFile(path));
    							iv_suggestion_pic.setOnClickListener(new ImageView.OnClickListener() {
    								
    								@Override
    								public void onClick(View arg0) {
    									Intent intent = new Intent();
    									intent.setClass(SuggestionDetailActivity.this, ImageViewActivity.class);
    									intent.putExtra("imagefilename", path);
    									startActivity(intent);
    								}
    							});
    						} else {// 下载地址不存在，或本地缓存文件被删除，重新下载
    							ISaleApplication application = (ISaleApplication) getApplication();
    							if (application != null) {
    								downloadtask = new FileDownloadTask(
    										application,
    										SuggestionDetailActivity.this,
    										"suggestion", photoid,
    										downloadeventlistener, false);
    								downloadtask.execute();
    							}
    						}
    					} else {// 下载地址为空，下载
    						ISaleApplication application = (ISaleApplication) getApplication();
    						if (application != null) {
    							downloadtask = new FileDownloadTask(application,
    									SuggestionDetailActivity.this,
    									"suggestion", photoid,
    									downloadeventlistener, false);
    							downloadtask.execute();
    						}
    					}

    				} else {
    					iv_suggestion_pic.setVisibility(View.GONE);
    				}
    			}
            }
        }
    }
}
