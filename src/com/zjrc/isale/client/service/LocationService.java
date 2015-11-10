package com.zjrc.isale.client.service;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：定位服务类
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.socket.ConnectionService;
import com.zjrc.isale.client.socket.event.IEventListener;
import com.zjrc.isale.client.util.LogUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;

public class LocationService extends Service {
	private static String TAG="LocationService";
	
	public static final String TASK_PATH=Environment.getExternalStorageDirectory().getPath()+File.separator+ "isale"+File.separator+"task";
	public static final String TASK_FILENAME = "locationtask.txt";
	
	private static LocationService locationservice = null;
	
	private static int token = 1;
	
	private static int tasktoken = 1001;
	
	private ISaleApplication isaleapplication = null;
	
	private ConnectionService connectionservice = null;
	
	private TelephonyManager telephonymanager = null;
    
	private LocationClient locationclient = null;
	
	private BDLocationListener locationlistener = null;	 
	
	public HashMap<String,LocationTask> locationtasks = null;
	
	private BDLocation currentlocation = null;
	
	//Socket消息处理器
	private EventProcess eventprocess = null;
	
	public static LocationService getService() {
		return locationservice;
	}
	
	@Override 
    public void onCreate() {
		Log.i(TAG, "LocationService onCreate()");
		if (LogUtil.IS_WRITEDEBUG)
			LogUtil.writeLog("LocationService onCreate()");				
		//初始化定位任务线程池
		locationtasks = new HashMap<String,LocationTask>();		
		telephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonymanager.getDeviceId()!=null){
			Log.i(TAG,telephonymanager.getDeviceId());
		}
		//创建网络连接
		isaleapplication = (ISaleApplication)getApplication();
		connectionservice = isaleapplication.getConnectionService();
		eventprocess = new EventProcess();
		connectionservice.addEventListener(eventprocess);
		connectionservice.init(isaleapplication.getConfig().getServerip(), isaleapplication.getConfig().getServerport());
		//声明LocationClient类    
		locationclient = new LocationClient(getApplicationContext());     
		//注册监听函数
		locationlistener = new MyLocationListener();
		locationclient.registerLocationListener(locationlistener);
    	Intent intent =new Intent(this,ReconnectReceiver.class);     
    	PendingIntent pendingintent= PendingIntent.getBroadcast(this, 9999, intent, 0);
    	AlarmManager alarmmanager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);    
    	alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(), 10*1000, pendingintent);		
		super.onCreate();
		locationservice = this;
		readTaskFromFile();
		Iterator iter = locationtasks.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry entry = (Map.Entry) iter.next();
    		LocationTask task = (LocationTask)entry.getValue();
    		if (task.getOperatetype()==0){//停止定位请求
				//暂时不处理
			}else if (task.getOperatetype()==1){//单次定位请求
				Log.i(TAG, "onCreate load OneTimeLocationTask,Token"+task.getToken()+",SerialNo["+task.getSerialno()+"],startOneTimeLocationTaskReciever");
				if (LogUtil.IS_WRITEDEBUG)
					LogUtil.writeLog("onCreate load OneTimeLocationTask,Token"+task.getToken()+",SerialNo["+task.getSerialno()+"],startOneTimeLocationTaskReciever");						
				startOneTimeLocationTaskReciever(task);
			}else if (task.getOperatetype()==2){//定时定位请求
				Log.i(TAG, "onCreate load ManyTimeLocationTask,Token"+task.getToken()+",SerialNo["+task.getSerialno()+"]Times["+task.getLocationtimes()+"]InteralTime["+task.getInteraltime()+"]startManyTimeLocationTaskReciever");
				if (LogUtil.IS_WRITEDEBUG)
					LogUtil.writeLog("onCreate load ManyTimeLocationTask,Token"+task.getToken()+",SerialNo["+task.getSerialno()+"]Times["+task.getLocationtimes()+"]InteralTime["+task.getInteraltime()+"]startManyTimeLocationTaskReciever");						
				startManyTimeLocationTaskReciever(task);						
			}
    	}		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {	
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent){
		return super.onUnbind(intent);
	}  	
	
	@Override
	public void onStart(Intent intent, int i){
		super.onStart(intent, i);
	}	
	
    @Override
    public void onDestroy() {
		Log.i(TAG, "LocationService onDestroy()");
		if (LogUtil.IS_WRITEDEBUG)
			LogUtil.writeLog("LocationService onDestroy()");
		stopForeground(true);
		List<LocationTask> list = new ArrayList<LocationTask>();
		Iterator iter = locationtasks.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry entry = (Map.Entry) iter.next();
    		list.add((LocationTask)entry.getValue());
    	}
    	for (LocationTask task:list){
    		stopLocationTaskReceiver(task);
    	}
    	locationtasks = null;
    	//停止定位
    	if (locationclient!=null){
    		if (locationlistener!=null){
    			//移除定位监听函数
    	    	locationclient.unRegisterLocationListener(locationlistener);
    	    	locationlistener = null;
    		}
	    	if (locationclient.isStarted()){
	    		//停止百度定位
	    		locationclient.stop();
	    	}
	    	locationclient = null;
    	}
        //停止重连和发送心跳Alarm
    	Intent intent =new Intent(this,ReconnectReceiver.class);      
    	PendingIntent pendingintent= PendingIntent.getBroadcast(this, 9999, intent, 0);
    	AlarmManager alarmmanager=(AlarmManager)getSystemService(ALARM_SERVICE);    
    	alarmmanager.cancel(pendingintent);        
        //移除Socket事件监听
    	connectionservice.removeEventListener(eventprocess);
    	super.onDestroy();
    }	
    
	public ConnectionService getConnectionservice() {
		return connectionservice;
	}

	public void setConnectionservice(ConnectionService connectionservice) {
		this.connectionservice = connectionservice;
	}    
    
    public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null && location.getLatitude()!=4.9E-324 && location.getLongitude()!=4.9E-324){	
				currentlocation = location;
			}
		}
		@Override
		public void onReceivePoi(BDLocation location) {
			
		}
    }
    
    /**
     * 保存定位任务到文件
     */
    public static void writeTaskToFile(){
    	if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			File fp = new File(TASK_PATH);
			if (!fp.exists()) {
				Log.i(TAG, "make dir"+TASK_PATH+"...");
				fp.mkdirs();
			}
			File file = new File(TASK_PATH + File.separator + TASK_FILENAME);
			ObjectOutputStream  oos = null;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(file)); 
				List<LocationTask> list = new ArrayList<LocationTask>();
				Iterator iter = locationservice.locationtasks.entrySet().iterator();
		    	while (iter.hasNext()) {
		    		Map.Entry entry = (Map.Entry) iter.next();
		    		list.add((LocationTask)entry.getValue());
		    	}
		    	oos.writeObject(list);
			} catch (IOException e) {
				Log.i(TAG, "write error"+e.getMessage()+"...");
			} catch (Exception e){
				Log.i(TAG, "write error"+e.getMessage()+"...");
    		}finally {
				if (oos != null) {
					try {
						oos.close();
					} catch (IOException e) {
	
					}
					oos = null;
				}
			}
		}    	
    }
    
    /**
     * 从文件读取定位任务
     */
    public static void readTaskFromFile(){
    	if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			File fp = new File(TASK_PATH);
			if (!fp.exists()) {
				Log.i(TAG, "make dir"+TASK_PATH+"...");
				fp.mkdirs();
			}
			File file = new File(TASK_PATH + File.separator + TASK_FILENAME);
			ObjectInputStream  ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(file)); 
				List<LocationTask> list = new ArrayList<LocationTask>();  
				list = (List<LocationTask>)ois.readObject();
				for (LocationTask task:list){
					locationservice.locationtasks.put(task.getSerialno(), task);
				}			
			} catch (IOException e) {
				Log.i(TAG, "read error"+e.getMessage()+"...");
			} catch (ClassNotFoundException e) {
				Log.i(TAG, "read error"+e.getMessage()+"...");
			} catch (Exception e){
				Log.i(TAG, "read error"+e.getMessage()+"...");
    		}finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
	
					}
					ois = null;
				}
			}
		}     	
    }

    public static class ReconnectReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if (locationservice!=null){
				PowerManager pm = (PowerManager) locationservice.getSystemService(Context.POWER_SERVICE);                
				WakeLock wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());                       
				wakelock.acquire(); 				
				Log.i(TAG, "ReconnectReceiver call checkConnectAndPulse()");
				if (LogUtil.IS_WRITEDEBUG)
					LogUtil.writeLog("ReconnectReceiver call checkConnectAndPulse()");
				locationservice.connectionservice.checkConnectAndPulse(); 
				wakelock.release();  
			}
		}
    }      
    
    public static class LocationTaskReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String serialno = intent.getStringExtra("serialno");
			if (serialno!=null){
                if (locationservice!=null){                	
					LocationTask task = locationservice.locationtasks.get(serialno);
					if (task!=null){
						task.setSubmitcount(task.getSubmitcount()+1);
	    				Log.i(TAG, "LocationTaskReceiver call LocationTaskThread(),Token"+task.getToken()+",submitcount["+task.getSubmitcount()+"]");
	    				if (LogUtil.IS_WRITEDEBUG)
	    					LogUtil.writeLog("LocationTaskReceiver call LocationTaskThread(),Token"+task.getToken()+",submitcount["+task.getSubmitcount()+"]");				
						LocationTaskThread taskthread = new LocationTaskThread(task);
						taskthread.start();
					}
                }
			}
		}
    }  
    
    public static class LocationTaskThread extends Thread {
    	private LocationTask task;

    	public LocationTaskThread(LocationTask task){
    		this.task = task;
    	}
    	
    	@Override
        public void run() {    		
    		if (locationservice!=null){   
				Log.i(TAG, "LocationTaskThread run()");
				if (LogUtil.IS_WRITEDEBUG)
					LogUtil.writeLog("LocationTaskThread run()");	    			
	   			locationservice.locationclient.start();
		    	LocationClientOption option = new LocationClientOption();
		    	option.setOpenGps(true);
		    	option.setAddrType("all");//返回的定位结果包含地址信息
		    	option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		    	option.setScanSpan(4000);//设置发起定位请求的间隔时间为4000ms
		    	option.disableCache(true);//禁止启用缓存定位
		    	option.setPoiNumber(1);	//最多返回POI个数	
		    	option.setPoiDistance(1000); //poi查询距离		
		    	option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息		   			
		    	locationservice.locationclient.setLocOption(option);
		    	locationservice.locationclient.requestLocation();	
		    	long lstartTime = SystemClock.elapsedRealtime();
				while((SystemClock.elapsedRealtime()-lstartTime<(task.getInteraltime()-1)*1000)){
					if (locationservice.currentlocation!=null){					
	    				Log.i(TAG, "LocationTaskThread call sendLocation()");
	    				if (LogUtil.IS_WRITEDEBUG)
	    					LogUtil.writeLog("LocationTaskThread call sendLocation()");							
						locationservice.sendLocation(task,locationservice.currentlocation);
						break;
					}
				}
				locationservice.locationclient.stop();
				if (task.getOperatetype()==1){//单次定位
					locationservice.stopLocationTaskReceiver(task);
				}else if (task.getOperatetype()==2){//定时定位 
					if (task.getSubmitcount()>=task.getLocationtimes()){
						locationservice.stopLocationTaskReceiver(task);
					}
				}
				writeTaskToFile();
				Log.i(TAG, "LocationTaskThread run()");
				if (LogUtil.IS_WRITEDEBUG)
					LogUtil.writeLog("LocationTaskThread run()");					
    		}
    	}
    }
    
    /**
     * 启动单次定位定时广播
     * @param task
     */
    private void startOneTimeLocationTaskReciever(LocationTask task){
    	Intent intent =new Intent(this,LocationTaskReceiver.class);    
    	intent.putExtra("serialno", task.getSerialno());    
    	PendingIntent pendingintent= PendingIntent.getBroadcast(this, task.getToken(), intent, 0);                
    	AlarmManager alarmmanager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);  
    	alarmmanager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,  SystemClock.elapsedRealtime()+100, pendingintent);   	
    }
    
    /**
     * 启动定时定位定时广播
     * @param task
     */
    private void startManyTimeLocationTaskReciever(LocationTask task){
    	Intent intent =new Intent(this,LocationTaskReceiver.class);    
    	intent.putExtra("serialno", task.getSerialno());  
    	PendingIntent pendingintent= PendingIntent.getBroadcast(this, task.getToken(), intent, 0);
    	AlarmManager alarmmanager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);    
    	alarmmanager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,  SystemClock.elapsedRealtime()+100,task.getInteraltime()*1000, pendingintent);
    } 
    
    /**
     * 停止定时广播
     */
    private void stopLocationTaskReceiver(LocationTask task){
		Log.i(TAG, "call stopLocationTaskReceiver(),Token"+task.getToken()+",totalsubmitcount["+task.getSubmitcount()+"]");
		if (LogUtil.IS_WRITEDEBUG)
			LogUtil.writeLog("call stopLocationTaskReceiver(),Token"+task.getToken()+",totalsubmitcount["+task.getSubmitcount()+"]");						    	
    	Intent intent =new Intent(this,LocationTaskReceiver.class);      
    	PendingIntent pendingintent= PendingIntent.getBroadcast(this, task.getToken(), intent, 0);
    	AlarmManager alarmmanager=(AlarmManager)getSystemService(ALARM_SERVICE);    
    	alarmmanager.cancel(pendingintent);
    	locationtasks.remove(task.getSerialno());
    }
    
    /**
     * 初始化Socket连接
     */
    public void init(){
    	if (connectionservice!=null && isaleapplication!=null){
    		connectionservice.init(isaleapplication.getConfig().getServerip(), isaleapplication.getConfig().getServerport());
    		connectionservice.connect();
    	}
    }
    
    /**
     * 处理消息
     * @param message
     */
    private void processMesssage(XmlNode xml){
    	if (xml!=null){
    		String functionno = xml.getText("root.functionno");
    		if (functionno!=null){
	    		if (functionno.equalsIgnoreCase(Constant.LOCATION_LOGIN)){//登录请求返回包
	    			String result = xml.getText("root.result");	
	    			if (result!=null){
						if ("ok".equalsIgnoreCase(result)){//身份校验成功
							isaleapplication.getConfig().setCompanyid(xml.getChildNodeText("companyid"));
							isaleapplication.getConfig().setUserid(xml.getChildNodeText("userid"));
							isaleapplication.getConfig().setUsername(xml.getChildNodeText("username"));
							isaleapplication.getConfig().setClientversion(xml.getChildNodeText("clientversion"));
							isaleapplication.getConfig().setClientdownloadurl(xml.getChildNodeText("clientdownloadurl"));							
						}else{//身份认证失败
							Log.i(TAG, "device not register");
		    				if (LogUtil.IS_WRITEDEBUG)
		    					LogUtil.writeLog("device not register");
						}
	    			}
				}else if (functionno.equalsIgnoreCase(Constant.LOCATION_TASK_PUSH)){//定位任务消息推送包
					String id = xml.getText("root.id");
					String content = xml.getText("root.content");
					if (content!=null && id!=null){
						connectionservice.sendFirst("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><functionno>"+Constant.LOCATION_TASK_PUSH+"</functionno><result>ok</result><id>"+id+"</id></root>");
						token = token + 1;
						if (token>=1000){
							token = 1;
						}
						LocationTask task = new LocationTask(token,content);
						if (!locationtasks.containsKey(task.getSerialno())){
							locationtasks.put(task.getSerialno(),task);
							if (task.getOperatetype()==0){//停止定位请求
								//暂时不处理
							}else if (task.getOperatetype()==1){//单次定位请求
			    				Log.i(TAG, "processMesssage receive OneTimeLocationTask,Token"+task.getToken()+",SerialNo["+task.getSerialno()+"],startOneTimeLocationTaskReciever");
			    				if (LogUtil.IS_WRITEDEBUG)
			    					LogUtil.writeLog("processMesssage receive OneTimeLocationTask,Token"+task.getToken()+",SerialNo["+task.getSerialno()+"],startOneTimeLocationTaskReciever");						
								startOneTimeLocationTaskReciever(task);
							}else if (task.getOperatetype()==2){//定时定位请求
								Log.i(TAG, "processMesssage receive ManyTimeLocationTask,Token"+task.getToken()+",SerialNo["+task.getSerialno()+"]Times["+task.getLocationtimes()+"]InteralTime["+task.getInteraltime()+"]startManyTimeLocationTaskReciever");
			    				if (LogUtil.IS_WRITEDEBUG)
			    					LogUtil.writeLog("processMesssage receive ManyTimeLocationTask,Token"+task.getToken()+",SerialNo["+task.getSerialno()+"]Times["+task.getLocationtimes()+"]InteralTime["+task.getInteraltime()+"]startManyTimeLocationTaskReciever");						
								startManyTimeLocationTaskReciever(task);						
							}
						}
					}
				}
    		}
    	}
    } 
    
    /**
     * 发送定位数据
     * @param task
     * @param location
     */
    private void sendLocation(LocationTask task, BDLocation location){
    	StringBuffer sb = new StringBuffer();
	    sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	    sb.append("<root>");
	    sb.append("<functionno>"+Constant.SUBMIT_LOCATION+"</functionno>");
	    sb.append("<userid>"+isaleapplication.getConfig().getUserid()+"</userid>");
	    sb.append("<serialno>"+task.getSerialno()+"</serialno>");
	    sb.append("<longitude>"+String.valueOf(location.getLongitude())+"</longitude>");
	    sb.append("<latitude>"+String.valueOf(location.getLatitude())+"</latitude>");
	    sb.append("<speed>"+String.valueOf(location.getSpeed())+"</speed>");
	    sb.append("<direction>"+String.valueOf(location.getDerect())+"</direction>");
	    sb.append("<inaccuracy>"+String.valueOf(location.getRadius())+"</inaccuracy>");
	    sb.append("<height>0</height>");
	    sb.append("</root>");
	    connectionservice.sendNormal(sb.toString().trim());
    }
    
	//Socket事件处理器
    private class EventProcess implements IEventListener {

		@Override
		public void onConecting() {
			
		}

		@Override
		public void onConnectSuccess() {
			/** 获取SIM卡的IMSI码
	         * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志，
	         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
	         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
	         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
	         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
	         * 46001 46002 中国移动(因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号)
	         * 46001 中国联通
	         * 46003 中国电信
	         */			
			String imsi = telephonymanager.getSubscriberId();//
			
			if(imsi!=null){
				Log.i(TAG, "imsi"+imsi);
				connectionservice.sendFirst("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><functionno>"+Constant.LOCATION_LOGIN+"</functionno><phoneno>"+isaleapplication.getConfig().getPhoneno()+"</phoneno><deviceid>"+imsi+"</deviceid><devicetype>0</devicetype></root>");
			}
		}

		@Override
		public void onConnectFail(String message) {
					
		}

		@Override
		public void onSendFail(byte[] requestdata,String message) {
						
		}

		@Override
		public void onReceiveSuccess(byte[] responsedata) {
			String sresponse = Charset.forName("UTF-8").decode(ByteBuffer.wrap(responsedata)).toString();
			XmlNode responsexml = XmlParser.parserXML(sresponse, "UTF-8");
			processMesssage(responsexml);
		}

		@Override
		public void onClosed() {
			
		}

		@Override
		public void onReiceiveFail(byte[] requestdata, String message) {
			// TODO Auto-generated method stub
			
		}
    }     
 }
