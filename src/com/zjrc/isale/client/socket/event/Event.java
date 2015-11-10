package com.zjrc.isale.client.socket.event;

/**
 * 项目名称：情报收集系统客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：事件类
 */

public abstract class Event implements IEventListener {

	@Override 
	public void onConecting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectFail(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendFail(byte[] requestdata,String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveSuccess(byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClosed() {
		// TODO Auto-generated method stub

	}

}
