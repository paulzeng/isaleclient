package com.zjrc.isale.client.util;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者:高林荣
 * 功能描述：日志记录类
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class LogUtil {
	private static final String TAG = "LogUtil";
	public static final boolean IS_DEBUG = true;
	public static final boolean IS_WRITEDEBUG = false;
	public static final String LOG_PATH = Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator
			+ "isale"
			+ File.separator + "log";
	public static final String LOG_FILENAME = "isaleclient.txt";

	public static void writeLog(String content) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File fp = new File(LOG_PATH);
			if (!fp.exists()) {
				Log.i(TAG, "make dir" + LOG_PATH + "...");
				fp.mkdirs();
			}
			File file = new File(LOG_PATH + File.separator + LOG_FILENAME);
			FileOutputStream fout = null;
			try {
				fout = new FileOutputStream(file, true);
				SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
				String sLog = dateformat.format(new Date()) + "  " + content
						+ "\n";
				fout.write(sLog.getBytes());
			} catch (IOException e) {
				Log.i(TAG, "write error" + e.getMessage() + "...");
			} finally {
				if (fout != null) {
					try {
						fout.close();
					} catch (IOException e) {
					}
					fout = null;
				}
			}
		}
	}

	public static void d(String tag, String content) {
		if (IS_DEBUG) {
			Log.d(tag, content);
		}
	}

	public static void e(String tag, String content) {
		if (IS_DEBUG) {
			Log.e(tag, content);
		}
	}
	public static void i(String tag, String content) {
		if (IS_DEBUG) {
			Log.i(tag, content);
		}
	}
}