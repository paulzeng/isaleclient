package com.zjrc.isale.client.socket.event;

/**
 * 项目名称：情报收集系统客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：事件通知类
 */

import java.util.ArrayList;

public class EventNotifier {

	private ArrayList<IEventListener> eventlisteners = null;

	/**
	 * 构造函数
	 */
	public EventNotifier() {
		eventlisteners = new ArrayList<IEventListener>();
	}

	/**
	 * 添加事件监听
	 * 
	 * @param IEventListener
	 *            eventlistener 监听器
	 */
	public synchronized void addEventListener(IEventListener eventlistener) {
		if (!eventlisteners.contains(eventlistener)) {
			eventlisteners.add(eventlistener);
		}
	}

	/**
	 * 移除事件监听
	 * 
	 * @param IEventListener
	 *            eventlistener 监听器
	 */
	public synchronized void removeEventListener(IEventListener eventlistener) {
		if (eventlisteners.contains(eventlistener)) {
			eventlisteners.remove(eventlistener);
		}
	}

	/**
	 * 清除事件监听
	 */
	public synchronized void clearEventListener() {
		eventlisteners.clear();
	}

	/**
	 * 通知开始连接事件
	 */
	public void fireOnConecting() {
		for (int i = eventlisteners.size() - 1; i >= 0; i--) {
			((IEventListener) eventlisteners.get(i)).onConecting();
		}
	}

	/**
	 * 通知连接成功事件
	 */
	public void fireOnConnectSuccess() {
		for (int i = eventlisteners.size() - 1; i >= 0; i--) {
			((IEventListener) eventlisteners.get(i)).onConnectSuccess();
		}
	}

	/**
	 * 通知连接失败事件
	 */
	public void fireOnConnectFail(String message) {
		for (int i = eventlisteners.size() - 1; i >= 0; i--) {
			((IEventListener) eventlisteners.get(i)).onConnectFail(message);
		}
	}

	/**
	 * 通知发送失败事件
	 */
	public void fireOnSendFail(byte[] requestdata, String message) {
		for (int i = eventlisteners.size() - 1; i >= 0; i--) {
			((IEventListener) eventlisteners.get(i)).onSendFail(requestdata,
					message);
		}
	}
	
	/**
	 * 通知接收失败事件
	 */
	public void fireOnReiceiveFail(byte[] requestdata, String message) {
		for (int i = eventlisteners.size() - 1; i >= 0; i--) {
			((IEventListener) eventlisteners.get(i)).onReiceiveFail(requestdata,
					message);
		}
	}
	/**
	 * 通知接收成功事件
	 */
	public void fireOnReceiveSuccess(byte[] data) {
		for (int i = eventlisteners.size() - 1; i >= 0; i--) {
			if (i <= eventlisteners.size() - 1) {
				((IEventListener) eventlisteners.get(i)).onReceiveSuccess(data);
			}
		}
	}

	/**
	 * 通知连接关闭事件
	 */
	public void fireOnClosed() {
		for (int i = eventlisteners.size() - 1; i >= 0; i--) {
			((IEventListener) eventlisteners.get(i)).onClosed();
		}
	}
}
