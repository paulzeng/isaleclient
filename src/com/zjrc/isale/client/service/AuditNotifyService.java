package com.zjrc.isale.client.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.task.ConnectionTask;
import com.zjrc.isale.client.task.ITaskEventListener;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.Timer;
import java.util.TimerTask;

public class AuditNotifyService extends BaseService {
    public static final String FILTER = "com.zjrc.isale.client_AUDIT_NOTIFICATION";
    public static final String REFETCH_FILTER = "com.zjrc.isale.client_AUDIT_REFETCH";

    // 每隔 xx 秒获取未审批数
    private static final long DELAY = 100 * 1000;

    private Timer timer;

    // 上一次未审批数
    private int mVacation = -1;
    private int mTravel = -1;
    private int mPlan = -1;
    private int mNotice = -1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 重新获取未审批数量
            AuditNotifyService.this.sendQuery();
        }
    };

    // 重新获取广播接收者，供审批成功/新增审批任务成功后使用
    private BroadcastReceiver refetchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain(mHandler, 0);
            mHandler.sendMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(refetchReceiver, new IntentFilter(REFETCH_FILTER));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendQuery();
            }
        }, 0, DELAY);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        unregisterReceiver(refetchReceiver);

        super.onDestroy();
    }

    public void sendQuery() {
        ISaleApplication application = ISaleApplication.getInstance();
        if (application != null) {
            String srequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
            srequest += "<root>";
            srequest += "<functionno>" + Constant.UNAUDIT_BADGES + "</functionno>";
            srequest += "<audituserid>" + XmlValueUtil.encodeString(application.getConfig().getUserid()) + "</audituserid>";
            srequest += "</root>";
            XmlNode requestxml = XmlParser.parserXML(srequest, "UTF-8");
            sendRequestNormal(requestxml);
        }

//        ISaleApplication application = (ISaleApplication)getApplication();
//        if (application != null) {
//            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("audituserid", XmlValueUtil.encodeString(application.getConfig().getUserid()));
//            request("audit!unauditBadgesCount?code=" + Constant.UNAUDIT_BADGES, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
//        }
    }

    @Override
    public void onRecvData(XmlNode response) {
        if (response != null) {
            String functionno = response.getText("root.functionno");
            if (functionno.equalsIgnoreCase(Constant.UNAUDIT_BADGES)) {
                XmlNode badges = response.getChildNode("badges");
                if (badges != null) {
                    int vacation = Integer.parseInt(badges.getChildNodeText("vacation"));
                    int travel = Integer.parseInt(badges.getChildNodeText("travel"));
                    int plan = Integer.parseInt(badges.getChildNodeText("plan"));
                    int notice = Integer.parseInt(badges.getChildNodeText("notice"));
                    int total = Integer.parseInt(badges.getChildNodeText("totalnum"));

                    // 只在和上次审批数不一致时发送广播
                    if (vacation != mVacation || travel != mTravel || plan != mPlan || notice != mNotice) {
                        Intent intent = new Intent(FILTER);
                        intent.putExtra("total", total);
                        intent.putExtra("vacation", vacation);
                        intent.putExtra("travel", travel);
                        intent.putExtra("plan", plan);
                        intent.putExtra("notice", notice);
                        // 通知UI更新
                        sendBroadcast(intent);

                        mVacation = vacation;
                        mTravel = travel;
                        mPlan = plan;
                        mNotice = notice;

                        // 存入application，供审批页面初始化时使用
                        ISaleApplication application = ISaleApplication.getInstance();
                        application.setUnAuditNumbers(new int[] {total, plan, travel, notice, vacation});
                    }
                }
            }
        }
    }
}

abstract class BaseService extends Service {
    private ConnectionTask task = null;

    private ITaskEventListener taskeventlistener = new ITaskEventListener() {
        @Override
        public void onTaskSuccess(XmlNode response) {
            if (response != null) {
                Log.i("myLog", response.createXML());
                String result = response.getText("root.result");
                if (result != null && result.compareToIgnoreCase("ok") == 0) {
                    BaseService.this.onRecvData(response);
                } else {
                    String message = response.getText("root.errorinfo");
                    Log.i("myLog", message);
                }
            }
        }

        @Override
        public void onTaskFailed(String message) {
            Log.i("myLog", message);
        }
    };

    public void sendRequestNormal(XmlNode request) {
        Log.i("myLog", request.createXML());
        task = new ConnectionTask((ISaleApplication) getApplication(), this, taskeventlistener, request, false);
        task.execute(30,1);
    }

    public abstract void onRecvData(XmlNode response);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (task != null) {
            task.cancelTask();
            task = null;
        }

        super.onDestroy();
    }
}
