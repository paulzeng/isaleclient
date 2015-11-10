package com.zjrc.isale.client.application;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：应用类
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zjrc.isale.client.bean.Area;
import com.zjrc.isale.client.bean.Audit;
import com.zjrc.isale.client.bean.Config;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.bean.TerminalType;
import com.zjrc.isale.client.socket.ConnectionService;
import com.zjrc.isale.client.util.LogUtil;
import com.zjrc.isale.client.volley.RequestManager;

import java.util.ArrayList;

public class ISaleApplication extends Application {

    private static ISaleApplication instance = null;

    private final static String TAG = "ISaleApplication";

    // 数据后台传输服务
    private static ConnectionService connectionservice = new ConnectionService();

    // 百度地图
    private BMapManager baidumapmanager = null;

    // 百度地图Key
    public static final String strKey = "C425396F4266330E96CAF8130DD4134E16932EC5";

    // 系统配置信息
    private Config config = null;

    private ArrayList<Area> areas = null;
    private ArrayList<Audit> audit = null;
    private ArrayList<TerminalType> terminaltypes = null;
    private ArrayList<Activity>activitys=new ArrayList<Activity>();

    private int[] unAuditNumbers = new int[5];

    public static ISaleApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initBaiduMapManager(this);
        initVolley(this);
        config = new Config();
        readConfig();
        Log.i(TAG, "ISaleApplication onCreate Start AlarmReceiver");
        if (LogUtil.IS_WRITEDEBUG)
            LogUtil.writeLog("ISaleApplication onCreate Start AlarmReceiver");
        // 启动定位服务
//        Intent intentalarm = new Intent(this.getApplicationContext(),
//                AlarmReceiver.class);
//        intentalarm.setAction("com.zjrc.isale.client.alarm.action");
//        PendingIntent pendingintent = PendingIntent.getBroadcast(
//                this.getApplicationContext(), 10000, intentalarm, 0);
//        AlarmManager alarmmanager = (AlarmManager) this.getApplicationContext()
//                .getSystemService(Context.ALARM_SERVICE);
//        alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP,
//                SystemClock.elapsedRealtime(), 5 * 1000, pendingintent);
    }

    @Override
    public void onTerminate() {
        if (connectionservice != null) {
            connectionservice.destroy();
            connectionservice = null;
        }
        if (baidumapmanager != null) {
            baidumapmanager.destroy();
            baidumapmanager = null;
        }
        super.onTerminate();
    }

    /**
     * 初始化百度地图
     *
     * @param context
     */
    public void initBaiduMapManager(Context context) {
        if (baidumapmanager == null) {
            baidumapmanager = new BMapManager(context);
        }

        if (baidumapmanager != null) {
            if (!baidumapmanager.init(strKey, new MyGeneralListener())) {
                Toast.makeText(getApplicationContext(), "BMapManager  初始化错误!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 获取百度地图Manager
     *
     * @return
     */
    public BMapManager getBaiduMapManager() {
        return this.baidumapmanager;
    }

    /**
     * 获取传输服务
     *
     * @return
     */
    public ConnectionService getConnectionService() {
        return this.connectionservice;
    }

    /**
     * 设置配置信息
     *
     * @param config
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * 获取配置信息
     *
     * @return
     */
    public Config getConfig() {
        if(config==null){
            config = new Config();
            readConfig();
        }else if(config.getUserid()==null||config.getUserid().equals("")){
            readConfig();
        }
        return config;
    }

    public ArrayList<Area> getAreas() {
        if(areas==null){
            Config config =getConfig();
            String areas_str = config.getAreas();
            Gson gson = new Gson();
            areas =gson.fromJson(areas_str, new TypeToken<ArrayList<Area>>(){}.getType()) ;
        }
        return areas;
    }

    public void setAreas(ArrayList<Area> areas) {
        this.areas = areas;
    }

    public ArrayList<Audit> getAudit() {
        if(audit==null){
            Config config =getConfig();
            String audits_str = config.getAudits();
            Gson gson = new Gson();
            audit =gson.fromJson(audits_str, new TypeToken<ArrayList<Audit>>(){}.getType()) ;
        }
        return audit;
    }

    public void setAudit(ArrayList<Audit> audit) {
        this.audit = audit;
    }

    public ArrayList<TerminalType> getTerminaltypes() {
        if(terminaltypes==null){
            Config config =getConfig();
            String terminal_str = config.getTerminalTypes();
            Gson gson = new Gson();
            terminaltypes =gson.fromJson(terminal_str, new TypeToken<ArrayList<TerminalType>>(){}.getType()) ;
        }
        return terminaltypes;
    }

    public void setTerminaltypes(ArrayList<TerminalType> terminaltypes) {
        this.terminaltypes = terminaltypes;
    }

    public int[] getUnAuditNumbers() {
        return unAuditNumbers;
    }

    public void setUnAuditNumbers(int[] unAuditNumbers) {
        this.unAuditNumbers = unAuditNumbers;
    }

    /**
     * 读取配置信息
     *
     * @return
     */
    public void readConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                Constant.PREFERENCE_NAME, Context.MODE_PRIVATE);
        // config.setServerip(sharedPreferences.getString(Constant.SERVER_IP,
        // "120.199.26.226"));//192.168.137.1"120.199.26.226"
        // config.setServerport(sharedPreferences.getInt(Constant.SERVER_PORT,
        // 9002));

        // // 资源池环境
        // config.setServerip("isale.zjrcinfo.com");
        // config.setServerport(9010);

        // 资源池测试环境
        config.setServerip("221.181.41.162");
        config.setServerport(8080);

        // //内网测试环境
        // config.setServerip("120.199.26.226");
        // config.setServerport(9684);

//		//本人环境
//		 config.setServerip("120.199.26.226");
//		 config.setServerport(9616);

        // 110云服务器测试环境
//		config.setServerip("111.1.58.110");
//		config.setServerport(9010);

        // // 资源池测试环境
//		config.setServerip("192.168.1.115");
//		config.setServerport(9010);
        config.setPhoneno(sharedPreferences.getString(Constant.PHONE_NO, ""));
        config.setPassword(sharedPreferences.getString(Constant.PASSWORD, ""));
        config.setSavepassword(sharedPreferences.getBoolean(
                Constant.SAVE_PASSWORD, false));
        config.setAutologin(sharedPreferences.getBoolean(Constant.AUTO_LOGIN,
                false));
        config.setFirstLogin(sharedPreferences.getBoolean(Constant.FIRST_LOGIN,
                true));
        config.setUserid(sharedPreferences.getString(Constant.USER_ID,
                ""));
        config.setAreas(sharedPreferences.getString(Constant.AREAS,
                ""));
        config.setAudits(sharedPreferences.getString(Constant.AUDITS,
                ""));
        config.setTerminalTypes(sharedPreferences.getString(Constant.TERMINAL_TYPES,
                ""));
        Log.i(TAG,
                "ISaleApplication read config [ServerIp:"
                        + config.getServerip() + "|ServerPort:"
                        + config.getServerport() + "|PhoneNo:"
                        + config.getPhoneno() + "|Password:"
                        + config.getPassword());
        if (LogUtil.IS_WRITEDEBUG)
            LogUtil.writeLog("ISaleApplication read config [ServerIp:"
                    + config.getServerip() + "|ServerPort:"
                    + config.getServerport() + "|PhoneNo:"
                    + config.getPhoneno() + "|Password:" + config.getPassword());
    }

    /**
     * 写配置文件
     *
     * @param config
     */
    public void writeConfig(Config config) {
        Log.i(TAG, "write config...");
        if (LogUtil.IS_WRITEDEBUG)
            LogUtil.writeLog("ISaleApplication write config...");
        if (config != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(
                    Constant.PREFERENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constant.SERVER_IP, config.getServerip());
            editor.putInt(Constant.SERVER_PORT, config.getServerport());
            editor.putString(Constant.PHONE_NO, config.getPhoneno());
            editor.putString(Constant.PASSWORD, config.getPassword());
            editor.putBoolean(Constant.SAVE_PASSWORD, config.getSavepassword());
            editor.putBoolean(Constant.AUTO_LOGIN, config.getAutologin());
            editor.putBoolean(Constant.FIRST_LOGIN, config.getFirstLogin());
            editor.putString(Constant.USER_ID, config.getUserid());
            editor.putString(Constant.AREAS, config.getAreas());
            editor.putString(Constant.AUDITS, config.getAudits());
            editor.putString(Constant.TERMINAL_TYPES, config.getTerminalTypes());
            editor.commit();
        }
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(
                        ISaleApplication.getInstance().getApplicationContext(),
                        "您的网络出错啦！", Toast.LENGTH_LONG).show();
            } else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(
                        ISaleApplication.getInstance().getApplicationContext(),
                        "输入正确的检索条件！", Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
                // 授权Key错误：
                Toast.makeText(
                        ISaleApplication.getInstance().getApplicationContext(),
                        "请在 ISaleApplication.java文件输入正确的授权Key！",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addActivity(Activity activity){
        activitys.add(activity);
    }

    public void removeActivity(Activity activity){
        if (checkActivity(activity))
            activitys.remove(activity);
    }

    public boolean checkActivity(Activity activity){
        if (activity!=null&&activitys.contains(activity)) {
            return true;
        }
        return false;
    }
    public void dropTask(){
        if(activitys!=null&&activitys.size()>1){
            for (Activity activity:activitys){
                activity.finish();
            }
        }
    }

    /**
     * @Title: initVolley
     * @Description: 初始化网络通讯队列
     * @param @param context
     * @return void
     * @throws
     */
    public void initVolley(Context context){
        RequestManager.init(context);
    }
}
