package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.task.FileUploadTask;
import com.zjrc.isale.client.task.IUploadEventListener;
import com.zjrc.isale.client.ui.widgets.CustomDatePicker;
import com.zjrc.isale.client.util.DecimalDigitLimitFilter;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：销售活动上报界面
 */
public class PromotionSubmitActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_barcode;

    private TextView tv_promotionterminal;
    private EditText et_promotiondesc;
    private TextView tv_promotionbegindate;
    private TextView tv_promotionenddate;
    private TextView tv_promotionproduct;
    private EditText et_promotionbrand;
    private EditText et_promotioncategory;
    private EditText et_promotionnorm;
    private EditText et_promotionprice;
    private EditText et_promotionnum;
    private EditText et_promotionphoto;
    private ImageView iv_takephoto;
    private RelativeLayout rl_photo;
    private ImageView iv_photo;
    private ImageView iv_cancelphoto;
    private Button btn_ok;

    //所选择的销售网点ID号
    private String terminalid;
    private String terminalcode;
    private String terminalname;

    private String productid;
    private String productname;

    private String photofileid;
    private String photofilepath;

    private FileUploadTask uploadtask;

    private IUploadEventListener uploadeventlistener = new IUploadEventListener() {
        @Override
        public void onFinish(String filetype, String fileid) {
            if (filetype.equalsIgnoreCase("promotion")) {
                photofileid = fileid;
            }

            if (!photofileid.equalsIgnoreCase("")) {
                sendSubmit();
            }
        }

		@Override
		public void onFailed(String filetype, String message) {
			// TODO Auto-generated method stub

		}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.promotion_submit);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.promotion_submit);

        btn_titlebar_barcode = (Button) findViewById(R.id.btn_titlebar_barcode);
        btn_titlebar_barcode.setVisibility(View.VISIBLE);
        btn_titlebar_barcode.setOnClickListener(this);

        tv_promotionterminal = (TextView) findViewById(R.id.tv_promotionterminal);
        tv_promotionterminal.setOnClickListener(this);

        et_promotiondesc = (EditText) findViewById(R.id.et_promotiondesc);

        tv_promotionbegindate = (TextView) findViewById(R.id.tv_promotionbegindate);
        tv_promotionbegindate.setOnTouchListener(this);

        tv_promotionenddate = (TextView) findViewById(R.id.tv_promotionenddate);
        tv_promotionenddate.setOnTouchListener(this);

        tv_promotionproduct = (TextView) findViewById(R.id.tv_promotionproduct);
        tv_promotionproduct.setOnClickListener(this);

        et_promotionbrand = (EditText) findViewById(R.id.et_promotionbrand);

        et_promotioncategory = (EditText) findViewById(R.id.et_promotioncategory);

        et_promotionnorm = (EditText) findViewById(R.id.et_promotionnorm);

        et_promotionprice = (EditText) findViewById(R.id.et_promotionprice);
        et_promotionprice.setFilters(new InputFilter[] {
                new DecimalDigitLimitFilter(10, 2)
        });

        et_promotionnum = (EditText) findViewById(R.id.et_promotionnum);

        et_promotionphoto = (EditText) findViewById(R.id.et_promotionphoto);

        iv_takephoto = (ImageView) findViewById(R.id.iv_takephoto);
        iv_takephoto.setOnClickListener(this);

        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);

        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        iv_photo.setOnClickListener(this);

        iv_cancelphoto = (ImageView) findViewById(R.id.iv_cancelphoto);
        iv_cancelphoto.setOnClickListener(this);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		Intent intent;
    		Bundle bundle;
    		switch (view.getId()) {
    		case R.id.btn_titlebar_back:
    			finish();
    			break;
    		case R.id.btn_titlebar_barcode:
    			Intent openCameraIntent = new Intent(PromotionSubmitActivity.this, com.zjrc.isale.client.zxing.activity.CaptureActivity.class);
    			startActivityForResult(openCameraIntent, Constant.BARCODE_SCAN);
    			break;
    		case R.id.tv_promotionterminal:
    			intent = new Intent();
    			bundle = new Bundle();
    			bundle.putString("terminalid", terminalid);
    			intent.putExtras(bundle);
    			intent.setClass(PromotionSubmitActivity.this, TerminalSelectActivity.class);
    			startActivityForResult(intent, Constant.RESULT_TEMINAL_SELECT);
    			break;
    		case R.id.tv_promotionproduct:
    			if (TextUtils.isEmpty(terminalid) || TextUtils.isEmpty(terminalname)) {
    				showAlertDialog("提示","请选择活动网点!", new View.OnClickListener() {
    					@Override
    					public void onClick(View arg0) {
    						alertDialog.cancel();
    					}
    				}, "确定", null, null);
    			} else {
    				intent = new Intent();
    				bundle = new Bundle();
    				bundle.putString("terminalid", terminalid);
    				bundle.putString("productid", productid);
    				intent.putExtras(bundle);
    				intent.setClass(PromotionSubmitActivity.this, TerminalProductSelectActivity.class);
    				startActivityForResult(intent, Constant.RESULT_PRODUCT_SELECT);
    			}
    			break;
    		case R.id.iv_takephoto:
                intent = new Intent();
                intent.setClass(PromotionSubmitActivity.this, CaptureActivity.class);
                startActivityForResult(intent, Constant.RESULT_PROMOTION_PHOTO);
    			break;
            case R.id.iv_photo:
                if (!TextUtils.isEmpty(photofilepath)) {
                    intent = new Intent();
                    bundle = new Bundle();
                    bundle.putString("imagefilename", photofilepath);
                    intent.putExtras(bundle);
                    intent.setClass(PromotionSubmitActivity.this, ImageViewActivity.class);
                    startActivity(intent);
                }
                break;
    		case R.id.iv_cancelphoto:
                rl_photo.setVisibility(View.GONE);
                iv_takephoto.setVisibility(View.VISIBLE);

//    			et_promotionphoto.setHint("现场照片");
//    			et_promotionphoto.setEnabled(false);

    			photofilepath = null;
    			break;
    		case R.id.btn_ok:
    			if (validityInput()) {
    				ISaleApplication application = (ISaleApplication) getApplication();
    				if (application != null) {
    					if (!TextUtils.isEmpty(photofilepath) && TextUtils.isEmpty(photofileid)) {
    						uploadtask = new FileUploadTask(application, PromotionSubmitActivity.this, "promotion", photofilepath, et_promotionphoto.getText().toString(), uploadeventlistener);
    						uploadtask.execute();
    					} else {
    						sendSubmit();
    					}
    				}
    			}
    			break;
    		}
    	}
    }

    CustomDatePicker datePicker;
    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
        		datePicker = new CustomDatePicker(PromotionSubmitActivity.this,
						"请选择时间", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
		                        ((TextView) v).setText(datePicker.getYear() + "-"
										+ datePicker.getMonth() + "-"
										+ datePicker.getDay());
								datePicker.dismiss();

							}
						}, new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								datePicker.dismiss();
							}
						},((TextView) v).getText().toString());
				datePicker.show();


                break;
            default:
                break;
        }
        return false;
    }

    private boolean validityInput() {
        if (TextUtils.isEmpty(terminalid)) {
            showAlertDialog("提示","请选择活动网点!", new View.OnClickListener() {
 				@Override
 				public void onClick(View arg0) {
 					alertDialog.cancel();
 				}
 			}, "确定", null, null);
            tv_promotionterminal.requestFocus();
            return false;
        }
        String sPromotionDesc = et_promotiondesc.getText().toString();
        if (TextUtils.isEmpty(sPromotionDesc)) {
            showAlertDialog("提示","请输入活动标题!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null, null);
            et_promotiondesc.requestFocus();
            return false;
        }
        String sPromotionBegindate = tv_promotionbegindate.getText().toString();
        if (TextUtils.isEmpty(sPromotionBegindate)) {
            showAlertDialog("提示","请选择开始日期!", new View.OnClickListener() {
       				@Override
       				public void onClick(View arg0) {
       					alertDialog.cancel();
       				}
       			}, "确定", null, null);
            tv_promotionbegindate.requestFocus();
            return false;
        }
        String sPromotionEnddate = tv_promotionenddate.getText().toString();
        if (TextUtils.isEmpty(sPromotionEnddate)) {
            showAlertDialog("提示","请选择结束日期!", new View.OnClickListener() {
   				@Override
   				public void onClick(View arg0) {
   					alertDialog.cancel();
   				}
   			}, "确定", null, null);
            tv_promotionenddate.requestFocus();
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date begindate;
        Date enddate;
        try {
            begindate = sdf.parse(sPromotionBegindate);
            enddate = sdf.parse(sPromotionEnddate);
        } catch (Exception e) {
            begindate = new Date();
            enddate = new Date();
        }
        if (enddate.getTime() < begindate.getTime()) {
            showAlertDialog("提示","结束日期不能小于开始日期!", new View.OnClickListener() {
     				@Override
     				public void onClick(View arg0) {
     					alertDialog.cancel();
     				}
     			}, "确定", null, null);
            tv_promotionenddate.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(productid)) {
            showAlertDialog("提示","请选择活动产品!", new View.OnClickListener() {
 				@Override
 				public void onClick(View arg0) {
 					alertDialog.cancel();
 				}
 			}, "确定", null, null);
            tv_promotionproduct.requestFocus();
            return false;
        }
        String sPromotionPrice = et_promotionprice.getText().toString();
        if (TextUtils.isEmpty(sPromotionPrice)) {
            showAlertDialog("提示","请输入产品价格!", new View.OnClickListener() {
 				@Override
 				public void onClick(View arg0) {
 					alertDialog.cancel();
 				}
 			}, "确定", null, null);
            et_promotionprice.requestFocus();
            return false;
        }
        String sPromotionNum = et_promotionnum.getText().toString();
        if (TextUtils.isEmpty(sPromotionNum)) {
            showAlertDialog("提示","请输入产品数量!", new View.OnClickListener() {
 				@Override
 				public void onClick(View arg0) {
 					alertDialog.cancel();
 				}
 			}, "确定", null, null);
            et_promotionnum.requestFocus();
            return false;
        } else if (Integer.parseInt(sPromotionNum) <= 0) {
            showAlertDialog("提示", "产品数量必须大于0，请重新输入!", new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    alertDialog.cancel();
                }
            }, "确定", null, null);
            et_promotionnum.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.BARCODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                if (scanResult != null) {
                    terminalcode = scanResult;
                    sendQueryTerminalDetail(terminalcode);
                }
            }
        } else if (requestCode == Constant.RESULT_TEMINAL_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                terminalid = bundle.getString("terminalid");
                terminalname = bundle.getString("terminalname");
                tv_promotionterminal.setText(terminalname);
            }
        } else if (requestCode == Constant.RESULT_PRODUCT_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                productid = bundle.getString("productid");
                productname = bundle.getString("productname");
                tv_promotionproduct.setText(productname);
                et_promotionbrand.setText(bundle.getString("brandname"));
                et_promotioncategory.setText(bundle.getString("categoryname"));
                et_promotionnorm.setText(bundle.getString("norm"));
                et_promotionprice.setText(bundle.getString("price"));
            }
        } else if (requestCode == Constant.RESULT_PROMOTION_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                photofilepath = bundle.getString("filepath");

                Drawable drawable = Drawable.createFromPath(photofilepath);
                iv_photo.setImageDrawable(drawable);

                iv_takephoto.setVisibility(View.GONE);

                rl_photo.setVisibility(View.VISIBLE);

//                et_promotionphoto.setHint(R.string.promotion_photohint);
//                et_promotionphoto.setEnabled(true);
            }
        }
    }

    private void sendQueryTerminalDetail(String code) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null && application.getConfig() != null && application.getConfig().getUserid() != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("terminalid", "");
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("terminalcode", code);
            request("terminal!detail?code=" + Constant.TERMINAL_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    private void sendSubmit() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("operate","insert");
            params.put("terminalid",XmlValueUtil.encodeString(terminalid));
            params.put("productid",XmlValueUtil.encodeString(productid));
            params.put("promotionprice",XmlValueUtil.encodeString(et_promotionprice.getText().toString()));
            params.put("promotionnum", XmlValueUtil.encodeString(et_promotionnum.getText().toString()));
            params.put("begindate", XmlValueUtil.encodeString(tv_promotionbegindate.getText().toString()));
            params.put("enddate", XmlValueUtil.encodeString(tv_promotionenddate.getText().toString()));
            params.put("promotiondesc", XmlValueUtil.encodeString(et_promotiondesc.getText().toString()));
            params.put("fileid", XmlValueUtil.encodeString(photofileid));
            params.put("promotionid", "");
            request("corppromotion!submit?code=" + Constant.PROMOTION_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {
   
    }
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.PROMOTION_SUBMIT.equals(code)) {
                Toast.makeText(this,"活动上报成功!", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }else if (Constant.TERMINAL_DETAIL.equals(code)) {//网点详情
                JsonObject backorder = response.getAsJsonObject("body");
                if(backorder != null) {
                    terminalid = backorder.get("id").getAsString();
                    terminalname = backorder.get("name").getAsString();
                    tv_promotionterminal.setText(terminalname);
                }
            } 
        }
    }
}
