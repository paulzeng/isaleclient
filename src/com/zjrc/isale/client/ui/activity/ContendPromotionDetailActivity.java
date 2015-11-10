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
 * 功能描述：竞品活动详情界面
 */
public class ContendPromotionDetailActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private TextView tv_promotiondesc;
    private TextView tv_createdate;
    private TextView tv_promotionterminal;
    private TextView tv_promotionbegindate;
    private TextView tv_promotionenddate;
    private TextView tv_promotionproduct;
    private TextView tv_promotionprice;
    private TextView tv_promotionnum;
    private ImageView iv_promotionphoto;

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
                    iv_promotionphoto.setImageBitmap(bitmap);
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
        setContentView(R.layout.contendpromotion_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.promotion_detail);

        tv_promotiondesc = (TextView) findViewById(R.id.tv_promotiondesc);
        tv_createdate = (TextView) findViewById(R.id.tv_createdate);
        tv_promotionterminal = (TextView) findViewById(R.id.tv_promotionterminal);
        tv_promotionbegindate = (TextView) findViewById(R.id.tv_promotionbegindate);
        tv_promotionenddate = (TextView) findViewById(R.id.tv_promotionenddate);
        tv_promotionproduct = (TextView) findViewById(R.id.tv_promotionproduct);
        tv_promotionprice = (TextView) findViewById(R.id.tv_promotionprice);
        tv_promotionnum = (TextView) findViewById(R.id.tv_promotionnum);
        iv_promotionphoto = (ImageView) findViewById(R.id.iv_promotionphoto);
        iv_promotionphoto.setOnClickListener(this);

        String promotionid = getIntent().getExtras().getString("promotionid");
        sendQueryPromotionDetail(promotionid);
    }

    @Override
    public void onClick(View view) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		switch (view.getId()) {
    		case R.id.btn_titlebar_back:
    			finish();
    			break;
    		case R.id.iv_promotionphoto:
    			if (!TextUtils.isEmpty(photofilepath)) {
    				Intent intent = new Intent();
    				Bundle bundle = new Bundle();
    				bundle.putString("imagefilename", photofilepath);
    				intent.putExtras(bundle);
    				intent.setClass(ContendPromotionDetailActivity.this, ImageViewActivity.class);
    				startActivity(intent);
    			}
    			break;
    		case R.id.btn_modify:
    			break;
    		case R.id.btn_delete:
    			break;
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

    private void sendQueryPromotionDetail(String id) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("contendpromotionid", XmlValueUtil.encodeString(id));
            request("corpcontendpromotion!detail?code=" + Constant.CONTENDPROMOTION_ITEM, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {

    }
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.CONTENDPROMOTION_ITEM.equals(code)) {
                JsonObject promotion = response.getAsJsonObject("body");
                if (promotion != null) {
                    tv_promotiondesc.setText(promotion.get("promotiondesc").getAsString());
                    tv_promotionterminal.setText(promotion.get("terminalname").getAsString());
                    tv_createdate.setText(promotion.get("submitdate").getAsString());
                    tv_promotionbegindate.setText(promotion.get("promotionbegindate").getAsString());
                    tv_promotionenddate.setText(promotion.get("promotionenddate").getAsString());
                    tv_promotionproduct.setText(promotion.get("productname").getAsString() );
                    tv_promotionprice.setText(promotion.get("promotionprice").getAsString());
                    tv_promotionnum.setText(promotion.get("promotionnum").getAsString() );
                    boolean isPhotoExists = false;
                    String photofilename =promotion.get("filename").getAsString();
                    if (!TextUtils.isEmpty(photofilename)) {
                        photofilepath = FileUtil.hasPicCached(photofilename);
                        if (new File(photofilepath).exists()) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(photofilepath, options);
                            iv_promotionphoto.setImageBitmap(bitmap);
                            isPhotoExists = true;
                        }
                    }
                    if (!isPhotoExists) {
                        String photofileid =promotion.get("fileid").getAsString() ;
                        if (!TextUtils.isEmpty(photofileid)) {
                            ISaleApplication application = (ISaleApplication) getApplication();
                            if (application != null) {
                                downloadtask = new FileDownloadTask(application, ContendPromotionDetailActivity.this, "promotion", photofileid, downloadeventlistener, false);
                                downloadtask.execute();
                            }
                        } else {
                            iv_promotionphoto.setImageDrawable(null);
                        }
                    }
                }
            }else {
                Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
