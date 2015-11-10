package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：我的客户上报界面
 */

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Area;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.bean.TerminalType;
import com.zjrc.isale.client.ui.widgets.CustomPopupDialog;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TerminalSubmitActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_titlebar_title;
	private Button btn_titlebar_back;

	private Button btn_ok;

	private EditText et_terminalcode;
	private EditText et_terminalname;
	private TextView tv_terminaltype;
	private TextView tv_terminalarea;
	private EditText et_terminaladdress;
	private EditText et_terminalzip;
	private TextView tv_terminallonlat;
	private TextView tv_terminalplacesize;
	private TextView tv_terminalemployeenum;
	private TextView tv_terminalsalenum;
	private EditText et_terminalphone;
	private EditText et_terminalfax;
	private EditText et_terminalcontactman;
	private EditText et_terminalcontactpost;
	private EditText et_terminalcontactphone;
	private EditText et_terminalcontactmobile;
	private EditText et_terminalemail;
	private TextView tv_terminalstate;

	private TextView tv_more;
	private LinearLayout ll_more;

	private Dialog terminalTypeDialog;
	private Dialog terminalAreaDialog;
	private Dialog terminalPlaceSizeDialog;
	private Dialog terminalEmployeeNumDialog;
	private Dialog terminalSaleNumDialog;
	private Dialog terminalStateDialog;

	private String operate;

	private String terminalid;

	private CustomProgressDialog progressdialog;

	// 定位相关
	private LocationClient mLocClient;
	private MyLocationListenner myListeners = new MyLocationListenner();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.terminal_submit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);

		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_titlebar_title.setText(R.string.terminaltitle_submit);

		btn_ok = (Button) findViewById(R.id.btn_ok);
		et_terminalcode = (EditText) findViewById(R.id.et_terminalcode);
		et_terminalname = (EditText) findViewById(R.id.et_terminalname);
		tv_terminaltype = (TextView) findViewById(R.id.tv_terminaltype);
		tv_terminalarea = (TextView) findViewById(R.id.tv_terminalarea);
		et_terminaladdress = (EditText) findViewById(R.id.et_terminaladdress);
		et_terminalzip = (EditText) findViewById(R.id.et_terminalzip);
		tv_terminallonlat = (TextView) findViewById(R.id.tv_terminallonlat);
		tv_terminalplacesize = (TextView) findViewById(R.id.tv_terminalplacesize);
		tv_terminalemployeenum = (TextView) findViewById(R.id.tv_terminalemployeenum);
		tv_terminalsalenum = (TextView) findViewById(R.id.tv_terminalsalenum);
		et_terminalphone = (EditText) findViewById(R.id.et_terminalphone);
		et_terminalfax = (EditText) findViewById(R.id.et_terminalfax);
		et_terminalcontactman = (EditText) findViewById(R.id.et_terminalcontactman);
		et_terminalcontactpost = (EditText) findViewById(R.id.et_terminalcontactpost);
		et_terminalcontactphone = (EditText) findViewById(R.id.et_terminalcontactphone);
		et_terminalcontactmobile = (EditText) findViewById(R.id.et_terminalcontactmobile);
		et_terminalemail = (EditText) findViewById(R.id.et_terminalemail);
		tv_terminalstate = (TextView) findViewById(R.id.tv_terminalstate);
		tv_more = (TextView) findViewById(R.id.tv_more);
		ll_more = (LinearLayout) findViewById(R.id.ll_more);

		btn_ok.setOnClickListener(this);
		tv_terminaltype.setOnClickListener(this);
		tv_terminalarea.setOnClickListener(this);
		tv_terminallonlat.setOnClickListener(this);
		tv_terminalplacesize.setOnClickListener(this);
		tv_terminalemployeenum.setOnClickListener(this);
		tv_terminalsalenum.setOnClickListener(this);
		tv_terminalstate.setOnClickListener(this);
		tv_more.setOnClickListener(this);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();

		operate = bundle.getString("operate");
		if (operate.equalsIgnoreCase("insert")) {
			tv_titlebar_title.setText("新增客户");
			startLocation();
		} else if (operate.equalsIgnoreCase("modify")) {
			tv_titlebar_title.setText("修改客户");
			terminalid = bundle.getString("terminalid");
			sendQueryDetail();
		}
	}

	@Override
	public void onClick(View v) {
		if (CommonUtils.isNotFastDoubleClick()) {
			switch (v.getId()) {
			case R.id.btn_ok:
				if (validityInput()) {
					sendSubmit();
				}
				break;
			case R.id.tv_terminaltype:
				if (terminalTypeDialog == null) {
					ArrayList<String> terminaltypes = new ArrayList<String>();
					ISaleApplication application = (ISaleApplication) getApplication();
					if (application != null
							&& application.getTerminaltypes() != null) {
						for (TerminalType terminaltype : application
								.getTerminaltypes()) {
							terminaltypes.add(terminaltype.getName());
						}
					}
					
					terminalTypeDialog = new CustomPopupDialog(this, "客户类型",
							terminaltypes.toArray(new String[0]),
							new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							tv_terminaltype.setText(((TextView) view)
									.getText());
							tv_terminaltype.setTag(position);
							terminalTypeDialog.dismiss();
						}
					});
				}
				terminalTypeDialog.show();
				break;
			case R.id.tv_terminalarea:
				if (terminalAreaDialog == null) {
					ArrayList<String> terminaltypes = new ArrayList<String>();
					ISaleApplication application = (ISaleApplication) getApplication();
					if (application != null && application.getAreas() != null) {
						for (Area area : application.getAreas()) {
							terminaltypes.add(area.getName());
						}
					}
					
					terminalAreaDialog = new CustomPopupDialog(this, "所属区域",
							terminaltypes.toArray(new String[0]),
							new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							tv_terminalarea.setText(((TextView) view)
									.getText());
							tv_terminalarea.setTag(position);
							terminalAreaDialog.dismiss();
						}
					});
				}
				terminalAreaDialog.show();
				break;
			case R.id.tv_terminallonlat:
				startLocation();
				break;
			case R.id.tv_terminalplacesize:
				if (terminalPlaceSizeDialog == null) {
					terminalPlaceSizeDialog = new CustomPopupDialog(this, "营业面积",
							getResources().getStringArray(
									R.array.terminalplacesizes),
									new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							tv_terminalplacesize.setText(((TextView) view)
									.getText());
							tv_terminalplacesize.setTag(position);
							terminalPlaceSizeDialog.dismiss();
						}
					});
				}
				terminalPlaceSizeDialog.show();
				break;
			case R.id.tv_terminalemployeenum:
				if (terminalEmployeeNumDialog == null) {
					terminalEmployeeNumDialog = new CustomPopupDialog(this, "人员规模",
							getResources().getStringArray(
									R.array.terminalemployeenums),
									new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							tv_terminalemployeenum
							.setText(((TextView) view).getText());
							tv_terminalemployeenum.setTag(position);
							terminalEmployeeNumDialog.dismiss();
						}
					});
				}
				terminalEmployeeNumDialog.show();
				break;
			case R.id.tv_terminalsalenum:
				if (terminalSaleNumDialog == null) {
					terminalSaleNumDialog = new CustomPopupDialog(
							this,
							"年销售额",
							getResources().getStringArray(R.array.terminalsalenums),
							new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									tv_terminalsalenum.setText(((TextView) view)
											.getText());
									tv_terminalsalenum.setTag(position);
									terminalSaleNumDialog.dismiss();
								}
							});
				}
				terminalSaleNumDialog.show();
				break;
			case R.id.tv_terminalstate:
				if (terminalStateDialog == null) {
					terminalStateDialog = new CustomPopupDialog(this, "客户状态",
							getResources().getStringArray(R.array.terminalstates),
							new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							tv_terminalstate.setText(((TextView) view)
									.getText());
							tv_terminalstate.setTag(position);
							terminalStateDialog.dismiss();
						}
					});
				}
				terminalStateDialog.show();
				break;
			case R.id.tv_more:
				if (ll_more.getVisibility() == View.GONE) {
					ll_more.setVisibility(View.VISIBLE);
				} else {
					ll_more.setVisibility(View.GONE);
				}
				break;
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

	@Override
	public void onRecvData(XmlNode response) {
	}

	private boolean validityInput() {
		String sCode = et_terminalcode.getText().toString();
		if ("".equalsIgnoreCase(sCode)) {
			showAlertDialog("提示", "客户编号不能为空,请输入客户编号!",
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_terminalcode.requestFocus();
			return false;
		}
		String sName = et_terminalname.getText().toString();
		if ("".equalsIgnoreCase(sName)) {
			showAlertDialog("提示", "客户名称不能为空,请输入客户名称!",
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_terminalname.requestFocus();
			return false;
		}
		String sType = tv_terminaltype.getText().toString();
		if ("".equalsIgnoreCase(sType)) {
			showAlertDialog("提示", "客户类型不能为空,请选择客户类型!",
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_terminaltype.requestFocus();
			return false;
		}
		String sArea = tv_terminalarea.getText().toString();
		if ("".equalsIgnoreCase(sArea)) {
			showAlertDialog("提示", "所属区域不能为空,请选择所属区域!",
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_terminalarea.requestFocus();
			return false;
		}
		String sAddress = et_terminaladdress.getText().toString();
		if ("".equalsIgnoreCase(sAddress)) {
			showAlertDialog("提示", "客户地址不能为空,请输入客户地址!",
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_terminaladdress.requestFocus();
			return false;
		}
		String sLongitude = tv_terminallonlat.getText().toString();
		if ("".equalsIgnoreCase(sLongitude)) {
			showAlertDialog("提示", "经纬度能为空,请选择网点位置!",
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			tv_terminallonlat.requestFocus();
			return false;
		}
		String sContactman = et_terminalcontactman.getText().toString();
		if ("".equalsIgnoreCase(sContactman)) {
			showAlertDialog("提示", "联系人不能为空,请输入联系人!",
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_terminalcontactman.requestFocus();
			return false;
		}
		String sContactmobile = et_terminalcontactmobile.getText().toString();
		if ("".equalsIgnoreCase(sContactmobile)) {
			showAlertDialog("提示", "手机号不能为空,请输入手机号码!",
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							alertDialog.cancel();
						}
					}, "确定", null, null);
			et_terminalcontactmobile.requestFocus();
			return false;
		}
		return true;
	}

	private void sendSubmit() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("operate",operate);
            params.put("companyid",application.getConfig().getCompanyid());
            params.put("userid",XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("id",XmlValueUtil.encodeString(terminalid));
            params.put("code", XmlValueUtil.encodeString(et_terminalcode.getText()
                    .toString()));
            params.put("name",XmlValueUtil.encodeString(et_terminalname.getText()
                    .toString()));
            params.put("typeid",(tv_terminaltype.getTag() != null
                        && application.getTerminaltypes() != null
                        && application.getTerminaltypes().size() > 0)?application.getTerminaltypes()
                        .get((Integer) tv_terminaltype.getTag())
                        .getId():"");
            params.put("areaid",(tv_terminalarea.getTag() != null
                    && application.getAreas() != null
                    && application.getAreas().size() > 0)?application.getAreas()
                    .get((Integer) tv_terminalarea.getTag())
                    .getId():"");
            params.put("address",XmlValueUtil.encodeString(et_terminaladdress.getText()
                    .toString()));
            params.put("zip",XmlValueUtil.encodeString(et_terminalzip.getText()
                    .toString()));
            String[] lonlat = tv_terminallonlat.getText().toString().split(",");
            if (lonlat != null && lonlat.length == 2) {
                params.put("longitude",XmlValueUtil.encodeString(lonlat[0].trim()));
                params.put("latitude",XmlValueUtil.encodeString(lonlat[1].trim()));
            } else {
                params.put("longitude","");
                params.put("latitude","");
            }
            params.put("placesize",(tv_terminalplacesize.getTag() != null ? tv_terminalplacesize
                    .getTag().toString():""));
            params.put("employeenum",(tv_terminalemployeenum.getTag() != null ? tv_terminalemployeenum
                    .getTag().toString() : ""));
            params.put("salenum",(tv_terminalsalenum.getTag() != null ? tv_terminalsalenum
                    .getTag().toString() : ""));
            params.put("phone",XmlValueUtil.encodeString(et_terminalphone.getText()
                    .toString()));
            params.put("mobile","");
            params.put("fax",XmlValueUtil.encodeString(et_terminalfax.getText()
                    .toString()));
            params.put("email",XmlValueUtil.encodeString(et_terminalemail.getText()
                    .toString()));
            params.put("contactman",XmlValueUtil.encodeString(et_terminalcontactman.getText()
                    .toString()));
            params.put("contactpost",XmlValueUtil.encodeString(et_terminalcontactpost
                    .getText().toString()));
            params.put("contactphone",XmlValueUtil.encodeString(et_terminalcontactphone
                    .getText().toString()));
            params.put("contactmobile",XmlValueUtil.encodeString(et_terminalcontactmobile
                    .getText().toString()));
            params.put("state",(tv_terminalstate.getTag() != null ? tv_terminalstate
                    .getTag().toString() : ""));
            params.put("fileid","");
            request("terminal!submit?code=" + Constant.TERMINAL_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private void startLocation() {
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListeners);

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
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		progressdialog.show();
		progressdialog.setMessage("正在定位,请稍等...");
	}

	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null && location.getLatitude() != 4.9E-324
					&& location.getLongitude() != 4.9E-324) {
				tv_terminallonlat.setText(String.valueOf(location
						.getLongitude()
						+ ", "
						+ String.valueOf(location.getLatitude())));
				if (operate.equalsIgnoreCase("insert")) {
					if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
						et_terminaladdress.setText(location.getAddrStr());
					}
				}
				mLocClient.stop();
				if (progressdialog != null) {
					progressdialog.cancel();
					progressdialog = null;
				}
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TERMINAL_SUBMIT.equals(code)) {
                if (operate.equalsIgnoreCase("insert")) {
                    Toast.makeText(this, "新增客户成功!", Toast.LENGTH_SHORT).show();
                } else if (operate.equalsIgnoreCase("modify")) {
                    Toast.makeText(this, "修改客户成功!", Toast.LENGTH_SHORT).show();
                }
                setResult(RESULT_OK);
                finish();
            } else if (Constant.TERMINAL_DETAIL.equals(code)) {//网点详情
            	JsonObject backorder = response.getAsJsonObject("body");
                if(backorder != null) {
    				ISaleApplication application = (ISaleApplication) getApplication();
    				if (application != null) {
    					int iposition = 0;
    					String itext = "";

    					et_terminalcode.setText(backorder.get("code").getAsString());
    					et_terminalname.setText(backorder.get("name").getAsString());
    					for (int i = 0; i < application.getTerminaltypes().size(); i++) {
    						TerminalType terminaltype = application.getTerminaltypes().get(i);
    						if (terminaltype.getId().equalsIgnoreCase(
    								backorder.get("typeid").getAsString())) {
    							iposition = i;
    							itext = terminaltype.getName();
    							break;
    						}
    					}
    					tv_terminaltype.setText(itext);
    					tv_terminaltype.setTag(iposition);
    					for (int i = 0; i < application.getAreas().size(); i++) {
    						Area area = application.getAreas().get(i);
    						if (area.getId().equalsIgnoreCase(
    								backorder.get("areaid").getAsString())) {
    							iposition = i;
    							itext = area.getName();
    							break;
    						}
    					}
    					tv_terminalarea.setText(itext);
    					tv_terminalarea.setTag(iposition);
    					et_terminaladdress.setText(backorder.get("address").getAsString());
    					et_terminalzip.setText(backorder.get("zip").getAsString());
    					tv_terminallonlat.setText(backorder.get("longitude").getAsString()
    							+ ", " + backorder.get("latitude").getAsString());
    					try {
    						iposition = Integer.valueOf(backorder.get("placesize").getAsString());
    					} catch (Exception e1) {
    						iposition = -1;
    					}
    					String[] placesizes = getResources().getStringArray(
    							R.array.terminalplacesizes);
    					if (iposition != -1 && iposition < placesizes.length) {
    						tv_terminalplacesize.setText(placesizes[iposition]);
    						tv_terminalplacesize.setTag(iposition);
    					}
    					try {
    						iposition = Integer.valueOf(backorder.get("employeenum").getAsString());
    					} catch (Exception e1) {
    						iposition = -1;
    					}
    					String[] employeenums = getResources().getStringArray(
    							R.array.terminalemployeenums);
    					if (iposition != -1 && iposition < employeenums.length) {
    						tv_terminalemployeenum.setText(employeenums[iposition]);
    						tv_terminalemployeenum.setTag(iposition);
    					}
    					try {
    						iposition = Integer.valueOf(backorder.get("salenum").getAsString());
    					} catch (Exception e1) {
    						iposition = -1;
    					}
    					String[] salenums = getResources().getStringArray(
    							R.array.terminalsalenums);
    					if (iposition != -1 && iposition < salenums.length) {
    						tv_terminalsalenum.setText(salenums[iposition]);
    						tv_terminalsalenum.setTag(iposition);
    					}
    					et_terminalphone
    							.setText(backorder.get("phone").getAsString());
    					et_terminalfax.setText(backorder.get("fax").getAsString());
    					et_terminalemail
    							.setText(backorder.get("email").getAsString());
    					et_terminalcontactman.setText(backorder.get("contactman").getAsString());
    					et_terminalcontactpost.setText(backorder.get("contactpost").getAsString());
    					et_terminalcontactphone.setText(backorder.get("contactphone").getAsString());
    					et_terminalcontactmobile.setText(backorder.get("contactmobile").getAsString());
    					try {
    						iposition = Integer.valueOf(backorder.get("state").getAsString());
    					} catch (Exception e1) {
    						iposition = -1;
    					}
    					String[] states = getResources().getStringArray(
    							R.array.terminalstates);
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
