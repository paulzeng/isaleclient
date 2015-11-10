package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.task.FileDownloadTask;
import com.zjrc.isale.client.task.FileUploadTask;
import com.zjrc.isale.client.task.IUploadEventListener;
import com.zjrc.isale.client.ui.adapter.PlanPicsAdapter;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;
import com.zjrc.isale.client.util.LogUtil;
import com.zjrc.isale.client.util.MD5;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：拜访计划执行界面
 */
public class PlanPatrolSubmitActivity extends BaseActivity {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;

	private Button btn_submit;

	private TextView tv_plan_place;
	private TextView tv_plan_time;

	private GridView gv_plan_pics;

	private EditText et_plan_content;
	private TextView tv_terminal;
	private TextView tv_date;
	private TextView tv_content;
	private TextView tv_state;
	private TextView tv_result;
	private TextView tv_type;
	private String planid;

	private String terminalid;

	private CustomProgressDialog progressdialog;

	private LocationClient mLocClient;

	private RelativeLayout rl_submit_msg;

	private RelativeLayout rl_submit_info;

	private RelativeLayout rl_audit_msg;

	private RelativeLayout rl_audit_info;

	private MyLocationListenner myListener = new MyLocationListenner();

	private boolean islocation;

	private boolean locationed;

	private FileUploadTask uploadtask;

	private FileDownloadTask downloadtask;

	private String longitude;
	private String latitude;
	private String address;
	private boolean isSubmitInfoVisible = false;
	private boolean isAuditInfoVisible = false;

	private ArrayList<PicItem> pics = new ArrayList<PicItem>();
	private PlanPicsAdapter adapter;

