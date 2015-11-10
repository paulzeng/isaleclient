package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
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
import com.zjrc.isale.client.task.FileDownloadTask;
import com.zjrc.isale.client.task.FileUploadTask;
import com.zjrc.isale.client.task.IDownloadEventListener;
import com.zjrc.isale.client.task.IUploadEventListener;
import com.zjrc.isale.client.util.DecimalDigitLimitFilter;
import com.zjrc.isale.client.util.FileUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：竞品信息上报界面
 */
public class ContendProductSubmitActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_barcode;

    private TextView tv_terminal;
    private EditText et_contendproductname;
    private EditText et_contendproductbrand;
    private EditText et_contendproductnorm;
    private EditText et_contendproductprice;
    private EditText et_contendproductdesc;
    private EditText et_contendproductphoto;
    private ImageView iv_takephoto;
    private RelativeLayout rl_photo;
    private ImageView iv_photo;
    private ImageView iv_cancelphoto;
    private Button btn_ok;

    private String operate;

    private String terminalid;
    private String terminalcode;
    private String terminalname;

    private String contendproductid;

    private String photofileid;
    private String photofilepath;

    private FileUploadTask uploadtask;

    private IUploadEventListener uploadeventlistener = new IUploadEventListener() {
        @Override
        public void onFinish(String filetype, String fileid) {
            if (filetype.equalsIgnoreCase("contendproduct")) {
                photofileid = fileid;
            }

            if (!TextUtils.isEmpty(photofileid)) {
                sendSubmit(contendproductid);
            }
        }

		@Override
		public void onFailed(String filetype, String message) {
			// TODO Auto-generated method stub
			
		}
    };

    private FileDownloadTask downloadtask;

    private IDownloadEventListener downloadeventlistener = new IDownloadEventListener() {
        @Override
        public void onFinish(String filetype, String filename) {
            if (filetype.equalsIgnoreCase("contendproduct")) {
                photofilepath = filename;
            }

            if (!TextUtils.isEmpty(photofilepath)) {
                Drawable drawable = Drawable.createFromPath(photofilepath);
                iv_photo.setImageDrawable(drawable);

                iv_takephoto.setVisibility(View.GONE);

                rl_photo.setVisibility(View.VISIBLE);

//                et_contendproductphoto.setHint(R.string.contendproduct_photohint);
//                et_contendproductphoto.setEnabled(true);
            }
        }

		@Override
		public void onFail(String filetype, String message) {
			// TODO Auto-generated method stub
			
		}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.contendproduct_submit);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.contendproduct_submit);

        btn_titlebar_barcode = (Button) findViewById(R.id.btn_titlebar_barcode);
        btn_titlebar_barcode.setVisibility(View.VISIBLE);
        btn_titlebar_barcode.setOnClickListener(this);

        tv_terminal = (TextView) findViewById(R.id.tv_terminal);
        tv_terminal.setOnClickListener(this);

        et_contendproductname = (EditText) findViewById(R.id.et_contendproductname);

        et_contendproductbrand = (EditText) findViewById(R.id.et_contendproductbrand);

        et_contendproductnorm = (EditText) findViewById(R.id.et_contendproductnorm);

        et_contendproductprice = (EditText) findViewById(R.id.et_contendproductprice);
        et_contendproductprice.setFilters(new InputFilter[] {
                new DecimalDigitLimitFilter(10, 2)
        });

        et_contendproductdesc = (EditText) findViewById(R.id.et_contendproductdesc);

        et_contendproductphoto = (EditText) findViewById(R.id.et_contendproductphoto);

        iv_takephoto = (ImageView) findViewById(R.id.iv_takephoto);
        iv_takephoto.setOnClickListener(this);

        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);

        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        iv_photo.setOnClickListener(this);

        iv_cancelphoto = (ImageView) findViewById(R.id.iv_cancelphoto);
        iv_cancelphoto.setOnClickListener(this);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        operate = bundle != null ? bundle.getString("operate") : "insert";
        if (operate.equalsIgnoreCase("insert")) {//新增
        } else if (operate.equalsIgnoreCase("modify")) {
            tv_titlebar_title.setText(R.string.contendproduct_modify);

            contendproductid = bundle.getString("contendproductid");
            sendQueryDetail(contendproductid);
        }
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
    			Intent openCameraIntent = new Intent(ContendProductSubmitActivity.this, com.zjrc.isale.client.zxing.activity.CaptureActivity.class);
    			startActivityForResult(openCameraIntent, Constant.BARCODE_SCAN);
    			break;
    		case R.id.tv_terminal:
    			intent = new Intent();
    			bundle = new Bundle();
    			bundle.putString("terminalid", terminalid);
    			intent.putExtras(bundle);
    			intent.setClass(ContendProductSubmitActivity.this, TerminalSelectActivity.class);
    			startActivityForResult(intent, Constant.RESULT_TEMINAL_SELECT);
    			break;
    		case R.id.iv_takephoto:
                try {
                    if (downloadtask != null && !downloadtask.isCancelled()) {
                        downloadtask.cancelTask();
                    }
//                    et_contendproductphoto.setHint(getResources().getString(R.string.contendproduct_photo));
//                    et_contendproductphoto.setEnabled(false);
                } catch (Exception e) {
                }
                intent = new Intent();
                intent.setClass(ContendProductSubmitActivity.this, CaptureActivity.class);
                startActivityForResult(intent, Constant.RESULT_PROMOTION_PHOTO);
    			break;
            case R.id.iv_photo:
                if (!TextUtils.isEmpty(photofilepath)) {
                    intent = new Intent();
                    bundle = new Bundle();
                    bundle.putString("imagefilename", photofilepath);
                    intent.putExtras(bundle);
                    intent.setClass(ContendProductSubmitActivity.this, ImageViewActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.iv_cancelphoto:
                rl_photo.setVisibility(View.GONE);
                iv_takephoto.setVisibility(View.VISIBLE);
                photofileid="";
//                et_contendproductphoto.setHint(getResources().getString(R.string.contendproduct_photo));
//                et_contendproductphoto.setEnabled(false);

                photofilepath = null;
                break;
    		case R.id.btn_ok:
    			if (validityInput()) {
    				ISaleApplication application = (ISaleApplication) getApplication();
    				if (application != null) {
    					if (!TextUtils.isEmpty(photofilepath) && TextUtils.isEmpty(photofileid)) {
    						uploadtask = new FileUploadTask(application, ContendProductSubmitActivity.this, "contendproduct", photofilepath, et_contendproductphoto.getText().toString(), uploadeventlistener);
    						uploadtask.execute();
    					} else {
    						sendSubmit(contendproductid);
    					}
    				}
    			}
    			break;
    		}
    	}
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
                tv_terminal.setText(terminalname);
            }
        } else if (requestCode == Constant.RESULT_PROMOTION_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                photofileid = "";
                photofilepath = bundle.getString("filepath");

                Drawable drawable = Drawable.createFromPath(photofilepath);
                iv_photo.setImageDrawable(drawable);

                iv_takephoto.setVisibility(View.GONE);

                rl_photo.setVisibility(View.VISIBLE);

//                et_contendproductphoto.setHint(R.string.contendproduct_photohint);
//                et_contendproductphoto.setEnabled(true);
            }
        }
    }

    private boolean validityInput() {
        if (TextUtils.isEmpty(terminalid)) {
			showAlertDialog("确认", "销售网点不能为空，请选择销售网点!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null,null);
            tv_terminal.requestFocus();
            return false;
        }
        String sContendProductName = et_contendproductname.getText().toString();
        if (TextUtils.isEmpty(sContendProductName)) {
			showAlertDialog("确认", "竞品名称不能为空，请输入竞品名称!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null,null);
            et_contendproductname.requestFocus();
            return false;
        }
        String sContendProductBrand = et_contendproductbrand.getText().toString();
        if (TextUtils.isEmpty(sContendProductBrand)) {
			showAlertDialog("确认", "竞品品牌不能为空，请输入竞品品牌!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null,null);
            et_contendproductbrand.requestFocus();
            return false;
        }
        String sContendProductNorm = et_contendproductnorm.getText().toString();
        if (TextUtils.isEmpty(sContendProductNorm)) {
			showAlertDialog("确认", "竞品规格不能为空，请输入竞品规格!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null,null);
            et_contendproductnorm.requestFocus();
            return false;
        }
        String sContendProductPrice = et_contendproductprice.getText().toString();
        if (TextUtils.isEmpty(sContendProductPrice)) {
			showAlertDialog("确认", "竞品单价不能为空，请输入竞品单价!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null,null);
            et_contendproductprice.requestFocus();
            return false;
        }
        return true;
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

    private void sendQueryDetail(String id) {
        ISaleApplication application = (ISaleApplication)getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("contendproductid", id);
            request("conproduct!detail?code=" + Constant.CONTENDPRODUCT_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    private void sendSubmit(String contendproductid) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("operate", XmlValueUtil.encodeString(operate));
            params.put("contendproductid",(contendproductid == null ? "" : XmlValueUtil.encodeString(contendproductid)));
            params.put("companyid",XmlValueUtil.encodeString(application.getConfig().getCompanyid()));
            params.put("terminalid", XmlValueUtil.encodeString(terminalid));
            params.put("name", XmlValueUtil.encodeString(et_contendproductname.getText().toString()));
            params.put("brand", XmlValueUtil.encodeString(et_contendproductbrand.getText().toString()));
            params.put("norm", XmlValueUtil.encodeString(et_contendproductnorm.getText().toString()));
            params.put("price", XmlValueUtil.encodeString(et_contendproductprice.getText().toString()));
            params.put("desc", XmlValueUtil.encodeString(et_contendproductdesc.getText().toString()));
            params.put("fileid", XmlValueUtil.encodeString(photofileid));
            request("conproduct!submit?code=" + Constant.CONTENDPRODUCT_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {

    }

    public int dpToPx(int dp) {
        float densityDpi = getResources().getDisplayMetrics().densityDpi;
        return Math.round(dp * (densityDpi / 160f));
    }
    @Override
    public void onRecvData(JsonObject response) {

        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.CONTENDPRODUCT_DETAIL.equals(code)) {
                JsonObject contentProduct_detail = response.getAsJsonObject("body");

                terminalid = contentProduct_detail.get("terminalid").getAsString();
                et_contendproductname.setText(contentProduct_detail.get("name").getAsString());
                tv_terminal.setText(contentProduct_detail.get("terminalname").getAsString());
                et_contendproductbrand.setText(contentProduct_detail.get("brand").getAsString());
                et_contendproductnorm.setText(contentProduct_detail.get("norm").getAsString());
                et_contendproductprice.setText(contentProduct_detail.get("price").getAsString());
                et_contendproductdesc.setText(contentProduct_detail.get("desc").getAsString());
                boolean isPhotoExists = false;
                String photofilename =contentProduct_detail.get("filename").getAsString();
                if (!TextUtils.isEmpty(photofilename)) {
                    photofilepath = FileUtil.hasPicCached(photofilename);
                    if (new File(photofilepath).exists()) {

                        Drawable drawable = Drawable.createFromPath(photofilepath);
                        iv_takephoto.setVisibility(View.GONE);

                        iv_photo.setImageDrawable(drawable);

                        iv_takephoto.setVisibility(View.GONE);

                        rl_photo.setVisibility(View.VISIBLE);

                        isPhotoExists = true;
                    }
                }
                photofileid = contentProduct_detail.get("fileid").getAsString();
                if (!isPhotoExists) {
                    if (!TextUtils.isEmpty(photofileid)) {
                        ISaleApplication application = (ISaleApplication) getApplication();
                        if (application != null) {
                            et_contendproductphoto.setHint("正在下载照片，请稍后～");
                            downloadtask = new FileDownloadTask(application, ContendProductSubmitActivity.this, "contendproduct", photofileid, downloadeventlistener, false);
                            downloadtask.execute();
                        }
                    }
                }

            }else if(Constant.CONTENDPRODUCT_SUBMIT.equals(code)){
                    Toast.makeText(this, "modify".equals(operate) ? "竞品修改成功!" : "竞品上报成功!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
            } else if (Constant.TERMINAL_DETAIL.equals(code)) {//网点详情
            	JsonObject backorder = response.getAsJsonObject("body");
                if(backorder != null) {
                	terminalid = backorder.get("id").getAsString();
    				terminalname = backorder.get("name").getAsString();
    				tv_terminal.setText(terminalname);
                }
            }
        }
    }
}
