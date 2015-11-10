package com.zjrc.isale.client.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.task.FileDownloadTask;
import com.zjrc.isale.client.task.IDownloadEventListener;
import com.zjrc.isale.client.ui.widgets.CustomPopupDialog;
import com.zjrc.isale.client.util.FileUtil;
import com.zjrc.isale.client.util.SerialFileDownloader;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.io.File;
import java.util.HashMap;

public class AuditSubmitActivity extends BaseActivity implements
        OnClickListener, OnTouchListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;

    private TextView tv_signresult;
    private EditText et_signcontent;
    private Button bt_commoncontent;

    private LinearLayout ll_plan;
    private LinearLayout ll_travel;
    private LinearLayout ll_notice;
    private LinearLayout ll_vacation;

    private TextView tv_plan_user;
    private TextView tv_plan_createdate;
    private TextView tv_plan_type;
    private TextView tv_plan_terminal;
    private TextView tv_plan_date;
    private TextView tv_plan_content;
    private LinearLayout ll_patrol_date;
    private TextView tv_patrol_date;
    private RelativeLayout rl_patrol_address;
    private TextView tv_patrol_address;
    private LinearLayout ll_patrol_content;
    private TextView tv_patrol_content;
    private Button btn_map;
    private RelativeLayout rl_patrol_pics;
    private ImageView iv_patrol_pics_1;
    private ImageView iv_patrol_pics_2;
    private ImageView iv_patrol_pics_3;

    private String longitude;
    private String latitude;

    private TextView tv_travel_user;
    private TextView tv_travel_createdate;
    private TextView tv_travel_city;
    private TextView tv_travel_begindate;
    private TextView tv_travel_endtate;
    private TextView tv_travel_days;
    private TextView tv_travel_reason;

    private TextView tv_notice_user;
    private TextView tv_notice_createdate;
    private TextView tv_notice_title;
    private TextView tv_notice_content;
    private TextView tv_notice_noticeuser;

    private TextView tv_vacation_user;
    private TextView tv_vacation_createdate;
    private TextView tv_vacation_type;
    private TextView tv_vacation_begindate;
    private TextView tv_vacation_endtate;
    private TextView tv_vacation_days;
    private TextView tv_vacation_reason;

    private Button btn_submit;

    private Dialog signResultDialog;
    private Dialog commonSignContentDialog;

    private int type = 0;
    private String id;

    private String doorfilepath;
    private String productfilepath;
    private String contendproductfilepath;

    private IDownloadEventListener downloadeventlistener = new IDownloadEventListener() {
        @Override
        public void onFinish(String filetype, String filename) {
            if (filetype.equalsIgnoreCase("door")) {
                doorfilepath = filename;
                if (!TextUtils.isEmpty(doorfilepath)) {
                    iv_patrol_pics_1.setImageBitmap(BitmapFactory.decodeFile(doorfilepath));
                }
            } else if (filetype.equalsIgnoreCase("product")) {
                productfilepath = filename;
                if (!TextUtils.isEmpty(productfilepath)) {
                    iv_patrol_pics_2.setImageBitmap(BitmapFactory.decodeFile(productfilepath));
                }
            } else if (filetype.equalsIgnoreCase("contendproduct")) {
                contendproductfilepath = filename;
                if (!TextUtils.isEmpty(contendproductfilepath)) {
                    iv_patrol_pics_3.setImageBitmap(BitmapFactory.decodeFile(contendproductfilepath));
                }
            }
        }

        @Override
        public void onFail(String filetype, String message) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.v2_activity_audit_submit);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);

        tv_signresult = (TextView) findViewById(R.id.tv_signresult);
        tv_signresult.setOnClickListener(this);

        et_signcontent = (EditText) findViewById(R.id.et_signcontent);
        et_signcontent.setOnTouchListener(this);

        bt_commoncontent = (Button) findViewById(R.id.bt_commoncontent);
        bt_commoncontent.setOnClickListener(this);

        ll_plan = (LinearLayout) findViewById(R.id.ll_plan);
        ll_travel = (LinearLayout) findViewById(R.id.ll_travel);
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        ll_vacation = (LinearLayout) findViewById(R.id.ll_vacation);

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt("type");
        id = bundle.getString("id");
        switch (type) {
            case 1:
                tv_titlebar_title.setText("拜访审批");
                ll_plan.setVisibility(View.VISIBLE);

                tv_plan_user = (TextView) findViewById(R.id.tv_plan_user);
                tv_plan_createdate = (TextView) findViewById(R.id.tv_plan_createdate);
                tv_plan_type = (TextView) findViewById(R.id.tv_plan_type);
                tv_plan_terminal = (TextView) findViewById(R.id.tv_plan_terminal);
                tv_plan_date = (TextView) findViewById(R.id.tv_plan_date);
                tv_plan_content = (TextView) findViewById(R.id.tv_plan_content);
                ll_patrol_date = (LinearLayout) findViewById(R.id.ll_patrol_date);
                tv_patrol_date = (TextView) findViewById(R.id.tv_patrol_date);
                rl_patrol_address = (RelativeLayout) findViewById(R.id.rl_patrol_address);
                tv_patrol_address = (TextView) findViewById(R.id.tv_patrol_address);
                ll_patrol_content = (LinearLayout) findViewById(R.id.ll_patrol_content);
                tv_patrol_content = (TextView) findViewById(R.id.tv_patrol_content);
                btn_map = (Button) findViewById(R.id.btn_map);
                btn_map.setOnClickListener(this);
                rl_patrol_pics = (RelativeLayout) findViewById(R.id.rl_patrol_pics);
                iv_patrol_pics_1 = (ImageView) findViewById(R.id.iv_patrol_pics_1);
                iv_patrol_pics_1.setOnClickListener(this);
                iv_patrol_pics_2 = (ImageView) findViewById(R.id.iv_patrol_pics_2);
                iv_patrol_pics_2.setOnClickListener(this);
                iv_patrol_pics_3 = (ImageView) findViewById(R.id.iv_patrol_pics_3);
                iv_patrol_pics_3.setOnClickListener(this);
                break;
            case 2:
                tv_titlebar_title.setText("差旅审批");
                ll_travel.setVisibility(View.VISIBLE);

                tv_travel_user = (TextView) findViewById(R.id.tv_travel_user);
                tv_travel_createdate = (TextView) findViewById(R.id.tv_travel_createdate);
                tv_travel_city = (TextView) findViewById(R.id.tv_travel_city);
                tv_travel_begindate = (TextView) findViewById(R.id.tv_travel_begindate);
                tv_travel_endtate = (TextView) findViewById(R.id.tv_travel_endtate);
                tv_travel_days = (TextView) findViewById(R.id.tv_travel_days);
                tv_travel_reason = (TextView) findViewById(R.id.tv_travel_reason);
                break;
            case 3:
                tv_titlebar_title.setText("公告审批");
                ll_notice.setVisibility(View.VISIBLE);

                tv_notice_user = (TextView) findViewById(R.id.tv_notice_user);
                tv_notice_createdate = (TextView) findViewById(R.id.tv_notice_createdate);
                tv_notice_title = (TextView) findViewById(R.id.tv_notice_title);
                tv_notice_content = (TextView) findViewById(R.id.tv_notice_content);
                tv_notice_noticeuser = (TextView) findViewById(R.id.tv_notice_noticeuser);
                break;
            case 4:
                tv_titlebar_title.setText("请假审批");
                ll_vacation.setVisibility(View.VISIBLE);

                tv_vacation_user = (TextView) findViewById(R.id.tv_vacation_user);
                tv_vacation_createdate = (TextView) findViewById(R.id.tv_vacation_createdate);
                tv_vacation_type = (TextView) findViewById(R.id.tv_vacation_type);
                tv_vacation_begindate = (TextView) findViewById(R.id.tv_vacation_begindate);
                tv_vacation_endtate = (TextView) findViewById(R.id.tv_vacation_endtate);
                tv_vacation_days = (TextView) findViewById(R.id.tv_vacation_days);
                tv_vacation_reason = (TextView) findViewById(R.id.tv_vacation_reason);
                break;
        }

        if (type != 0 && id != null) {
            request();
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.isNotFastDoubleClick()) {
            switch (v.getId()) {
                case R.id.tv_signresult:
                    if (signResultDialog == null) {
                        signResultDialog = new CustomPopupDialog(
                                this,
                                "审批结果",
                                getResources().getStringArray(R.array.audit_signresult),
                                new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        tv_signresult.setText(((TextView) view).getText());
                                        signResultDialog.dismiss();
                                    }
                                });
                    }
                    signResultDialog.show();
                    break;
                case R.id.bt_commoncontent:
                    if (commonSignContentDialog == null) {
                        commonSignContentDialog = new CustomPopupDialog(
                                this,
                                "常用审批意见",
                                getResources().getStringArray(R.array.audit_content),
                                new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        et_signcontent.setText(((TextView) view).getText());
                                        commonSignContentDialog.dismiss();
                                    }
                                });
                    }
                    commonSignContentDialog.show();
                    break;
                case R.id.btn_submit:
                    sendSubmit();
                    break;
                case R.id.btn_map:
                    Intent intent = new Intent(AuditSubmitActivity.this, BaiduMapActivity.class);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("terminalname", tv_plan_terminal.getText());
                    startActivity(intent);
                    break;
                case R.id.iv_patrol_pics_1:
                    if (!TextUtils.isEmpty(doorfilepath)) {
                        Intent intent1 = new Intent(AuditSubmitActivity.this, ImageViewActivity.class);
                        intent1.putExtra("imagefilename", doorfilepath);
                        startActivity(intent1);
                    }
                    break;
                case R.id.iv_patrol_pics_2:
                    if (!TextUtils.isEmpty(productfilepath)) {
                        Intent intent1 = new Intent(AuditSubmitActivity.this, ImageViewActivity.class);
                        intent1.putExtra("imagefilename", productfilepath);
                        startActivity(intent1);
                    }
                    break;
                case R.id.iv_patrol_pics_3:
                    if (!TextUtils.isEmpty(contendproductfilepath)) {
                        Intent intent1 = new Intent(AuditSubmitActivity.this, ImageViewActivity.class);
                        intent1.putExtra("imagefilename", contendproductfilepath);
                        startActivity(intent1);
                    }
                    break;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Scrolling EditText inside ScrollView
        if (v.getId() == R.id.et_signcontent) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return false;
    }

    private void request() {
        HashMap<String, String> params = new HashMap<String, String>();
        StringBuffer srequest = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        srequest.append("<root>");
        switch (type) {
            case 1: // 拜访审批
                params.put("planid", id);
                request("plan!detail?code=" + Constant.PLAN_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
                break;
            case 2: // 差旅审批
                params.put("corptravelid", id);
                request("travel!detail?code=" + Constant.TRAVEL_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
                break;
            case 3: // 公告审批
                params.put("noticeid", id);
                request("notice!detail?code=" + Constant.NOTICE_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
                break;
            case 4: // 请假审批
                params.put("corpvacationid", id);
                request("vacation!detail?code=" + Constant.VACATION_ITEM, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
                break;
        }

    }

    private void sendSubmit() {
        if (TextUtils.isEmpty(tv_signresult.getText())) {
            Toast.makeText(this, "审批结果不能为空！", Toast.LENGTH_SHORT).show();
            tv_signresult.requestFocus();
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            StringBuffer srequest = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            srequest.append("<root>");
            switch (type) {
                case 1: // 拜访审批
                    params.put("corpplanid", id);
                    params.put("userid", XmlValueUtil.encodeString(application.getConfig().getUserid()));
                    params.put("signresult", getSignResult(tv_signresult.getText().toString()));
                    params.put("signcontent",XmlValueUtil.encodeString(et_signcontent.getText().toString()));
                    request("audit!planAuditSubmit?code=" + Constant.AUDIT_PLAN, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
                    break;
                case 2: // 差旅审批
                    params.put("corptravelid", id);
                    params.put("userid", XmlValueUtil.encodeString(application.getConfig().getUserid()));
                    params.put("signresult", getSignResult(tv_signresult.getText().toString()));
                    params.put("signcontent",XmlValueUtil.encodeString(et_signcontent.getText().toString()));
                    request("audit!travelAuditSubmit?code=" + Constant.AUDIT_TRAVEL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
                    break;
                case 3: // 公告审批
                    params.put("corpusernoticeid", id);
                    params.put("userid", XmlValueUtil.encodeString(application.getConfig().getUserid()));
                    params.put("signresult", getSignResult(tv_signresult.getText().toString()));
                    params.put("signcontent",XmlValueUtil.encodeString(et_signcontent.getText().toString()));
                    request("audit!noticeAuditSubmit?code=" + Constant.AUDIT_NOTICE, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
                    break;
                case 4: // 请假审批
                    params.put("corpvacationid", id);
                    params.put("userid", XmlValueUtil.encodeString(application.getConfig().getUserid()));
                    params.put("signresult", getSignResult(tv_signresult.getText().toString()));
                    params.put("signcontent",XmlValueUtil.encodeString(et_signcontent.getText().toString()));
                    request("audit!vacationAuditSubmit?code=" + Constant.AUDIT_VACATION, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
                    break;
            }

        }
    }

    private String getSignResult(String signresult) {
        String[] signresults = getResources().getStringArray(R.array.audit_signresult);
        String[] signresultindexs = getResources().getStringArray(R.array.audit_signresult_index);
        for (int i = 0; i < signresults.length; i++) {
            if (signresults[i].equalsIgnoreCase(signresult)) {
                return signresultindexs[i];
            }
        }

        return null;
    }

    @Override
    public void onRecvData(final XmlNode response) {
    }

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.VACATION_ITEM.equals(code)) {
            	JsonObject vacation = response.getAsJsonObject("body");
                if (vacation != null) {
                    tv_vacation_user.setText(vacation.get("username").getAsString());
                    tv_vacation_createdate.setText(vacation.get("submitdate").getAsString());
                    String[] types = getResources().getStringArray(R.array.vacationtypes);
                    String type = vacation.get("type").getAsString();
                    Integer index = Integer.valueOf(type);
                    tv_vacation_type.setText(types[index]);
                    tv_vacation_begindate.setText(vacation.get("begindate").getAsString());
                    tv_vacation_endtate.setText(vacation.get("enddate").getAsString());
                    tv_vacation_days.setText(Double.valueOf(vacation.get("vacationdays").getAsString()).intValue() + " 天");
                    tv_vacation_reason.setText(vacation.get("desc").getAsString());
                } else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
            } else if(Constant.TRAVEL_QUERY.equals(code)){
                JsonObject travel = response.getAsJsonObject("body");
                if(travel!=null) {
                    tv_travel_user.setText(travel.get("username").getAsString());
                    tv_travel_createdate.setText(travel.get("date").getAsString());
                    tv_travel_city.setText((travel.get("province") != null ? travel.get("province").getAsString() : "") + (travel.get("province") != null ? travel.get("city").getAsString() : "") + (travel.get("province") != null ? travel.get("zone").getAsString() : ""));
                    tv_travel_begindate.setText(travel.get("begindate").getAsString());
                    tv_travel_endtate.setText(travel.get("enddate").getAsString());
                    tv_travel_days.setText(Double.valueOf(travel.get("days").getAsString()).intValue() + " 天");
                    tv_travel_reason.setText(travel.get("desc").getAsString());
                }else {
                    Toast.makeText(this, "无返回数据!", Toast.LENGTH_SHORT).show();
                }
            } else if(Constant.PLAN_QUERY.equals(code)){
                JsonObject plan = response.getAsJsonObject("body");
                tv_plan_user.setText(plan.get("username").getAsString());
                tv_plan_createdate.setText(plan.get("date").getAsString());
                String[] typeArray = getResources().getStringArray(R.array.plantype);
                String typeString = plan.get("plantype").getAsString();
                int type = TextUtils.isEmpty(typeString) ? 1 : Integer.parseInt(typeString); // 0: 临时任务, 1: 正式任务
                tv_plan_type.setText(typeArray[type + 1] + "任务");
                tv_plan_terminal.setText(plan.get("terminalname").getAsString());
                tv_plan_date.setText(plan.get("plandate").getAsString());
                tv_plan_content.setText(plan.get("plancontent").getAsString());
                if (type == 0) {
                    tv_patrol_date.setText(plan.get("patroldate").getAsString());
                    tv_patrol_address.setText(plan.get("address").getAsString());
                    String longitudeStr = plan.get("longitude").getAsString();
                    String latitudeStr = plan.get("latitude").getAsString();
                    if (!TextUtils.isEmpty(longitudeStr) && !TextUtils.isEmpty(latitudeStr)) {
                        longitude = longitudeStr;
                        latitude = latitudeStr;
                        btn_map.setVisibility(View.VISIBLE);
                    }
                    tv_patrol_content.setText(plan.get("patrolcontent").getAsString());
                    boolean isDoorExists = false;
                    String doorfilename =plan.get("doorfilename").getAsString();
                    if (!TextUtils.isEmpty(doorfilename)) {
                        doorfilepath = FileUtil.hasPicCached(doorfilename);
                        if (new File(doorfilepath).exists()) {
                            iv_patrol_pics_1.setVisibility(View.VISIBLE);
                            iv_patrol_pics_1.setImageBitmap(BitmapFactory.decodeFile(doorfilepath));
                            isDoorExists = true;
                        }
                    }
                    boolean isProductExists = false;
                    String productfilename = plan.get("productfilename").getAsString();
                    if (!TextUtils.isEmpty(productfilename)) {
                        productfilepath = FileUtil.hasPicCached(productfilename);
                        if (new File(productfilepath).exists()) {
                            iv_patrol_pics_2.setVisibility(View.VISIBLE);
                            iv_patrol_pics_2.setImageBitmap(BitmapFactory.decodeFile(productfilepath));
                            isProductExists = true;
                        }
                    }
                    boolean isContendProductExists = false;
                    String contendproductfilename = plan.get("contendproductfilename").getAsString();
                    if (!TextUtils.isEmpty(contendproductfilename)) {
                        contendproductfilepath = FileUtil.hasPicCached(contendproductfilename);
                        if (new File(contendproductfilepath).exists()) {
                            iv_patrol_pics_3.setVisibility(View.VISIBLE);
                            iv_patrol_pics_3.setImageBitmap(BitmapFactory.decodeFile(contendproductfilepath));
                            isContendProductExists = true;
                        }
                    }
                    SerialFileDownloader serialFileDownloader = SerialFileDownloader.getInstance();
                    if (!isDoorExists) {
                        String doorfileid = plan.get("doorfileid").getAsString();
                        if (!TextUtils.isEmpty(doorfileid)) {
                            iv_patrol_pics_1.setVisibility(View.VISIBLE);
                            ISaleApplication application = (ISaleApplication) getApplication();
                            if (application != null) {
                                serialFileDownloader.addTask(new FileDownloadTask(application, AuditSubmitActivity.this, "door", doorfileid, downloadeventlistener, false));
                            }
                        }
                    }
                    if (!isProductExists) {
                        String productfileid = plan.get("productfileid").getAsString();
                        if (!TextUtils.isEmpty(productfileid)) {
                            iv_patrol_pics_2.setVisibility(View.VISIBLE);
                            ISaleApplication application = (ISaleApplication) getApplication();
                            if (application != null) {
                                serialFileDownloader.addTask(new FileDownloadTask(application, AuditSubmitActivity.this, "product", productfileid, downloadeventlistener, false));
                            }
                        }
                    }
                    if (!isContendProductExists) {
                        String contendproductfileid =plan.get("contendproductfileid").getAsString();
                        if (!TextUtils.isEmpty(contendproductfileid)) {
                            iv_patrol_pics_3.setVisibility(View.VISIBLE);
                            ISaleApplication application = (ISaleApplication) getApplication();
                            if (application != null) {
                                serialFileDownloader.addTask(new FileDownloadTask(application, AuditSubmitActivity.this, "contendproduct", contendproductfileid, downloadeventlistener, false));
                            }
                        }
                    }
                    serialFileDownloader.execute();

                    ll_patrol_date.setVisibility(View.VISIBLE);
                    rl_patrol_address.setVisibility(View.VISIBLE);
                    ll_patrol_content.setVisibility(View.VISIBLE);
                    rl_patrol_pics.setVisibility(View.VISIBLE);
                }
            } else if(Constant.AUDIT_VACATION.equals(code)){
                    Toast.makeText(this, "请假审批上报成功!", Toast.LENGTH_SHORT).show();
                    setResult(Constant.RESULT_AUDITLIST_REFRESH);
                    Intent intent = new Intent();
                    intent.setAction(Constant.VACATION_AUDIT_ACTION);
                    sendBroadcast(intent);
                    finish();
            } else if (Constant.AUDIT_TRAVEL.equals(code)) {
                Toast.makeText(this, "差旅审批上报成功!", Toast.LENGTH_SHORT).show();
                setResult(Constant.RESULT_AUDITLIST_REFRESH);
                Intent intent = new Intent();
                intent.setAction(Constant.TRAVEL_AUDIT_ACTION);
                sendBroadcast(intent);
                finish();
            } else if (Constant.AUDIT_PLAN.equals(code)) {
                Toast.makeText(this, "拜访审批上报成功!", Toast.LENGTH_SHORT).show();
                setResult(Constant.RESULT_AUDITLIST_REFRESH);
                Intent intent = new Intent();
                intent.setAction(Constant.PLAN_AUDIT_ACTION);
                sendBroadcast(intent);
                finish();
            } else if (Constant.AUDIT_NOTICE.equals(code)) {
                Toast.makeText(this, "公告审批上报成功!", Toast.LENGTH_SHORT).show();
                setResult(Constant.RESULT_AUDITLIST_REFRESH);
                Intent intent = new Intent();
                intent.setAction(Constant.NOTICE_AUDIT_ACTION);
                sendBroadcast(intent);
                finish();
            } else if (Constant.NOTICE_DETAIL.equals(code)) {//公告详情
            	JsonObject notice = response.getAsJsonObject("body");
                if(notice != null) {
                	final String detailurl = notice.get("detailurl").getAsString();//参数在匿名类内部使用,则必须是final
    				tv_notice_user.setText(notice.get("creator").getAsString());
                    tv_notice_createdate.setText(notice.get("date").getAsString());
                    tv_notice_title.setText(notice.get("title").getAsString());
                    tv_notice_content.setText(Html.fromHtml("<u>点击查看</u>"));
                    tv_notice_content.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CommonUtils.isNotFastDoubleClick()) {
                                Intent intent = new Intent();
                                intent.putExtra("url", detailurl);
                                intent.setClass(AuditSubmitActivity.this, NoticeDetailActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                    String users = notice.get("user").getAsString();
                    tv_notice_noticeuser.setText(TextUtils.isEmpty(users) ? "全体" : users);
                }
            }
        }
    }
}
