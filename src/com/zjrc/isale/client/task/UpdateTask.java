package com.zjrc.isale.client.task;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：版本更新任务类
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;

import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;

public class UpdateTask extends AsyncTask<Void, Integer, Boolean> {

	// 上下文
	private Context context = null;

	// 升级文件下载URL
	private String downloadurl;

	// 升级文件本地存放位置
	private String filename;

	// 提示对话框
	private CustomProgressDialog progressdialog;

	private CustomAlertDialog alertDialog;

	private IUpdateEventListener updateeventlistener;

	// 处理是否成功
	private boolean taskcanceled;

	public UpdateTask(Context context, String downloadurl, String filename,
			IUpdateEventListener updateeventlistener) {
		this.context = context;
		this.downloadurl = downloadurl;
		this.filename = filename;
		this.updateeventlistener = updateeventlistener;
		this.taskcanceled = false;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		Boolean bdownloaded = false;
		FileOutputStream fileoutputstream = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(downloadurl);
			HttpResponse httpresponse = httpclient.execute(httpget);
			if (httpresponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpentity = httpresponse.getEntity();
				long filesize = httpentity.getContentLength();
				InputStream inputstream = httpentity.getContent();
				if (inputstream != null) {
					File file = new File(filename);
					File path = new File(file.getParentFile().getPath());
					if (!path.exists()) {
						path.mkdirs();
					}
					fileoutputstream = new FileOutputStream(file);
					byte[] readbuf = new byte[1024];
					int readlen = -1;
					long receivelen = 0;
					while (!taskcanceled
							&& (readlen = inputstream.read(readbuf)) != -1) {
						receivelen += readlen;
						fileoutputstream.write(readbuf, 0, readlen);
						publishProgress((int) (receivelen / 1024));
					}
					if (receivelen == filesize) {
						fileoutputstream.flush();
						bdownloaded = true;
					}
				}
			}
		} catch (Exception e) {

		} finally {
			if (fileoutputstream != null) {
				try {
					fileoutputstream.close();
				} catch (Exception e) {

				}
				fileoutputstream = null;
			}
		}
		return bdownloaded;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (this.progressdialog != null) {
			String strMsg = "正在更新，已下载" + String.valueOf(values[0]) + "KB";
			progressdialog.setMessage(strMsg);
		}
	}

	@Override
	protected void onPreExecute() {
		this.progressdialog = new CustomProgressDialog(this.context,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						cancelTask();
					}
				});
		this.progressdialog.show();
		this.progressdialog.setMessage("正在更新，请稍等...");

	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			updateeventlistener.onFinish();
		} else {

			alertDialog = new CustomAlertDialog(context);
			alertDialog.show();
			alertDialog.setTitle("提示");
			alertDialog.setMessage("下载更新文件失败");
			alertDialog.getBtn_positive().setText("确定");
			alertDialog.getBtn_negative().setVisibility(View.GONE);
			alertDialog.setOnPositiveListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			});

		}
		if (this.progressdialog != null) {
			this.progressdialog.cancel();
			this.progressdialog = null;
		}
	}

	public void cancelTask() {
		taskcanceled = true;
		this.cancel(true);
		if (this.progressdialog != null) {
			this.progressdialog.cancel();
			this.progressdialog = null;
		}
	}
}
