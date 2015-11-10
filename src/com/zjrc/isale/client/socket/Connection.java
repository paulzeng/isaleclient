package com.zjrc.isale.client.socket;

/**
 * 项目名称：情报收集系统客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：Socket连接类
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedList;

import android.os.SystemClock;
import android.util.Log;

import com.zjrc.isale.client.socket.event.EventNotifier;
import com.zjrc.isale.client.util.ByteUtil;

public class Connection {
	private static String LOG_TAG = "Connection";

	private static int MAX_LENGTH = 4096;

	// 连接实例
	private static Connection connection = null;

	// 用于通信的Socket通道
	private Socket socket = null;

	// Socket输入流
	private InputStream is = null;

	// Socket输出流
	private OutputStream os = null;

	// 远程地址
	private String remoteaddress = "";

	// 远程端口
	private int remoteport = 0;

	// 本地地址
	private String localaddress = "";

	// 本地端口
	private int localport = 0;

	// 写超时时间
	private int connecttimeout = 0;

	// 读超时时间
	private int readtimeout = 0;

	// 是否已连接
	private boolean connected = false;

	// 是否正在进行重连
	private boolean connecting = false;

	// 写缓冲区
	private LinkedList<byte[]> writerqueue = null;

	// 读线程
	private ReadWriteThread readwritethead = null;

	// 消息处理
	private EventNotifier eventnotifier = null;

	// 最后一次收到包时间
	private long lastreceivetime;

	/**
	 * 获取单实例
	 * 
	 * @return
	 */
	public static Connection getInstance() {
		if (connection == null) {
			connection = new Connection();
		}
		return connection;
	}

	/**
	 * 构造函数
	 */
	public Connection() {
		remoteaddress = "";
		remoteport = 0;
		localaddress = "";
		localport = 0;
		connecttimeout = 0;
		readtimeout = 0;
		connected = false;
		connecting = false;
		writerqueue = new LinkedList<byte[]>();
		eventnotifier = new EventNotifier();
		lastreceivetime = SystemClock.elapsedRealtime();
	}

	/**
	 * 获取设置连接超时时间
	 * 
	 * @return
	 */
	public int getConnecttimeout() {
		return this.connecttimeout;
	}

	/**
	 * 设置连接超时时间
	 * 
	 * @param int
	 */
	public void setConnecttimeout(int connecttimeout) {
		this.connecttimeout = connecttimeout;
	}

	/**
	 * 获取读超时时间
	 * 
	 * @return
	 */
	public int getReadtimeout() {
		return this.readtimeout;
	}

	/**
	 * 设置读超时时间
	 * 
	 * @param sotimeout
	 */
	public void setReadtimeout(int readtimeout) {
		this.readtimeout = readtimeout;
	}

	/**
	 * 获取远程连接地址
	 * 
	 * @return
	 */
	public String getRemoteaddress() {
		return this.remoteaddress;
	}

	/**
	 * 设置远程连接地址
	 * 
	 * @param remoteaddress
	 */
	public void setRemoteaddress(String remoteaddress) {
		this.remoteaddress = remoteaddress;
	}

	/**
	 * 获取远程连接端口
	 * 
	 * @return
	 */
	public int getRemoteport() {
		return this.remoteport;
	}

	/**
	 * 设置远程连接端口
	 * 
	 * @param remoteport
	 */
	public void setRemoteport(int remoteport) {
		this.remoteport = remoteport;
	}

	/**
	 * 获取本地连接地址
	 * 
	 * @return
	 */
	public String getLocaladdress() {
		return this.localaddress;
	}

	/**
	 * 获取本地连接端口
	 * 
	 * @return
	 */
	public int getLocalport() {
		return this.localport;
	}

	/**
	 * 判断是否已连接
	 */
	public boolean getConnected() {
		return this.connected;
	}

	/**
	 * 判断是否正在连接
	 * 
	 * @return
	 */
	public boolean getConnecting() {
		return connecting;
	}

	/**
	 * 获取消息通知
	 * 
	 * @return
	 */
	public EventNotifier getEventnotifier() {
		return eventnotifier;
	}

	public long getLastreceivetime() {
		return lastreceivetime;
	}

	/**
	 * 开始连接消息
	 */
	private void fireOnConnecting() {
		Log.i("info", "fireOnConnecting");
		if (eventnotifier != null) {
			eventnotifier.fireOnConecting();
		}
	}

	/**
	 * 发送连接成功消息
	 */
	private void fireOnConnectSuccess() {
		Log.i("info", "fireOnConnectSuccess");
		if (eventnotifier != null) {
			eventnotifier.fireOnConnectSuccess();
		}
	}

	/**
	 * 发送连接失败消息
	 */
	private void fireOnConnectFail(String message) {
		Log.i("info", "fireOnConnectFail");
		if (eventnotifier != null) {
			eventnotifier.fireOnConnectFail(message);
		}
	}

	/**
	 * 发送包发送失败消息
	 */
	private void fireOnSendFail(byte[] requestdata, String message) {
		Log.i("info", "fireOnSendFail");
		if (eventnotifier != null) {
			eventnotifier.fireOnSendFail(requestdata, message);
		}
	}

	/**
	 * 发送包接收失败消息
	 */
	private void fireOnReceiveFail(byte[] requestdata, String message) {
		if (eventnotifier != null) {
			eventnotifier.fireOnReiceiveFail(requestdata, message);
		}
	}

	/**
	 * 发送接收到包的消息
	 * 
	 * @param reqdata
	 * @param resdata
	 */
	private void fireOnReceiveSuccess(byte[] data) {
		if (eventnotifier != null) {
			eventnotifier.fireOnReceiveSuccess(data);
		}
		System.gc();
	}

	/**
	 * 发送连接已断开的消息
	 */
	private void fireOnConnectClosed() {
		Log.i("info", "fireOnConnectClosed");
		if (eventnotifier != null) {
			eventnotifier.fireOnClosed();
		}
	}

	/**
	 * 初始化连接参数
	 * 
	 * @param remoteaddress
	 * @param remoteport
	 * @param connecttimeout
	 * @param readtimeout
	 */
	public void init(String remoteaddress, int remoteport, int connecttimeout,
			int readtimeout) {
		this.remoteaddress = remoteaddress;
		this.remoteport = remoteport;
		this.readtimeout = readtimeout;
		this.connecttimeout = connecttimeout;
		this.lastreceivetime = SystemClock.elapsedRealtime();
	}

	/**
	 * 连接服务器
	 */
	public void connect() {
		if (!connected && !connecting) {
			new Thread(new Runnable() {
				public void run() {
					connecting = true;
					connected = false;
					try {
						if (readwritethead != null) {
							readwritethead.setStop(true);
							readwritethead.interrupt();
							readwritethead = null;
						}
						if (socket != null) {
							try {
								socket.shutdownInput();
								socket.shutdownOutput();
								socket.close();
							} catch (IOException e1) {
							} catch (Exception e1) {
							}
							is = null;
							os = null;
							socket = null;
						}
						Log.i(LOG_TAG, "connection try connect to server[ip:"
								+ remoteaddress + ",port:" + remoteport
								+ "]...");
						socket = new Socket();
						fireOnConnecting();
						InetSocketAddress socketAddress = new InetSocketAddress(
								remoteaddress, remoteport);
						socket.connect(socketAddress, connecttimeout * 1000);
						is = socket.getInputStream();
						os = socket.getOutputStream();
						// 设置读取超时时间
						socket.setSoTimeout(readtimeout);
						// 设置socket数据不作缓冲，立即发送
						socket.setTcpNoDelay(true);
						// 设置socket关闭时立即释放资源
						socket.setSoLinger(true, 0);
						// 设置socket保持连接
						socket.setKeepAlive(true);
						// 设置socket发送缓冲区大小
						socket.setSendBufferSize(32768);
						// 设置socket接收缓冲区大小
						socket.setReceiveBufferSize(32768);
						localaddress = socket.getLocalAddress()
								.getHostAddress();
						localport = socket.getLocalPort();
						// 启动发送线程
						readwritethead = new ReadWriteThread();
						readwritethead.start();
						lastreceivetime = SystemClock.elapsedRealtime();
						fireOnConnectSuccess();
						connected = true;
						connecting = false;
					} catch (UnknownHostException e) {
						Log.i(LOG_TAG, "connection connect to [ip:"
								+ remoteaddress + ",port:" + remoteport
								+ "]failed,causeby[" + e.getMessage() + "]");
						if (socket != null) {
							try {
								socket.shutdownInput();
								socket.shutdownOutput();
								socket.close();
							} catch (IOException e1) {
							} catch (Exception e1) {
							}
							is = null;
							os = null;
							socket = null;
						}
						connected = false;
						connecting = false;
						fireOnConnectFail("连接服务器失败");
					} catch (SocketTimeoutException e) {
						Log.i(LOG_TAG, "connection connect to [ip:"
								+ (remoteaddress != null ? remoteaddress : "")
								+ ",port:" + remoteport + "]failed,causeby["
								+ e.getMessage() + "]");
						if (socket != null) {
							try {
								socket.shutdownInput();
								socket.shutdownOutput();
								socket.close();
							} catch (IOException e1) {
							} catch (Exception e1) {
							}
							is = null;
							os = null;
							socket = null;
						}
						connected = false;
						connecting = false;
						fireOnConnectFail("连接服务器失败");
					} catch (IOException e) {
						Log.i(LOG_TAG, "connection connect to [ip:"
								+ remoteaddress + ",port:" + remoteport
								+ "]failed,causeby[" + e.getMessage() + "]");
						if (socket != null) {
							try {
								socket.shutdownInput();
								socket.shutdownOutput();
								socket.close();
							} catch (IOException e1) {
							} catch (Exception e1) {
							}
							is = null;
							os = null;
							socket = null;
						}
						connected = false;
						connecting = false;
						fireOnConnectFail("连接服务器失败");
					} catch (Exception e) {
						Log.i(LOG_TAG, "connection connect to [ip:"
								+ remoteaddress + ",port:" + remoteport
								+ "]failed,causeby[" + e.getMessage() + "]");
						if (socket != null) {
							try {
								socket.shutdownInput();
								socket.shutdownOutput();
								socket.close();
							} catch (IOException e1) {
							} catch (Exception e1) {
							}
							is = null;
							os = null;
							socket = null;
						}
						connected = false;
						connecting = false;
						fireOnConnectFail("连接服务器失败");
					}
				}
			}).start();
		}
	}

	/**
	 * 请求断开连接
	 */
	public void disconnect() {
		if (connected && !connecting) {
			Log.i(LOG_TAG, "connection try disconnect from server[ip:"
					+ remoteaddress + ",port:" + remoteport + "]...");
			// 停止读写线程
			if (readwritethead != null) {
				readwritethead.setStop(true);
				readwritethead.interrupt();
				readwritethead = null;
			}
			// 关闭Socket
			if (socket != null) {
				try {
					socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
				} catch (IOException e) {
					Log.i(LOG_TAG, "connection try disconnect from server[ip:"
							+ remoteaddress + ",port:" + remoteport
							+ "]failed,cause by[" + e.getMessage() + "]");
				} catch (Exception e) {
					Log.i(LOG_TAG, "connection try disconnect from server[ip:"
							+ remoteaddress + ",port:" + remoteport
							+ "]failed,cause by[" + e.getMessage() + "]");
				}
				is = null;
				os = null;
				socket = null;
			}
			fireOnConnectClosed();
			connected = false;
			connecting = false;
		}
	}

	/**
	 * 按最高优先级请求发送数据(数据将放到待发送队列队头)
	 * 
	 * @param data
	 */
	public void sendDataFirst(byte[] data) {
		// 将待发送数据放到待发送队列队头
		if (writerqueue != null) {
			synchronized (writerqueue) {
				writerqueue.add(0, data);
				writerqueue.notifyAll();
			}
		}
	}

	/**
	 * 按正常优先级请求发送数据
	 * 
	 * @param data
	 */
	public void sendDataNormal(byte[] data) {
		// 将待发送数据放到待发送队列队尾
		if (writerqueue != null) {
			synchronized (writerqueue) {
				writerqueue.add(writerqueue.size(), data);
				writerqueue.notifyAll();
			}
		}
	}

	/**
	 * 释放连接
	 */
	public void destroy() {
		// 停止读写线程
		if (readwritethead != null) {
			readwritethead.setStop(true);
			readwritethead.interrupt();
			readwritethead = null;
		}
		if (writerqueue != null) {
			writerqueue.clear();
			writerqueue = null;
		}
		// 关闭Socket
		if (socket != null) {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			} catch (IOException e) {
				Log.i(LOG_TAG,
						"connection  disconnect failed,cause by["
								+ e.getMessage() + "]");
			} catch (Exception e) {
				Log.i(LOG_TAG,
						"connection  disconnect failed,cause by["
								+ e.getMessage() + "]");
			}
			is = null;
			os = null;
			socket = null;
		}
		fireOnConnectClosed();
		if (eventnotifier != null) {
			eventnotifier.clearEventListener();
			eventnotifier = null;
		}
		connecting = false;
		connected = false;
	}

	/**
	 * 读写线程
	 */
	private class ReadWriteThread extends Thread {
		private boolean stop = false;

		/**
		 * 接收数据
		 */
		private void read() {
			if (is != null) {
				byte[] blength = new byte[4];
				// 读取数据长度
				int ireadlen = 0;
				int iPos = 0;
				int iRemain = 4;
				while (iRemain > 0) {
					try {
						ireadlen = is.read(blength, iPos, iRemain);
					} catch (SocketTimeoutException e) {
						Log.i(LOG_TAG,
								"SocketTimeoutException read data from connection,read length failed,cause by["
										+ e.getMessage() + "]");
					} catch (SocketException e) {
						Log.i(LOG_TAG,
								"SocketException read data from connection failed,cause by["
										+ e.getMessage() + "]");
						setStop(true);
						disconnect();
						return;
					} catch (IOException e) {
						Log.i(LOG_TAG,
								"IOException read data from connection,read length failed,cause by["
										+ e.getMessage() + "]");
						setStop(true);
						disconnect();
						return;
					} catch (OutOfMemoryError ome) {
						Log.i(LOG_TAG,
								"OutOfMemoryError read data from connection failed,cause by["
										+ ome.getMessage() + "]");
						setStop(true);
						disconnect();
						return;
					} catch (Exception e) {
						Log.i(LOG_TAG,
								"read data from connection,read length failed,cause by["
										+ e.getMessage() + "]");
						setStop(true);
						disconnect();
						return;
					}
					if (ireadlen == -1) {
						Log.i(LOG_TAG,
								"read data from connection failed,cause by[read length -1,disconnected]");
						setStop(true);
						disconnect();
						return;
					}
					if (ireadlen > 0) {
						iPos += ireadlen;
						iRemain -= ireadlen;
					} else {// 等待
						try {
							Thread.sleep(200);
						} catch (Exception e) {
						}
					}
				}
				if (ireadlen == 4) {
					int icontentlength = ByteUtil.byteToInt(blength);
					blength = null;
					byte[] bcontent = null;
					try {
						bcontent = new byte[icontentlength];
					} catch (OutOfMemoryError ome) {
						Log.i(LOG_TAG,
								"read data from connection failed,cause by["
										+ ome.getMessage() + "]");
						setStop(true);
						disconnect();
						return;
					} catch (Exception ee) {
						Log.i(LOG_TAG,
								"read data from connection failed,cause by["
										+ ee.getMessage() + "]");
						setStop(true);
						disconnect();
						return;
					}
					iPos = 0;
					iRemain = icontentlength;
					while (iRemain > 0) {
						int ilength = iRemain;
						if (ilength > MAX_LENGTH) {
							ilength = MAX_LENGTH;
						}
						try {
							ireadlen = is.read(bcontent, iPos, ilength);
						} catch (SocketTimeoutException e) {
							Log.i(LOG_TAG,
									"SocketTimeoutException read data from connection,read content failed,cause by["
											+ e.getMessage()
											+ " "
											+ e.toString() + "]");
							setStop(true);
							disconnect();
							lastreceivetime = SystemClock.elapsedRealtime();
							fireOnReceiveFail(bcontent, "接收数据超时");
							System.gc();
							return;
						} catch (SocketException e) {
							Log.i(LOG_TAG,
									"SocketException read data from connection,read content failed,cause by["
											+ e.getMessage() + "]");
							setStop(true);
							disconnect();
							return;
						} catch (IOException e) {
							Log.i(LOG_TAG,
									"IOException read data from connection,read content failed,cause by["
											+ e.getMessage() + "]");
							setStop(true);
							disconnect();
							return;
						} catch (OutOfMemoryError ome) {
							Log.i(LOG_TAG,
									"OutOfMemoryError read data from connection failed,cause by["
											+ ome.getMessage() + "]");
							setStop(true);
							disconnect();
							return;
						} catch (Exception e) {
							Log.i(LOG_TAG,
									"read data from connection,read length failed,cause by["
											+ e.getMessage() + "]");
							setStop(true);
							disconnect();
							return;
						}
						if (ireadlen == -1) {
							Log.i(LOG_TAG,
									"read data from connection failed,cause by[read length -1,disconnected]");
							setStop(true);
							disconnect();
							return;
						}
						if (ireadlen > 0) {
							iPos += ireadlen;
							iRemain -= ireadlen;
						} else {// 等待
							try {
								Thread.sleep(200);
							} catch (Exception e) {
							}
						}
					}
					lastreceivetime = SystemClock.elapsedRealtime();
					fireOnReceiveSuccess(bcontent);
				}
			}
		}

		/**
		 * 发送数据
		 */
		private void write() {
			if (os != null) {
				synchronized (writerqueue) {
					if (!writerqueue.isEmpty()) {
						byte[] data = writerqueue.get(0);
						writerqueue.remove(data);
						try {
							int iRemain = data.length;
							int iPos = 0;
							while (iRemain > 0) {
								int iLength = iRemain;
								if (iLength > MAX_LENGTH)
									iLength = MAX_LENGTH;
								os.write(data, iPos, iLength);
								os.flush();
								lastreceivetime = SystemClock.elapsedRealtime();
								iPos += iLength;
								iRemain -= iLength;
							}
						} catch (SocketException e) {
							Log.i(LOG_TAG,
									"connection send data failed,cause by["
											+ e.getMessage() + "]");
							writerqueue.addFirst(data);
							setStop(true);
							disconnect();
						} catch (IOException e) {
							Log.i(LOG_TAG,
									"connection send data failed,cause by["
											+ e.getMessage() + "]");
							writerqueue.addFirst(data);
						}
					}
				}
			} else {
				Log.i(LOG_TAG,
						"connection send data failed,cause by[socket=null]");
				setStop(true);
				disconnect();
			}
		}

		/**
		 * 线程运行
		 */
		public void run() {
			while (!stop) {
				try {
					try {
						if (is.available() > 4) {
							read();
							// System.gc();
						} else {
							write();
							// System.gc();
						}
					} catch (Exception e) {
						Log.i(LOG_TAG, "connection read data failed,cause by["
								+ e.getMessage() + "]");
						System.gc();
						setStop(true);
						disconnect();
					}
					Thread.sleep(200);
				} catch (InterruptedException e1) {

				}
			}
		}

		public void setStop(boolean stop) {
			this.stop = stop;
		}
	}
}
