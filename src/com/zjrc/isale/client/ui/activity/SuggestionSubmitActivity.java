package com.zjrc.isale.client.ui.activity;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 高林荣
 * @修改者： 贺彬
 * @功能描述：反馈新增修改界面
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.zjrc.isale.client.task.FileUploadTask;
import com.zjrc.isale.client.task.IUploadEventListener;
import com.zjrc.isale.client.ui.widgets.CustomPopupDialog;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;
import java.util.Map;

public class SuggestionSubmitActivity extends BaseActivity {
	private static final String TAG = "SuggestionSubmitActivity";
	private TextView tv_titlebar_title;
	private Button btn_titlebar_back;

	private EditText et_suggestion_title;
	private TextView tv_suggestion_type;
	private TextView tv_suggestion_place;
	private ImageView iv_suggestion_pic;
	private ImageView iv_suggestion_pic_delete;
	private TextView et_suggestion_content;

	private Button btn_ok;

	private String operate;

	private String suggestionid="";

	// 定位相关
	private LocationClient mLocClient;

	private MyLocationListenner myListener = new MyLocationListenner();

	private boolean islocation;

	private boolean locationed;
	private CustomPopupDialog suggestionTypeDialog;
	private CustomProgressDialog progressdialog;
	private String[] types;
	FileUploadTask uploadtask;
	String photoid = "";
	String photofilepath = "";
	private IUploadEventListener uploadeventlistener = new IUploadEventListener() {

		@Override
		public void onFinish(String filetype, String fileid) {
			if (filetype.equalsIgnoreCase("suggestion")) {
				photoid = fileid;
			}
			ISaleApplication application = (ISaleApplication) getApplication();
			if (application != null) {
				if (!photofilepath.equalsIgnoreCase("")
						&& photoid.equalsIgnoreCase("")) {
					uploadtask = new FileUploadTask(application,
							SuggestionSubmitActivity.this, "suggestion",
							photofilepath, "suggestionphoton",
							uploadeventlistener);
					uploadtask.execute();
				} else {
					sendSubmit();
				}
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
							if (!locationed) {
								Toast.makeText(SuggestionSubmitActivity.this,
										"抱歉，请等待定位完成再提交!", Toast.LENGTH_SHORT).show();
								progressdialog = new CustomProgressDialog(
										SuggestionSubmitActivity.this,
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
							}
							if (validityInput()) {
								ISaleApplication application = (ISaleApplication) getApplication();
								if (application != null) {
									if (!photofilepath.equalsIgnoreCase("")
											&& photoid.equalsIgnoreCase("")) {
										uploadtask = new FileUploadTask(
												application,
												SuggestionSubmitActivity.this,
												"suggestion", photofilepath,
												"suggestionphoto",
												uploadeventlistener);
										uploadtask.execute();
									} else {
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
		setContentView(R.layout.suggestion_submit);
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
		et_suggestion_title = (EditText) findViewById(R.id.et_suggestion_title);
		tv_suggestion_type = (TextView) findViewById(R.id.tv_suggestion_type);
		iv_suggestion_pic = (ImageView) findViewById(R.id.iv_suggestion_pics);
		iv_suggestion_pic_delete = (ImageView) findViewById(R.id.iv_suggestion_delete);
		et_suggestion_content = (TextView) findViewById(R.id.et_suggestion_content);
		tv_suggestion_place = (TextView) findViewById(R.id.tv_suggstion_place);
		btn_ok = (Button) findViewById(R.id.btn_check);
		types = getResources().getStringArray(R.array.suggestiontypes);

		tv_suggestion_type.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (suggestionTypeDialog == null) {
						suggestionTypeDialog = new CustomPopupDialog(
								SuggestionSubmitActivity.this, "反馈类型", types,
								new OnItemClickListener() {
									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										tv_suggestion_type
												.setText(((TextView) view)
														.getText());
										suggestionTypeDialog.dismiss();
										type = position;
									}
								});
					}
					suggestionTypeDialog.show();
				}
			}
		});

		iv_suggestion_pic.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					Intent intent = new Intent();
					intent.setClass(SuggestionSubmitActivity.this,
							CaptureActivity.class);
					startActivityForResult(intent,
							Constant.RESULT_SUGGESTION_DOORPHOTO);
				}
			}
		});

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();

		operate = bundle.getString("operate");

		if (operate.equalsIgnoreCase("insert")) {
			tv_titlebar_title.setText(R.string.suggestion_submit);
			btn_ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (CommonUtils.isNotFastDoubleClick()) {
						if (!locationed) {
							Toast.makeText(SuggestionSubmitActivity.this,
									"抱歉，请等待定位完成再提交!", Toast.LENGTH_SHORT).show();
							progressdialog = new CustomProgressDialog(
									SuggestionSubmitActivity.this,
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
						}
						if (validityInput()) {
							ISaleApplication application = (ISaleApplication) getApplication();
							if (application != null) {
								if (!photofilepath.equalsIgnoreCase("")
										&& photoid.equalsIgnoreCase("")) {
									uploadtask = new FileUploadTask(
											application,
											SuggestionSubmitActivity.this,
											"suggestion", photofilepath,
											"suggestionphoto",
											uploadeventlistener);
									uploadtask.execute();
								} else {
									sendSubmit();
								}
							}
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
		} else if (operate.equalsIgnoreCase("modify")) {
			// suggestionid = bundle.getString("suggestionid");
			// tv_titlebar_title.setText(R.string.suggestion_modify);
			// ll_suggestiondate.setVisibility(View.VISIBLE);
			// et_suggestioncontent.setMinLines(11);
			// btn_ok.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View arg0) {
			// if (validityInput()) {
			// sendSubmit();
			// }
			// }
			// });
			// sendQueryDetail();
			// } else if (operate.equalsIgnoreCase("view")) {
			//
			// suggestionid = bundle.getString("suggestionid");
			//
			// ll_suggestiondate.setVisibility(View.VISIBLE);
			// et_suggestioncontent.setMinLines(11);
			//
			// et_suggestiontitle.setEnabled(false);
			// ll_suggestiondate.setEnabled(false);
			//
			// btn_ok.setVisibility(View.GONE);
			//
			// sendQueryDetail();
		}
		tv_titlebar_title.requestFocus();
	}

	// /**
	// * 查询投诉建议详情
	// */
	// private void sendQueryDetail() {
	// String srequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	// srequest += "<root>";
	// srequest += "<functionno>" + Constant.SUGGESTION_DETAIL
	// + "</functionno>";
	// srequest += "<suggestionid>" + suggestionid + "</suggestionid>";
	// srequest += "</root>";
	// XmlNode requestxml = XmlParser.parserXML(srequest, "UTF-8");
	// sendRequest(requestxml);
	// }
	//
	private int type = 2;

	private void sendSubmit() {
		ISaleApplication application = (ISaleApplication) getApplication();
		if (application != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            params.put("operate", operate);
            params.put("id", suggestionid);
            params.put("title", et_suggestion_title.getText().toString());
            params.put("type", String.valueOf(type));
            params.put("content", et_suggestion_content.getText().toString());
            params.put("longitude", longitude);
            params.put("latitude", latitude);
            params.put("address", address);
            params.put("pictureid", photoid);
            request("suggestion!submit?code=" + Constant.SUGGESTION_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

	String longitude = "";
	String latitude = "";
	String address = "";
	String pictureid = "";

	private boolean validityInput() {
		String sTitle = et_suggestion_title.getText().toString();
		if ("".equalsIgnoreCase(sTitle)) {
			showAlertDialog("提示", "反馈标题不能为空,请输入反馈标题!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_suggestion_title.requestFocus();
			return false;
		}
		String sContent = et_suggestion_content.getText().toString();
		if ("".equalsIgnoreCase(sContent)) {
			showAlertDialog("提示", "反馈内容不能为空,请输入反馈内容!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_suggestion_content.requestFocus();
			return false;
		}
		if (type >= 2) {
			showAlertDialog("提示", "反馈类型不能为空,请选择反馈类型!",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_suggestion_content.requestFocus();
			return false;
		}
		return true;
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
					 SuggestionSubmitActivity.this,
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
					 tv_suggestion_place.requestFocus();
				
			}
			tv_suggestion_place.setText(address);
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.RESULT_SUGGESTION_DOORPHOTO) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				photofilepath = bundle.getString("filepath");
				iv_suggestion_pic.setImageBitmap(BitmapFactory
						.decodeFile(photofilepath));
				iv_suggestion_pic_delete.setVisibility(View.VISIBLE);
				iv_suggestion_pic_delete
						.setOnClickListener(new ImageView.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									photofilepath = "";
									iv_suggestion_pic_delete
											.setVisibility(View.GONE);
									iv_suggestion_pic
											.setImageResource(R.drawable.v2_add_pic);
									iv_suggestion_pic
											.setOnClickListener(new ImageView.OnClickListener() {

												@Override
												public void onClick(View arg0) {
													Intent intent = new Intent();
													intent.setClass(
															SuggestionSubmitActivity.this,
															CaptureActivity.class);
													startActivityForResult(
															intent,
															Constant.RESULT_SUGGESTION_DOORPHOTO);
												}
											});
								}
							}
						});
				iv_suggestion_pic
						.setOnClickListener(new ImageView.OnClickListener() {

							@Override
							public void onClick(View arg0) {
							}
						});

			}
		}
	}

    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.SUGGESTION_SUBMIT.equals(code)) {//上报投诉建议
            	Intent intent = new Intent();
				if (operate.equalsIgnoreCase("insert")) {
					Toast.makeText(this, "信息反馈上报成功!", Toast.LENGTH_SHORT)
							.show();
					intent.setAction(Constant.SUGGESTION_ADD_ACTION);
				} else if (operate.equalsIgnoreCase("modify")) {
					Toast.makeText(this, "信息反馈修改成功!", Toast.LENGTH_SHORT)
							.show();
					intent.setAction(Constant.SUGGESTION_MODIFY_ACTION);
				}
				sendBroadcast(intent);
				finish();
            }
        }
    }
}
