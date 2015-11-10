package com.zjrc.isale.client.socket.event;

/**
 * 项目名称：情报收集系统客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：Socket事件通知接口
 */

public interface IEventListener {
	
	/**
	 * 开始连接服务器的消息
	 */
	public void onConecting();
	
	/**
	 * 连接服务器成功的消息
	 */
	public void onConnectSuccess();
	
	/**
	 * 连接服务器失败的消息
	 * @param message 连接失败原因 
	 */
	public void onConnectFail(String message);
	
	/**
	 * 发送数据包失败的消息
	 * @param message 发送失败原因
	 */
	public void onSendFail(byte[] requestdata,String message);
	
	/**
	 * 接收数据包失败的消息
	 * @param message 接收失败原因
	 */
	public void onReiceiveFail(byte[] requestdata,String message);
	
	
	/**
	 * 接收数据包成功的消息
	 */
	public void onReceiveSuccess(byte[] responsedata);
	
	/**
	 * 连接已经断开的消息
	 */
	public void onClosed();

}
