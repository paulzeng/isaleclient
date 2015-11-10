package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：考勤上报界面
 */
public class AttenceSubmitActivity extends BaseActivity {

	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;
	private TextView tv_username;
	private TextView tv_date;
	private TextView tv_time;
	private TextView tv_address;
	private TextView tv_type;
	private TextView tv_result;

	private Button btn_check;

	private RelativeLayout rl_button;
	private RelativeLayout rl_type;
	private RelativeLayout rl_result;

	private CustomProgressDialog progressdialog;

	// 定位相关
	private LocationClient mLocClient;

	private MyLocationListenner myListener = new MyLocationListenner();

	private boolean locationed;
	private boolean islocation;

	private int attencetype;
	private String longitude="";
	private String latitude="";
	private String address="";
	private String checkState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.attence_submit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);

		btn_check = (Button) findViewById(R.id.btn_check);

		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_type = (TextView) findViewById(R.id.tv_type);
		tv_result = (TextView) findViewById(R.id.tv_result);
		rl_button = (RelativeLayout) findViewById(R.id.rl_button);
		rl_type = (RelativeLayout) findViewById(R.id.rl_type);
		rl_result = (RelativeLayout) findViewById(R.id.rl_result);
		Intent intent = getIntent();
		String oper = intent.getStringExtra("oper");
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			tv_username.setText(application.getConfig().getUsername());
		}
		if ("0".equals(oper)) {
			tv_titlebar_title.setText(R.string.attence_view);
			attenceitem(intent.getStringExtra("bizworkcheckid"));
		} else {
			checkState = intent.getStringExtra("method");
			tv_titlebar_title.setText(R.string.attence_submit);
			attencesubmit();
		}
	}

	private void attenceitem(String stringExtra) {
		rl_type.setVisibility(RelativeLayout.VISIBLE);
		rl_result.setVisibility(RelativeLayout.VISIBLE);
		rl_button.setVisibility(RelativeLayout.VISIBLE);
		btn_check.setVisibility(View.GONE);
		request(stringExtra);

	}

	private void attencesubmit() {

		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd");
		tv_date.setText(dateformat1.format(new Date()));

		SimpleDateFormat dateformat2 = new SimpleDateFormat("HH:mm:ss");
		tv_time.setText(dateformat2.format(new Date()));

		rl_button.setVisibility(RelativeLayout.VISIBLE);

		if (checkState.equals("checkin")) {
			btn_check.setText("签到");
		} else if (checkState.equals("checkout")) {
			btn_check.setText("签退");
		}

		btn_check.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!locationed) {
						Toast.makeText(AttenceSubmitActivity.this,
								"抱歉，请等待定位完成再提交!", Toast.LENGTH_SHORT).show();
						progressdialog = new CustomProgressDialog(
								AttenceSubmitActivity.this,
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										if (islocation) {
											mLocClient
													.unRegisterLocationListener(myListener);
											mLocClient.stop();
										}
										finish();
									}
								});
						progressdialog.show();
						progressdialog.setMessage("正在定位,请稍等...");
						return;
					
				}else{
					if (checkState.equals("checkin")) {
						attencetype = 0;
						sendSubmit("0");
					} else if (checkState.equals("checkout")) {
						attencetype = 1;
						sendSubmit("1");
					}
				}
			}
		});

		locationed = false;
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);

		mLocClient.start();

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为4000ms
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(1); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		mLocClient.setLocOption(option);
		mLocClient.requestLocation();
		progressdialog = new CustomProgressDialog(this,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		progressdialog.show();
		progressdialog.setMessage("正在定位,请稍等...");
	}

	public void request(String id) {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("bizworkcheckid", id);
            request("attenceCheck!detail?code=" + Constant.ATTENCE_ITEM, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	private void sendSubmit(String attencetype) {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("userid", application.getConfig().getUserid());
            params.put("type", attencetype);
            params.put("longitude", longitude);
            params.put("latitude", latitude);
            params.put("address", tv_address.getText().toString());
            request("attenceCheck!submit?code=" + Constant.ATTENCE_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.ATTENCE_ITEM.equals(code)) {//考勤登记详情
            	JsonObject attence = response.getAsJsonObject("body");
                if(attence != null) {
                	tv_date.setText(attence.get("date").getAsString());
    				tv_time.setText(attence.get("time").getAsString());
    				longitude = attence.get("longitude").getAsString();
    				latitude = attence.get("latitude").getAsString();
    				tv_address.setText(attence.get("address").getAsString());

        			String type = attence.get("type").getAsString();
        			Integer typeindex = Integer.valueOf(type);
        			String result = attence.get("attenceresult").getAsString();
        			Integer resultindex = Integer.valueOf(result);
        			tv_type.setText((this.getResources()
        					.getStringArray(R.array.attencetypes))[typeindex]);
        			tv_result.setText((this.getResources()
        					.getStringArray(R.array.attenceresult))[resultindex]);
                }
            } else if (Constant.ATTENCE_SUBMIT.equals(code)) {//上报考勤登记
            	Intent intent = new Intent();
				if (attencetype == 0) {
					Toast.makeText(this, "上班签到成功!", Toast.LENGTH_SHORT).show();
					intent.setAction(Constant.ATTENCE_CHECKIN_ACTION);
				} else {
					Toast.makeText(this, "下班签退成功!", Toast.LENGTH_SHORT).show();
					intent.setAction(Constant.ATTENCE_CHECKOUT_ACTION);
				}
				sendBroadcast(intent);
				finish();
            }
            
        }
    }

    public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			locationed = true;
			longitude = String.valueOf(location.getLongitude());
			latitude = String.valueOf(location.getLatitude());
			mLocClient.stop();
			if (progressdialog != null) {
				progressdialog.cancel();
				progressdialog = null;
			}
			
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				address = location.getAddrStr();
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				address = location.getAddrStr();
			}else{
				address = "暂时无法定位";
				 showAlertDialog("提示", "定位失败请重试!", new View.OnClickListener() {
					 @Override
					 public void onClick(View arg0) {
					 alertDialog.cancel();
					 locationed = false;
					 mLocClient = new LocationClient(getApplicationContext());
					 mLocClient.registerLocationListener(myListener);
					 mLocClient.start();
					 LocationClientOption option = new LocationClientOption();
					 option.setOpenGps(true);
					 option.setAddrType("all");// 返回的定位结果包含地址信息
					 option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
					 option.setScanSpan(1000);// 设置发起定位请求的间隔时间为4000ms
					 option.disableCache(true);// 禁止启用缓存定位
					 option.setPoiNumber(1); // 最多返回POI个数
					 option.setPoiDistance(1000); // poi查询距离
					 option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
					 mLocClient.setLocOption(option);
					 mLocClient.requestLocation();
					 islocation = true;
					 progressdialog = new CustomProgressDialog(
					 AttenceSubmitActivity.this,
					 new View.OnClickListener() {
					 @Override
					 public void onClick(View v) {
					 if (islocation) {
					 mLocClient
					 .unRegisterLocationListener(myListener);
					 mLocClient.stop();
					 }
					 finish();
					 }
					 });
					 progressdialog.show();
					 progressdialog.setMessage("正在定位,请稍等...");
					 }
					 }, "确定", "取消", new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							 alertDialog.cancel();
						}
					});
					 tv_address.requestFocus();
				
			}
			tv_address.setText(address);

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

}
