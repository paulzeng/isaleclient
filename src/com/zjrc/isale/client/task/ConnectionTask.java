package com.zjrc.isale.client.task;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：通信任务类
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;

import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.socket.ConnectionService;
import com.zjrc.isale.client.socket.event.IEventListener;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;
import com.zjrc.isale.client.util.LogUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ConnectionTask extends AsyncTask<Integer, Void, Boolean> {
	public static final String TAG = "ConnectionTask";
	// 应用程序
	private ISaleApplication isaleapplication = null;

	// 上下文
	private Context context = null;

	// 调用者消息处理器
	private ITaskEventListener taskeventlistener = null;

	// 发送的请求数据
	private XmlNode request = null;

	// 是否需要提示对话框
	private boolean needprogressdialog = true;

	// 提示对话框
	// private ProgressDialog progressdialog;
	private CustomProgressDialog progressdialog;

	// 是否已处理
	private boolean dealed = false;

	// 处理是否成功
	private boolean taskresult = false;

	// 失败消息
	private String failedmessage = null;

	// Socket连接服务
	private ConnectionService connectionservice = null;

	// Socket消息处理器
	private EventProcess eventprocess = null;

	// 接收的应答数据
	private XmlNode response = null;

	// 是否可被取消
	private boolean cancelble = false;

	private String requestInfo = "";

	public ConnectionTask(ISaleApplication isaleapplication, Context context,
			ITaskEventListener taskeventlistener, XmlNode request,
			boolean needprogressdialog) {
		this.isaleapplication = isaleapplication;
		this.context = context;
		this.taskeventlistener = taskeventlistener;
		this.request = request;
		this.needprogressdialog = needprogressdialog;
	}

	public ConnectionTask(ISaleApplication isaleapplication, Context context,
			ITaskEventListener taskeventlistener, XmlNode request,
			boolean needprogressdialog, boolean cancelble) {
		this.isaleapplication = isaleapplication;
		this.context = context;
		this.taskeventlistener = taskeventlistener;
		this.request = request;
		this.needprogressdialog = needprogressdialog;
		this.cancelble = cancelble;
	}

	@Override
	protected Boolean doInBackground(Integer... params) {
		this.dealed = false;
		this.taskresult = false;
		this.failedmessage = "";
		this.connectionservice = isaleapplication.getConnectionService();
		this.eventprocess = new EventProcess();
		this.connectionservice.addEventListener(eventprocess);
		this.connectionservice.init(isaleapplication.getConfig().getServerip(),
				isaleapplication.getConfig().getServerport());
		requestInfo = request.createXML();
		if (params.length > 1) {
			if (params[1] == 0)
				this.connectionservice.sendFirst(requestInfo);// 顶级任务,压入队列顶端
            else
				this.connectionservice.sendNormal(requestInfo);// 顶级任务,压入队列顶端
		} else {
			this.connectionservice.sendFirst(requestInfo);// 顶级任务,压入队列顶端
		}
		long starttime = SystemClock.elapsedRealtime();
		while ((SystemClock.elapsedRealtime() - starttime) < params[0] * 1000) {
			if (dealed) {
				break;
			}
			try {
				Thread.sleep(50); // 10240 *10 = 102400; 老版本至少5秒1张;3张至少15秒;
			} catch (InterruptedException e) {
			}
		}
		if (!dealed) {
			this.taskresult = false;
			this.failedmessage = "信息交互失败，失败原因[超时]...";
		}
		return taskresult;
	}

	@Override
	protected void onPreExecute() {
		if (this.needprogressdialog) {
			this.progressdialog = new CustomProgressDialog(this.context,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							cancelTask();
						}
					}, cancelble);
			this.progressdialog.show();
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		this.connectionservice.removeEventListener(eventprocess);
		if (taskeventlistener != null) {
			if (result) {
				taskeventlistener.onTaskSuccess(response);
			} else {
				taskeventlistener.onTaskFailed(failedmessage);
			}
		}
		taskeventlistener = null;
		if (this.needprogressdialog) {
			if (this.progressdialog != null) {
				this.progressdialog.cancel();
				this.progressdialog = null;
			}
		}
	}

	public void cancelTask() {
		this.cancel(true);
		if (this.connectionservice != null) {
			this.connectionservice.removeEventListener(eventprocess);
		}
		taskeventlistener = null;
		eventprocess = null;
		if (this.needprogressdialog) {
			if (this.progressdialog != null) {
				this.progressdialog.cancel();
				this.progressdialog = null;
			}
		}
	}

	// Socket事件处理器
	private class EventProcess implements IEventListener {

		@Override
		public void onConecting() {

		}

		@Override
		public void onConnectSuccess() {

		}

		@Override
		public void onConnectFail(String message) {
			dealed = true;
			taskresult = false;
			failedmessage = "数据交互失败，失败原因[" + message + "]...";
			LogUtil.e(TAG, "Socket连接失败,失败原因[" + message + "]...");
		}

		@Override
		public void onSendFail(byte[] requestdata, String message) {
			LogUtil.e(TAG, "数据发送失败,失败原因[" + message + "]...");

			// String srequest =
			// Charset.forName("UTF-8").decode(ByteBuffer.wrap(requestdata)).toString();
			// XmlNode requestxml = XmlParser.parserXML(srequest, "UTF-8");
			// if (requestxml!=null){
			// if
			// (requestxml.getText("root.functionno").equalsIgnoreCase(request.getText("root.functionno"))){
			// dealed = true;
			// taskresult = false;
			// failedmessage = "数据交互失败，失败原因[数据发送失败]...";
			// }
			// }

		}

		@Override
		public void onReceiveSuccess(byte[] responsedata) {
			String sresponse = Charset.forName("UTF-8")
					.decode(ByteBuffer.wrap(responsedata)).toString();
			LogUtil.i("myLog", sresponse);
			if (sresponse != null) {
				XmlNode responsexml = XmlParser.parserXML(sresponse, "UTF-8");
				if (responsexml != null) {
					String sresfuntionno = responsexml
							.getText("root.functionno");
					if (sresfuntionno != null) {
						if (sresfuntionno.equalsIgnoreCase(request
								.getText("root.functionno"))) {// 请求与应答相同
							dealed = true;
							taskresult = true;
							failedmessage = "数据交互成功...";
							response = responsexml;
						}
					}
				}
			}
		}

		@Override
		public void onClosed() {
			// dealed = true;
			// taskresult = false;
			// failedmessage = "数据交互失败，失败原因[连接已断开]...";
			// LogUtil.e(TAG, "数据交互失败，失败原因[连接已断开]...");
		}

		@Override
		public void onReiceiveFail(byte[] requestdata, String message) {
			dealed = true;
			taskresult = false;
			failedmessage = message;
		}
	}
}