	private IUploadEventListener uploadeventlistener = new IUploadEventListener() {

		@Override
		public void onFinish(String filetype, String fileid) {

			int count = pics.size();
			for (int i = 0; i < count; i++) {
				if (pics.get(i).fileType.equalsIgnoreCase(filetype)) {
					pics.get(i).id = fileid;
					LogUtil.i("info", "fileType:" + filetype);
					LogUtil.i("info", "fileid:" + fileid);
				}
			}

			ISaleApplication application = ISaleApplication.getInstance();
			if (application != null) {
				for (int i = 0; i < count; i++) {
					if (pics.get(i).type == 1) {
						if (pics.get(i).id.equalsIgnoreCase("")) {
							uploadtask = new FileUploadTask(application,
									PlanPatrolSubmitActivity.this,
									pics.get(i).fileType, pics.get(i).fileName,
									pics.get(i).des, uploadeventlistener);
							uploadtask.execute();
							return;
						}
					}
				}
				sendSubmit();
			}
		}


		@Override
		public void onFailed(String filetype, String message) {
			if (filetype.equals("cancel")) {

				showAlertDialogNormal("提示", "已取消拜访执行上报", true);

			} else {

				showAlertDialog("提示", "详情上报失败", new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (CommonUtils.isNotFastDoubleClick()) {
							alertDialog.cancel();
							if (!locationed) {
								Toast.makeText(PlanPatrolSubmitActivity.this,
										"抱歉，请等待定位完成再提交!", Toast.LENGTH_SHORT).show();
								progressdialog = new CustomProgressDialog(
										PlanPatrolSubmitActivity.this,
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
							if (validityInput()) {
								ISaleApplication application = ISaleApplication.getInstance();
								if (application != null) {
									int count = pics.size();
									for (int i = 0; i < count; i++) {
										if (pics.get(i).type == 1) {
											if (pics.get(i).id.equalsIgnoreCase("")) {
												uploadtask = new FileUploadTask(
														application,
														PlanPatrolSubmitActivity.this,
														pics.get(i).fileType, pics
																.get(i).fileName, pics
																.get(i).des,
														uploadeventlistener);
												uploadtask.execute();
												picIndex = i;
												return;
											}
										}
									}
									sendSubmit();
								}
							}
							
						}
						}
					}
				}, "重试", "取消", new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						alertDialog.cancel();
					}
				});
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.plan_patrol_submit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);

		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		btn_submit = (Button) findViewById(R.id.btn_check);
		btn_submit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (!locationed) {
						Toast.makeText(PlanPatrolSubmitActivity.this,
								"抱歉，请等待定位完成再提交!", Toast.LENGTH_SHORT).show();
						progressdialog = new CustomProgressDialog(
								PlanPatrolSubmitActivity.this,
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
					if (validityInput()) {
						ISaleApplication application = ISaleApplication.getInstance();
						if (application != null) {
							int count = pics.size();
							for (int i = 0; i < count; i++) {
								if (pics.get(i).type == 1) {
									if (pics.get(i).id.equalsIgnoreCase("")) {
										uploadtask = new FileUploadTask(
												application,
												PlanPatrolSubmitActivity.this,
												pics.get(i).fileType, pics
														.get(i).fileName, pics
														.get(i).des,
												uploadeventlistener);
										uploadtask.execute();
										picIndex = i;
										return;
									}
								}
							}
							sendSubmit();
						}
					}
					
				}
				}
			}
		});

		tv_plan_place = (TextView) findViewById(R.id.tv_plan_place);
		tv_plan_time = (TextView) findViewById(R.id.tv_plan_time);
		gv_plan_pics = (GridView) findViewById(R.id.gv_plan_pics);
		et_plan_content = (EditText) findViewById(R.id.tv_plan_content);
		rl_submit_msg = (RelativeLayout) findViewById(R.id.rl_submit_msg);
		rl_submit_info = (RelativeLayout) findViewById(R.id.rl_submit_info);
		rl_audit_msg = (RelativeLayout) findViewById(R.id.rl_audit_msg);
		rl_audit_info = (RelativeLayout) findViewById(R.id.rl_audit_info);
		tv_type = (TextView)findViewById(R.id.tv_type);
		tv_terminal = (TextView) findViewById(R.id.tv_terminal);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_state = (TextView) findViewById(R.id.tv_state_2);
		tv_result = (TextView) findViewById(R.id.tv_result);

		if (!needAudit) {
			rl_audit_msg.setVisibility(View.GONE);
		}

		rl_submit_msg.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!isSubmitInfoVisible) {
					rl_submit_info.setVisibility(View.VISIBLE);
					isSubmitInfoVisible = true;
				} else {
					rl_submit_info.setVisibility(View.GONE);
					isSubmitInfoVisible = false;
				}
			}
		});
		rl_audit_msg.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!isAuditInfoVisible) {
					rl_audit_info.setVisibility(View.VISIBLE);
					isAuditInfoVisible = true;
				} else {
					rl_audit_info.setVisibility(View.GONE);
					isAuditInfoVisible = false;
				}
			}
		});
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		tv_titlebar_title.setText(R.string.planpatrol_submit);
		planid = bundle.getString("planid");
		sendQueryPlan();
		pics.add(new PicItem("", "", "", "", 0));
		adapter = new PlanPicsAdapter(this, pics);
		gv_plan_pics.setAdapter(adapter);
		gv_plan_pics.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				if (index < 3) {
					if (pics.get(index).type == 0) {
						Intent intent = new Intent();
						intent.setClass(PlanPatrolSubmitActivity.this,
								CaptureActivity.class);
						picIndex = index;
						startActivityForResult(intent, picIndex);
					} else {

					}
				}
			}
		});

	}

	private int picIndex = 0;
	private String doorphotofilepath = "";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == picIndex) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				doorphotofilepath = bundle.getString("filepath");
				if (picIndex == 2) {
					pics.remove(2);
				}
				if (picIndex == 0) {
					pics.add(
							0,
							new PicItem("door", MD5.getMD5(String
									.valueOf(new Date().getTime())),
									doorphotofilepath, "", 1));
				} else if (picIndex == 1) {
					pics.add(
							1,
							new PicItem("product", MD5.getMD5(String
									.valueOf(new Date().getTime())),
									doorphotofilepath, "", 1));
				} else if (picIndex == 2) {
					pics.add(
							2,
							new PicItem("contendproduct", MD5.getMD5(String
									.valueOf(new Date().getTime())),
									doorphotofilepath, "", 1));
				}

				adapter.notifyDataSetChanged();
				// et_doorphoto.setHint(R.string.planpatrol_photohint);
				// et_doorphoto.setEnabled(true);
				// iv_plan_pic_1.setImageBitmap(BitmapFactory
				// .decodeFile(doorphotofilepath));
				// iv_plan_pic_2.setVisibility(View.VISIBLE);
				// iv_plan_pic_2.setOnClickListener(new View.OnClickListener() {
				// @Override
				// public void onClick(View arg0) {
				// Intent intent = new Intent();
				// intent.setClass(PlanPatrolSubmitActivity.this,
				// CaptureActivity.class);
				// startActivityForResult(intent,
				// Constant.RESULT_PATROL_PRODUCTPHOTO);
				// }
				// });
			}

		}
	}

	@Override
	protected void onDestroy() {
		if (uploadtask != null) {
			uploadtask.cancelTask();
			uploadtask = null;
		}
		if (downloadtask != null) {
			downloadtask.cancelTask();
			downloadtask = null;
		}
		super.onDestroy();
	}

	private void sendQueryPlan() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("planid", XmlValueUtil.encodeString(planid));
        request("plan!detail?code=" + Constant.PLAN_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
	}

	private void sendSubmit() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("type", "0");
            params.put("planid",  XmlValueUtil.encodeString(planid));
            params.put("terminalid",  XmlValueUtil.encodeString(terminalid));
            params.put("content",  XmlValueUtil.encodeString(et_plan_content.getText()
                    .toString()));
            params.put("longitude",  XmlValueUtil.encodeString(longitude));
            params.put("latitude",   XmlValueUtil.encodeString(latitude));
            params.put("address",   XmlValueUtil.encodeString(!tv_plan_place.getText()
                    .toString().equalsIgnoreCase("")?(tv_plan_place.getText()
                    .toString()):"获取不到位置信息"));
            params.put("address",   XmlValueUtil.encodeString(!tv_plan_place.getText()
                    .toString().equalsIgnoreCase("")?(tv_plan_place.getText()
                    .toString()):"获取不到位置信息"));
            int size = pics.size();
            if (size > 0 && pics.get(0) != null) {
                if (pics.get(0).type == 1) {
                    params.put("doorphotoid",  XmlValueUtil.encodeString(pics.get(0).id));
                } else {
                    params.put("doorphotoid",  XmlValueUtil.encodeString(""));
                }
            } else {
                params.put("doorphotoid",  XmlValueUtil.encodeString(""));
            }

            if (size > 1 && pics.get(1) != null) {
                if (pics.get(1).type == 1) {
                    params.put("productphotoid",  XmlValueUtil.encodeString(pics.get(1).id));

                } else {
                    params.put("productphotoid",   XmlValueUtil.encodeString(""));
                }
            } else {
                params.put("productphotoid",   XmlValueUtil.encodeString(""));
            }
            if (size > 2 && pics.get(2) != null) {
                if (pics.get(2).type == 1) {
                    params.put("contendproductphotoid",  XmlValueUtil.encodeString(pics.get(2).id));
                } else {
                    params.put("contendproductphotoid", XmlValueUtil.encodeString(""));
                }
            } else {
                params.put("contendproductphotoid", XmlValueUtil.encodeString(""));
            }
            request("patrol!submit?code=" + Constant.PATROL_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private boolean validityInput() {
		String sPatrolContent = et_plan_content.getText().toString();
		if ("".equalsIgnoreCase(sPatrolContent)) {
			showAlertDialog("提示", "执行内容不能为空,请输入执行内容!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_plan_content.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public void onRecvData(XmlNode response) {


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
			islocation = false;
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
					 PlanPatrolSubmitActivity.this,
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
				 tv_plan_place.requestFocus();
				
			}
			tv_plan_place.setText(address);

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public static class PicItem {
		public String des;
		public String fileName;
		public String fileType;
		public String id;
		public int type;

		public PicItem(String fileType, String des, String fileName, String id,
				int type) {
			this.des = des;
			this.fileName = fileName;
			this.type = type;
			this.fileType = fileType;
			this.id = id;
		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.PLAN_QUERY.equals(code)) {
                JsonObject plan = response.getAsJsonObject("body");
                String plantype =plan.get("plantype").getAsString();
                if (plantype.equalsIgnoreCase("0")) {
                    tv_type.setText("临时任务");
                }else{
                    tv_type.setText("常规任务");
                }
                String signState =plan.get("signstate").getAsString();
                if (signState.equalsIgnoreCase("0")) {
                    tv_state.setText("未审批");
                } else {
                    tv_state.setText("已审批");
                    tv_result.setText(plan.get("signcontent").getAsString());
                }
                tv_date.setText(plan.get("plandate").getAsString());
                tv_terminal.setText(plan.get("terminalname").getAsString());
                tv_content.setText(plan.get("plancontent").getAsString());
                terminalid =plan.get("terminalid").getAsString();
                tv_plan_time.setText(new SimpleDateFormat(getResources()
                        .getString(R.string.year_mouth_day_name_format))
                        .format(new Date()));
                tv_plan_place
                        .setText(plan.get("terminalname").getAsString());
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
                progressdialog = new CustomProgressDialog(this,
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

            }else if(Constant.PATROL_SUBMIT.equals(code)){
                JsonObject patrol = response.getAsJsonObject("body");
                Toast.makeText(this, "拜访执行成功!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Constant.PLAN_EXECUTION_ACTION);
                sendBroadcast(intent);
                Intent intent2 = new Intent();
                intent2.setClass(PlanPatrolSubmitActivity.this,
                        PlanDetailActivity.class);
                intent2.putExtra("planid", planid);
                intent2.putExtra("patrolid",patrol.get("patrolid").getAsString());
                startActivity(intent2);
                finish();
            }
        }
    }
}
