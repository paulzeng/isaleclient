package com.zjrc.isale.client.service;

/**
 * 项目名称：情报收集系统客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：开机状态监听类
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	protected static final String TAG = "BootBroadcastReceiver";
	
	@Override 
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){ 
//			Log.i("BootBroadcastReceiver", "BootBroadcastReceiver start AlarmReceiver...");
//	    	Intent intentalarm =new Intent(context,AlarmReceiver.class);
//	    	intentalarm.setAction("com.zjrc.isale.client.alarm.action");
//	    	PendingIntent pendingintent= PendingIntent.getBroadcast(context, 10000, intentalarm, 0);
//	    	AlarmManager alarmmanager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//	    	alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(), 5*1000, pendingintent);
        }		
	}
}
