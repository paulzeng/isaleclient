package com.zjrc.isale.client.database;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：数据库操作类
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.util.LogUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class DatabaseService {
	// 缓冲区大小
	private static final int BUFFER_SIZE = 2048;
	// 数据库文件名
	private static final String DATABASE_NAME = "isale.db";
	// 数据库存放路径
	private static final String DATABASE_PATH = Environment
			.getExternalStorageDirectory().toString() + "/isale/database";

	private SQLiteDatabase database;
	private Context context;

	public DatabaseService(Context context) {
		this.context = context;
		this.database = openDatabase(DATABASE_PATH, DATABASE_NAME);
	}

	/**
	 * 打开指定路径的数据库
	 * 
	 * @param dbpath
	 * @param dbfilename
	 * @return
	 */
	private SQLiteDatabase openDatabase(String dbpath, String dbfilename) {
		SQLiteDatabase db = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			File file = new File(dbpath + "/" + dbfilename);
			// 如果数据库文件不存在，则通过模版数据库文件创建数据库文件
			if (!file.exists()) {
				File fpath = new File(dbpath);
				if (!fpath.exists()) {
					fpath.mkdirs();
				}
				is = context.getResources().openRawResource(R.raw.isale);
				fos = new FileOutputStream(dbpath + "/" + dbfilename);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				if ((is != null) && (fos != null)) {
					while ((count = is.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
						fos.flush();
					}
				}
			}
			db = SQLiteDatabase.openOrCreateDatabase(dbpath + "/" + dbfilename,
					null);
		} catch (FileNotFoundException e) {
			LogUtil.writeLog("创建数据库失败，原因是[" + e.getMessage() + "]");
		} catch (IOException e) {
			LogUtil.writeLog("创建数据库失败，原因是[" + e.getMessage() + "]");
		} catch (Exception e) {
			LogUtil.writeLog("创建数据库失败，原因是[" + e.getMessage() + "]");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {

				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {

				}
			}
		}
		return db;
	}

	/**
	 * 查找所有省份信息
	 * 
	 * @return
	 * @throws SQLException
	 */
	public synchronized Cursor getProvinceDataCursor() throws SQLException {
		if (this.database != null) {
			Cursor cursor = this.database
					.rawQuery(
							"select provinceid as _id, provincename from province order by provinceid asc",
							null);
			return cursor;
		} else {
			return null;
		}
	}

	/**
	 * 查找所有城市信息
	 * 
	 * @return
	 * @throws SQLException
	 */
	public synchronized Cursor getCityDataCursor(String provinceid)
			throws SQLException {
		if (this.database != null) {
			Cursor cursor = this.database.rawQuery(
					"select cityid as _id, cityname from city where provinceid="
							+ provinceid + " order by cityid asc ", null);
			return cursor;
		} else {
			return null;
		}
	}

	/**
	 * 查找所有区县信息
	 * 
	 * @return
	 * @throws SQLException
	 */
	public synchronized Cursor getZoneDataCursor(String cityid)
			throws SQLException {
		if (this.database != null) {
			Cursor cursor = this.database.rawQuery(
					"select zoneid as _id, zonename from zone where cityid="
							+ cityid + " order by cityid asc ", null);
			return cursor;
		} else {
			return null;
		}
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		if (this.database != null) {
			database.close();
			this.database = null;
		}
	}

}
