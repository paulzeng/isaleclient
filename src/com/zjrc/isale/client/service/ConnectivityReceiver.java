package com.zjrc.isale.client.service;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：网络连接状态监听类
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {

	private static final String TAG="PhoneStateChangeListener";

	private final LocationService locationservice;

    public ConnectivityReceiver(LocationService locationservice) {
        this.locationservice = locationservice;
    }

    @Override 
    public void onReceive(Context context, Intent intent) {
//        Log.i(TAG, "ConnectivityReceiver.onReceive()...");
        String action = intent.getAction();
//        Log.i(TAG, "action=" + action);

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
//            Log.i(TAG, "Network Type  = " + networkInfo.getTypeName());
//            Log.i(TAG, "Network State = " + networkInfo.getState());
            if (networkInfo.isConnected()) {
//                Log.i(TAG, "Network connected");
                if (locationservice!=null){
            		locationservice.getConnectionservice().connect();
            	}
            }
        }
    }

}
