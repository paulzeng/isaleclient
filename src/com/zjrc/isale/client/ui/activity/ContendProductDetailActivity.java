package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.task.FileDownloadTask;
import com.zjrc.isale.client.task.IDownloadEventListener;
import com.zjrc.isale.client.util.FileUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.io.File;
import java.util.HashMap;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：竞品信息详情界面
 */
public class ContendProductDetailActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private TextView tv_contendproductname;
    private TextView tv_date;
    private TextView tv_terminal;
    private TextView tv_contendproductbrand;
    private TextView tv_contendproductnorm;
    private TextView tv_contendproductprice;
    private TextView tv_contendproductdesc;
    private ImageView iv_contendproductphoto;

    private Button btn_modify;
    private Button btn_delete;

    private String contendproductid;

    private String photofilepath;

    private FileDownloadTask downloadtask;

    private IDownloadEventListener downloadeventlistener = new IDownloadEventListener() {
        @Override
        public void onFinish(String filetype, String filename) {
            if (filetype.equalsIgnoreCase("promotion")) {
                photofilepath = filename;
                if (!TextUtils.isEmpty(photofilepath)) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(photofilepath, options);
                    iv_contendproductphoto.setImageBitmap(bitmap);
                }
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
        setContentView(R.layout.contendproduct_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.contendproduct_detail);

        tv_contendproductname = (TextView) findViewById(R.id.tv_contendproductname);

        tv_date = (TextView) findViewById(R.id.tv_date);

        tv_terminal = (TextView) findViewById(R.id.tv_terminal);

        tv_contendproductbrand = (TextView) findViewById(R.id.tv_contendproductbrand);

        tv_contendproductnorm = (TextView) findViewById(R.id.tv_contendproductnorm);

        tv_contendproductprice = (TextView) findViewById(R.id.tv_contendproductprice);

        tv_contendproductdesc = (TextView) findViewById(R.id.tv_contendproductdesc);

        iv_contendproductphoto = (ImageView) findViewById(R.id.iv_contendproductphoto);
        iv_contendproductphoto.setOnClickListener(this);

        btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(this);

        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        contendproductid = bundle.getString("contendproductid");
        sendQueryDetail(contendproductid);
    }

    @Override
    public void onClick(View view) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		switch (view.getId()) {
    		case R.id.btn_titlebar_back:
    			finish();
    			break;
    		case R.id.iv_contendproductphoto:
    			if (!TextUtils.isEmpty(photofilepath)) {
    				Intent intent = new Intent();
    				Bundle bundle = new Bundle();
    				bundle.putString("imagefilename", photofilepath);
    				intent.putExtras(bundle);
    				intent.setClass(ContendProductDetailActivity.this, ImageViewActivity.class);
    				startActivity(intent);
    			}
    			break;
    		case R.id.btn_modify:
    			Intent intent = new Intent();
    			Bundle bundle = new Bundle();
    			bundle.putString("operate", "modify");
    			bundle.putString("contendproductid", contendproductid);
    			intent.putExtras(bundle);
    			intent.setClass(ContendProductDetailActivity.this, ContendProductSubmitActivity.class);
    			startActivityForResult(intent, Constant.CONTENDPRODUCTLIST_ADD);
    			break;
    		case R.id.btn_delete:
    			showAlertDialog("确认", "确定删除？", new View.OnClickListener() {
    				@Override
    				public void onClick(View view) {
						requestDelete(contendproductid);
    				}
    			}, "确定", "取消", new View.OnClickListener() {
    				@Override
    				public void onClick(View view) {
    					alertDialog.cancel();
    				}
    			});
    			break;
    		}
    	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.CONTENDPRODUCTLIST_ADD) {
            if (resultCode == RESULT_OK) {
                sendQueryDetail(contendproductid);
                // 修改成功，通知列表刷新
                setResult(RESULT_OK);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (downloadtask != null) {
            downloadtask.cancelTask();
            downloadtask = null;
        }

        super.onDestroy();
    }

    private void sendQueryDetail(String id) {
        ISaleApplication application = (ISaleApplication)getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("contendproductid", id);
            request("conproduct!detail?code=" + Constant.CONTENDPRODUCT_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    public void requestDelete(String contendproductid) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("operate","delete");
            params.put("contendproductid",(contendproductid == null ? "" : XmlValueUtil.encodeString(contendproductid)));
            params.put("companyid",XmlValueUtil.encodeString(application.getConfig().getCompanyid()));
            params.put("terminalid", "");
            params.put("name", "");
            params.put("brand", "");
            params.put("norm", "");
            params.put("price", "0");
            params.put("desc", "");
            params.put("fileid", "");
            request("conproduct!submit?code=" + Constant.CONTENDPRODUCT_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {

    }
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.CONTENDPRODUCT_DETAIL.equals(code)) {
                JsonObject contentProduct_detail = response.getAsJsonObject("body");
                tv_contendproductname.setText(contentProduct_detail.get("name").getAsString());
                tv_date.setText(contentProduct_detail.get("submitdate").getAsString());
                tv_terminal.setText(contentProduct_detail.get("terminalname").getAsString());
                tv_contendproductbrand.setText(contentProduct_detail.get("brand").getAsString());
                tv_contendproductnorm.setText(contentProduct_detail.get("norm").getAsString());
                tv_contendproductprice.setText(contentProduct_detail.get("price").getAsString());
                tv_contendproductdesc.setText(contentProduct_detail.get("desc").getAsString());
                boolean isPhotoExists = false;
                String photofilename = contentProduct_detail.get("filename").getAsString();
                if (!TextUtils.isEmpty(photofilename)) {
                    photofilepath = FileUtil.hasPicCached(photofilename);
                    if (new File(photofilepath).exists()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(photofilepath, options);
                        iv_contendproductphoto.setVisibility(View.VISIBLE);
                        iv_contendproductphoto.setImageBitmap(bitmap);
                        isPhotoExists = true;
                    }
                }
                if (!isPhotoExists) {
                    String photofileid =contentProduct_detail.get("fileid").getAsString();
                    if (!TextUtils.isEmpty(photofileid)) {
                        ISaleApplication application = (ISaleApplication) getApplication();
                        if (application != null) {
                            downloadtask = new FileDownloadTask(application, ContendProductDetailActivity.this, "promotion", photofileid, downloadeventlistener, false);
                            downloadtask.execute();
                        }
                    } else {
                        iv_contendproductphoto.setVisibility(View.GONE);
                    }
                }
            }else if(Constant.CONTENDPRODUCT_SUBMIT.equals(code)){
                        Toast.makeText(this, "竞品删除成功!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
            }
        }
    }
}
