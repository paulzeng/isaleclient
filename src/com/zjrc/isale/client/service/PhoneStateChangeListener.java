package com.zjrc.isale.client.service;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：电话状态监听类
 */

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneStateChangeListener extends PhoneStateListener {

	private static final String TAG="PhoneStateChangeListener";

    private final LocationService locationservice;

    public PhoneStateChangeListener(LocationService locationservice) {
        this.locationservice = locationservice;
    }

    @Override 
    public void onDataConnectionStateChanged(int state) {
        super.onDataConnectionStateChanged(state);
//        Log.d(TAG, "onDataConnectionStateChanged()...");
//        Log.d(TAG, "Data Connection State = " + getState(state));
        if (state == TelephonyManager.DATA_DISCONNECTED){
        	if (locationservice!=null){
        		locationservice.getConnectionservice().disconnect();
        	}
        }else if (state == TelephonyManager.DATA_CONNECTED) {
        	if (locationservice!=null){
        		locationservice.getConnectionservice().connect();
        	}
        }
    }

    private String getState(int state) {
        switch (state) {
        case 0: // '\0'
            return "DATA_DISCONNECTED";
        case 1: // '\001'
            return "DATA_CONNECTING";
        case 2: // '\002'
            return "DATA_CONNECTED";
        case 3: // '\003'
            return "DATA_SUSPENDED";
        }
        return "DATA_<UNKNOWN>";
    }

}
