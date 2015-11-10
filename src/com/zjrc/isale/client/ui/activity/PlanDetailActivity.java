package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：拜访详情
 */
public class PlanDetailActivity extends BaseActivity {

    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private TextView tv_title;
    private TextView tv_add_time;
    private TextView tv_state;
    private TextView tv_terminal;
    private TextView tv_date;
    private TextView tv_place;
    private TextView tv_content;
    private TextView tv_to_submit_detail;
    private ImageView iv_plan_pics_1;
    private ImageView iv_plan_pics_2;
    private ImageView iv_plan_pics_3;
    private Button btn_map;

    private String doorphotofilepath = "";
    private String doorphotoid = "";
    private String productphotofilepath = "";
    private String productphotoid = "";
    private String contendproductphotofilepath = "";
    private String contendproductphotoid = "";
    private String planid;
    private String patrolid;

    private String longitude;
    private String latitude;
    private FileDownloadTask downloadtask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.plan_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.titlebar_small);
        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (CommonUtils.isNotFastDoubleClick()) {
                    finish();
                }
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_add_time = (TextView) findViewById(R.id.tv_add_time);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_terminal = (TextView) findViewById(R.id.tv_terminal);
        tv_date = (TextView) findViewById(R.id.tv_time);
        tv_place = (TextView) findViewById(R.id.tv_place);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_to_submit_detail = (TextView) findViewById(R.id.tv_to_submit_detail);

        btn_map = (Button) findViewById(R.id.btn_map);
        iv_plan_pics_1 = (ImageView) findViewById(R.id.iv_plan_pics_1);
        iv_plan_pics_2 = (ImageView) findViewById(R.id.iv_plan_pics_2);
        iv_plan_pics_3 = (ImageView) findViewById(R.id.iv_plan_pics_3);
        tv_titlebar_title.setText(R.string.plan_detail);

        if (!needAudit) {
            findViewById(R.id.tv_to_submit_detail).setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        planid = bundle.getString("planid");
        patrolid = bundle.getString("patrolid");
        tv_to_submit_detail.setText(Html
                .fromHtml("<u><font color=\"#5E76FF\">查看审批详情</font></u>"));
        tv_to_submit_detail.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (CommonUtils.isNotFastDoubleClick()) {
                    Intent intent = new Intent();
                    intent.setClass(PlanDetailActivity.this,
                            PlanSubmitDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("planid", planid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        sendQueryPatrol();

    }

    private void sendQueryPatrol() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("patrolid", XmlValueUtil.encodeString(patrolid));
        request("patrol!detail?code=" + Constant.PATROL_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
    }

    @Override
    public void onRecvData(XmlNode response) {

    }

    private IDownloadEventListener downloadeventlistener = new IDownloadEventListener() {// 顺序下载

        @Override
        public void onFinish(String filetype, String filename) {
            if (filetype.equalsIgnoreCase("door")) {
                doorphotofilepath = filename;
                iv_plan_pics_1.setImageBitmap(BitmapFactory
                        .decodeFile(doorphotofilepath));
                iv_plan_pics_1
                        .setOnClickListener(new ImageView.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                Intent intent = new Intent();
                                intent.setClass(PlanDetailActivity.this,
                                        ImageViewActivity.class);
                                intent.putExtra("imagefilename",
                                        doorphotofilepath);
                                startActivity(intent);
                            }
                        });
                if (productphotofilepath != null
                        && !productphotofilepath.equalsIgnoreCase("")) {
                    final String path = FileUtil
                            .hasPicCached(productphotofilepath);
                    if (path != null && !path.equalsIgnoreCase("")) {// 下载地址存在

                        iv_plan_pics_2.setImageBitmap(BitmapFactory
                                .decodeFile(path));
                        iv_plan_pics_2
                                .setOnClickListener(new ImageView.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        Intent intent = new Intent();
                                        intent.setClass(
                                                PlanDetailActivity.this,
                                                ImageViewActivity.class);
                                        intent.putExtra("imagefilename", path);
                                        startActivity(intent);
                                    }
                                });
                    } else {
                        ISaleApplication application = (ISaleApplication) getApplication();
                        if (application != null) {
                            downloadtask = new FileDownloadTask(application,
                                    PlanDetailActivity.this, "product",
                                    productphotoid, downloadeventlistener,
                                    false);
                            downloadtask.execute();
                        }
                    }

                }

            } else if (filetype.equalsIgnoreCase("product")) {
                productphotofilepath = filename;
                iv_plan_pics_2.setImageBitmap(BitmapFactory
                        .decodeFile(productphotofilepath));
                iv_plan_pics_2
                        .setOnClickListener(new ImageView.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                Intent intent = new Intent();
                                intent.setClass(PlanDetailActivity.this,
                                        ImageViewActivity.class);
                                intent.putExtra("imagefilename",
                                        productphotofilepath);
                                startActivity(intent);
                            }
                        });
                if (contendproductphotofilepath != null
                        && !contendproductphotofilepath.equalsIgnoreCase("")) {
                    final String path = FileUtil
                            .hasPicCached(contendproductphotofilepath);
                    if (path != null && !path.equalsIgnoreCase("")) {// 下载地址存在
                        iv_plan_pics_3.setImageBitmap(BitmapFactory
                                .decodeFile(path));

                        iv_plan_pics_3
                                .setOnClickListener(new ImageView.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        Intent intent = new Intent();
                                        intent.setClass(
                                                PlanDetailActivity.this,
                                                ImageViewActivity.class);
                                        intent.putExtra("imagefilename", path);
                                        startActivity(intent);
                                    }
                                });

                    } else {
                        ISaleApplication application = (ISaleApplication) getApplication();
                        if (application != null) {
                            downloadtask = new FileDownloadTask(application,
                                    PlanDetailActivity.this, "contendproduct",
                                    contendproductphotoid,
                                    downloadeventlistener, false);
                            downloadtask.execute();
                        }
                    }

                }

            } else if (filetype.equalsIgnoreCase("contendproduct")) {
                contendproductphotofilepath = filename;
                iv_plan_pics_3.setImageBitmap(BitmapFactory
                        .decodeFile(contendproductphotofilepath));
                iv_plan_pics_3
                        .setOnClickListener(new ImageView.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                Intent intent = new Intent();
                                intent.setClass(PlanDetailActivity.this,
                                        ImageViewActivity.class);
                                intent.putExtra("imagefilename",
                                        contendproductphotofilepath);
                                startActivity(intent);
                            }
                        });

            }
        }

        @Override
        public void onFail(String filetype, String message) {
            // TODO Auto-generated method stub
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
            if (Constant.PATROL_QUERY.equals(code)) {
                JsonObject body = response.getAsJsonObject("body");

                tv_title.setText(body.get("terminalname").getAsString());
                tv_terminal.setText(body.get("terminalname").getAsString());
                tv_add_time.setText(body.get("plandate").getAsString()
                        + " 申请");
                tv_state.setText("已执行");
                tv_date.setText(body.get("patroldate").getAsString());
                tv_place.setText(body.get("address").getAsString());
                btn_map.setVisibility(View.VISIBLE);
                longitude =body.get("longitude").getAsString();
                latitude =body.get("latitude").getAsString() ;
                tv_content.setText(body.get("patrolcontent").getAsString());
                btn_map.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (CommonUtils.isNotFastDoubleClick()) {
                            Intent intent = new Intent();
                            intent.setClass(PlanDetailActivity.this,
                                    BaiduMapActivity.class);
                            intent.putExtra("longitude", longitude);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("terminalname",
                                    tv_terminal.getText());
                            startActivity(intent);
                        }
                    }
                });

                doorphotoid =body.get("doorfileid").getAsString();
                doorphotofilepath =body.get("doorfilename").getAsString();
                productphotoid =body.get("productfileid").getAsString() ;
                productphotofilepath =body.get("productfilename").getAsString();
                contendproductphotoid =body.get("contendproductfileid").getAsString();
                contendproductphotofilepath = body.get("contendproductfilename").getAsString();

                if (doorphotoid != null && !doorphotoid.equalsIgnoreCase("")) {
                    iv_plan_pics_1.setVisibility(View.VISIBLE);
                    if (doorphotofilepath != null
                            && !doorphotofilepath.equalsIgnoreCase("")) {// 下载地址不为空，尝试本地缓存取图
                        final String path = FileUtil
                                .hasPicCached(doorphotofilepath);
                        if (path != null && !path.equalsIgnoreCase("")) {// 下载地址存在
                            iv_plan_pics_1.setImageBitmap(BitmapFactory
                                    .decodeFile(path));
                            iv_plan_pics_1
                                    .setOnClickListener(new ImageView.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent();
                                            intent.setClass(
                                                    PlanDetailActivity.this,
                                                    ImageViewActivity.class);
                                            intent.putExtra("imagefilename",
                                                    path);
                                            startActivity(intent);
                                        }
                                    });
                            if (productphotofilepath != null
                                    && !productphotofilepath
                                    .equalsIgnoreCase("")) {

                                final String path2 = FileUtil
                                        .hasPicCached(productphotofilepath);
                                if (path2 != null
                                        && !path2.equalsIgnoreCase("")) {// 下载地址存在
                                    iv_plan_pics_2.setImageBitmap(BitmapFactory
                                            .decodeFile(path2));
                                    iv_plan_pics_2
                                            .setOnClickListener(new ImageView.OnClickListener() {

                                                @Override
                                                public void onClick(View arg0) {
                                                    Intent intent = new Intent();
                                                    intent.setClass(
                                                            PlanDetailActivity.this,
                                                            ImageViewActivity.class);
                                                    intent.putExtra(
                                                            "imagefilename",
                                                            path2);
                                                    startActivity(intent);
                                                }
                                            });
                                } else {
                                    ISaleApplication application = (ISaleApplication) getApplication();
                                    if (application != null) {
                                        downloadtask = new FileDownloadTask(
                                                application,
                                                PlanDetailActivity.this,
                                                "product", productphotoid,
                                                downloadeventlistener, false);
                                        downloadtask.execute();
                                    }
                                }

                            }

                        } else {// 下载地址不存在，或本地缓存文件被删除，重新下载
                            ISaleApplication application = (ISaleApplication) getApplication();
                            if (application != null) {
                                downloadtask = new FileDownloadTask(
                                        application, PlanDetailActivity.this,
                                        "door", doorphotoid,
                                        downloadeventlistener, false);
                                downloadtask.execute();
                            }
                        }
                    }
                } else {
                    iv_plan_pics_1.setVisibility(View.GONE);
                }

                if (productphotoid != null
                        && !productphotoid.equalsIgnoreCase("")) {
                    iv_plan_pics_2.setVisibility(View.VISIBLE);
                    if (!productphotofilepath.equalsIgnoreCase("")) {
                        final String path = FileUtil
                                .hasPicCached(productphotofilepath);
                        if (path != null && !path.equalsIgnoreCase("")) {// 下载地址存在
                            iv_plan_pics_2.setImageBitmap(BitmapFactory
                                    .decodeFile(path));
                            iv_plan_pics_2
                                    .setOnClickListener(new ImageView.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent();
                                            intent.setClass(
                                                    PlanDetailActivity.this,
                                                    ImageViewActivity.class);
                                            intent.putExtra("imagefilename",
                                                    path);
                                            startActivity(intent);
                                        }
                                    });

                            if (contendproductphotofilepath != null
                                    && !contendproductphotofilepath
                                    .equalsIgnoreCase("")) {
                                final String path3 = FileUtil
                                        .hasPicCached(contendproductphotofilepath);
                                if (path3 != null
                                        && !path3.equalsIgnoreCase("")) {// 下载地址存在
                                    iv_plan_pics_3.setImageBitmap(BitmapFactory
                                            .decodeFile(path3));
                                    iv_plan_pics_3
                                            .setOnClickListener(new ImageView.OnClickListener() {

                                                @Override
                                                public void onClick(View arg0) {
                                                    Intent intent = new Intent();
                                                    intent.setClass(
                                                            PlanDetailActivity.this,
                                                            ImageViewActivity.class);
                                                    intent.putExtra(
                                                            "imagefilename",
                                                            path3);
                                                    startActivity(intent);
                                                }
                                            });

                                } else {
                                    ISaleApplication application = (ISaleApplication) getApplication();
                                    if (application != null) {
                                        downloadtask = new FileDownloadTask(
                                                application,
                                                PlanDetailActivity.this,
                                                "contendproduct",
                                                contendproductphotoid,
                                                downloadeventlistener, false);
                                        downloadtask.execute();
                                    }
                                }

                            }

                        }
                    }
                }

                if (contendproductphotoid != null
                        && !contendproductphotoid.equalsIgnoreCase("")) {
                    iv_plan_pics_3.setVisibility(View.VISIBLE);
                    final String path = FileUtil
                            .hasPicCached(contendproductphotofilepath);
                    if (path != null && !path.equalsIgnoreCase("")) {// 下载地址存在
                        iv_plan_pics_3.setImageBitmap(BitmapFactory
                                .decodeFile(path));
                        iv_plan_pics_3
                                .setOnClickListener(new ImageView.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        Intent intent = new Intent();
                                        intent.setClass(
                                                PlanDetailActivity.this,
                                                ImageViewActivity.class);
                                        intent.putExtra("imagefilename", path);
                                        startActivity(intent);
                                    }
                                });
                    }
                }

            }
        }


    }
}
