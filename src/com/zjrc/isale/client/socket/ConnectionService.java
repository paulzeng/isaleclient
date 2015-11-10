package com.zjrc.isale.client.socket;

/**
 * 项目名称：情报收集系统客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：Socket通信服务类
 */

import android.os.SystemClock;
import android.util.Log;

import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.socket.event.EventNotifier;
import com.zjrc.isale.client.socket.event.IEventListener;
import com.zjrc.isale.client.util.ByteUtil;

public class ConnectionService {
	private static String TAG = "ConnectionService";

	private byte[] data;

	// Socket连接
	private Connection connection = null;

	// Socket底层事件处理器
	private EventProcess eventprocess = null;

	// 向上事件通知
	private EventNotifier eventnotifier = null;

	/**
	 * 构造函数
	 */
	public ConnectionService() {
		connection = Connection.getInstance();
		eventprocess = new EventProcess();
		connection.getEventnotifier().addEventListener(eventprocess);
		eventnotifier = new EventNotifier();
	}

	/**
	 * 添加消息通知
	 * 
	 * @param handler
	 */
	public void addEventListener(IEventListener eventlistener) {
		eventnotifier.addEventListener(eventlistener);
	}

	/**
	 * 移除移除通知
	 * 
	 * @param handler
	 */
	public void removeEventListener(IEventListener eventlistener) {
		eventnotifier.removeEventListener(eventlistener);
	}

	/**
	 * 手工连接服务器
	 * 
	 * @param serverip
	 * @param serverport
	 */
	public void init(String serverip, int serverport) {
		if (connection != null) {
			// 判断连接的目标服务器是否相同
			if (!connection.getRemoteaddress().equalsIgnoreCase(serverip)
					|| connection.getRemoteport() != serverport) {// 已改变
				connection.disconnect();
				connection.init(serverip, serverport, 30, 2000);// readtimeout设置过长很容易堵塞。
				connection.connect();
			} else {// 未改变
				connection.connect();
			}
		}
	}

	/**
	 * 与通信服务器连接
	 */
	public void connect() {
		if (connection != null) {
			connection.connect();
		}
	}

	/**
	 * 断开与通信服务器连接
	 */
	public void disconnect() {
		if (connection != null) {
			connection.disconnect();
		}
	}

	/**
	 * 按高优先级发送请求数据
	 * 
	 * @param request
	 */
	public void sendFirst(String request) {
		if (connection != null) {
			this.data = ByteUtil.EncodeField(request);
			connection.sendDataFirst(ByteUtil.EncodeField(request));
		}
	}

	/**
	 * 按正常优先级发送请求数据
	 * 
	 * @param request
	 */
	public void sendNormal(String request) {
		if (connection != null) {
			this.data = ByteUtil.EncodeField(request);
			connection.sendDataNormal(data);
		}
	}

	/**
	 * 获取Socket连接
	 * 
	 * @return
	 */
	public Connection getConnection() {
		if (connection != null) {
			return connection;
		} else {
			return null;
		}
	}

	/**
	 * 重连并发送心跳包
	 */
	public void checkConnectAndPulse() {
		if (connection != null) {
			if ((SystemClock.elapsedRealtime() - connection
					.getLastreceivetime()) > 40 * 1000) {// 如果超过40秒未收到数据包认为连接已断，重连
				connection.disconnect();
			}
			connection.connect();
			if (connection.getConnected()) {
				if ((SystemClock.elapsedRealtime() - connection
						.getLastreceivetime()) > 30 * 1000) {// 如果超过30秒未收到数据包，则发送心跳包
					connection
							.sendDataNormal(ByteUtil
									.EncodeField("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><functionno>"
											+ Constant.SOCKET_PULSE
											+ "</functionno></root>"));
				}
			}
		}
	}

	public void destroy() {
		connection.getEventnotifier().removeEventListener(eventprocess);
		if (eventnotifier != null) {
			eventnotifier.clearEventListener();
			eventnotifier = null;
		}
		connection.destroy();
		connection = null;
	}

	// Socket事件处理器
	private class EventProcess implements IEventListener {

		@Override
		public void onConecting() {
			if (eventnotifier != null) {
				eventnotifier.fireOnConecting();
			}
		}

		@Override
		public void onConnectSuccess() {
			if (eventnotifier != null) {
				eventnotifier.fireOnConnectSuccess();
			}
		}

		@Override
		public void onConnectFail(String message) {
			if (eventnotifier != null) {
				eventnotifier.fireOnConnectFail(message);
			}
		}

		@Override
		public void onSendFail(byte[] requestdata, String message) {
			if (eventnotifier != null) {
				eventnotifier.fireOnSendFail(requestdata, message);
			}
		}

		@Override
		public void onReceiveSuccess(byte[] responsedata) {
			if (responsedata != null && responsedata.length > 0) {
				if (eventnotifier != null) {
					eventnotifier.fireOnReceiveSuccess(responsedata);
				}
			}
		}

		@Override
		public void onClosed() {
			if (eventnotifier != null) {
				eventnotifier.fireOnClosed();
			}
		}

		@Override
		public void onReiceiveFail(byte[] requestdata, String message) {
			if (eventnotifier != null) {
				eventnotifier.fireOnReiceiveFail(requestdata, message);
			}
		}
	}
}
