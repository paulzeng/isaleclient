package com.zjrc.isale.client.task;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：文件上传任务类
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.activity.BaseActivity;
import com.zjrc.isale.client.ui.activity.LoginActivity;
import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;
import com.zjrc.isale.client.util.DESedeUtilsForServer;
import com.zjrc.isale.client.util.FileUtil;
import com.zjrc.isale.client.util.LogUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.volley.GsonRequest;
import com.zjrc.isale.client.volley.RequestManager;

import java.util.HashMap;
import java.util.Map;

public class FileUploadTask extends AsyncTask<Void, Void, String> {

	// 应用程序
	private ISaleApplication isaleapplication = null;

	// 上下文
	private Context context = null;

	// 待上传文件的类型
	private String filetype;

	// 待上传文件的路径
	private String filename;

	// 待上传文件的描述
	private String filedesc;

	// 待上传文件的大小
	private long filesize;

	// 已上传文件的大小
	private long filestart;

	// 本次上传的长度
	private long length;

	// 文件上传保存成功的文件ID
	private String fileid;

	private String waitconext;

	// 提示对话框
	private CustomProgressDialog progressdialog;

	// 传输任务类
	private ConnectionTask connectiontask;

	private IUploadEventListener uploadeventlistener = null;

	// 处理是否成功
	private boolean taskdone;

	private CustomAlertDialog alertDialog;

	private boolean upLoadOk = false;
	// 传输事件监听
	private ITaskEventListener taskeventlistener = new ITaskEventListener() {

		@Override
		public void onTaskSuccess(XmlNode response) {
			if (response != null) {
				String result = response.getText("root.result");
				if (result != null && result.compareToIgnoreCase("ok") == 0) {

				} else {

				}
			}
		}

		@Override
		public void onTaskFailed(String message) {
			if (progressdialog != null) {
				progressdialog.cancel();
				progressdialog = null;
			}
			connectiontask = null;
			uploadeventlistener.onFailed(filetype, message);
			taskdone = true;
		}

	};

	public FileUploadTask(ISaleApplication isaleapplication, Context context,
			String filetype, String filename, String filedesc,
			IUploadEventListener uploadeventlistener) {
		this.isaleapplication = isaleapplication;
		this.context = context;
		this.filetype = filetype;
		this.filename = filename;
		this.filedesc = filedesc;
		this.uploadeventlistener = uploadeventlistener;
	}

	public FileUploadTask(ISaleApplication isaleapplication, Context context,
			String filetype, String filename, String filedesc,
			IUploadEventListener uploadeventlistener, String waitconext) {
		this.isaleapplication = isaleapplication;
		this.context = context;
		this.filetype = filetype;
		this.filename = filename;
		this.filedesc = filedesc;
		this.uploadeventlistener = uploadeventlistener;
		this.waitconext = waitconext;
	}

	@Override
	protected String doInBackground(Void... arg0) {
		while (!taskdone) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
		return fileid;
	}

	@Override
	protected void onPreExecute() {
		filesize = FileUtil.getFileSize(filename);
		filestart = 0;
		length = 0;
		taskdone = false;
		fileid = "";
		this.progressdialog = new CustomProgressDialog(this.context,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!upLoadOk) {
							cancelTask();
							connectiontask = null;
							uploadeventlistener.onFailed("cancel", "文件上传已取消");
							taskdone = true;
						}

					}
				}, true);
		this.progressdialog.show();
		this.progressdialog.setMessage(waitconext == null ? "正在上传文件,请稍等..."
				: waitconext);
		send();
	}

	private void send() {
		if (this.isaleapplication != null) {
			try {
				length = FileUtil.FILECONTENT_MAXLENGTH;
				if (filestart + length >= filesize) {
					length = filesize - filestart;
				}
				Log.i("info", "length:" + length);
				String filecontent = FileUtil.readFileAndEncode(filename,
						filestart, length);
//				String srequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
//				srequest += "<root>";
//				srequest += "<functionno>" + Constant.FILE_UPLOAD
//						+ "</functionno>";
//				srequest += "<userid>"
//						+
//						+ "</userid>";
//				srequest += "<filename>" +
//						+ "</filename>";
//				srequest += "<filedesc>" + filedesc + "</filedesc>";
//				srequest += "<filesize>" + filesize + "</filesize>";
//				srequest += "<startpos>" + filestart + "</startpos>";
//				srequest += "<length>" + length + "</length>";
//				srequest += "<filecontent>" + filecontent + "</filecontent>";
//				srequest += "</root>";
//				XmlNode requestxml = XmlParser.parserXML(srequest, "UTF-8");
//				connectiontask = new ConnectionTask(this.isaleapplication,
//						this.context, taskeventlistener, requestxml, false);
//				connectiontask.execute(30,0);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("userid", this.isaleapplication.getConfig().getUserid());
                params.put("filename", FileUtil.getShortFileName(filename));
                params.put("filedesc", filedesc);
                params.put("filesize", filesize+"");
                params.put("startpos", filestart+"");
                params.put("endpos", (filestart+length)+"");
                params.put("length", length+"");
                params.put("sourceid", "");
                params.put("fileid", "");
                params.put("filecontent", filecontent);
                request("file!upload?code=" + Constant.FILE_UPLOAD, params, BaseActivity.FLAG_SHOW_PROGRESS | BaseActivity.FLAG_SHOW_ERROR | BaseActivity.FLAG_CANCEL);
			} catch (Exception e) {
				LogUtil.e("error", e.toString());
			}
		}
	}

	@Override
	protected void onPostExecute(String result) {
		if (upLoadOk) {
			uploadeventlistener.onFinish(filetype, result);
		}
		if (this.progressdialog != null) {
			this.progressdialog.cancel();
			this.progressdialog = null;
		}
	}

	public void cancelTask() {
		if (connectiontask != null) {
			connectiontask.cancelTask();
			connectiontask = null;
		}
		this.cancel(true);
		if (this.progressdialog != null) {
			this.progressdialog.cancel();
			this.progressdialog = null;
		}
	}





    /**
     * 请求JSON数据
     *
     * @param url    请求接口路径
     * @param params 请求参数
     * @param flags  FLAG_DEFAULT,FLAG_SHOW_PROGRESS,FLAG_SHOW_ERROR,FLAG_CANCEL
     */
    public <T> void request(String url, T params, int flags) {
        boolean isShowProgress = (flags & BaseActivity.FLAG_SHOW_PROGRESS) != 0;
        boolean isShowError = (flags & BaseActivity.FLAG_SHOW_ERROR) != 0;
        boolean cancelable = (flags & BaseActivity.FLAG_CANCEL) != 0;
        boolean isEncrypt = (flags & BaseActivity.FLAG_ENCRYPT) != 0;

        request(url, params, isShowProgress, isShowError, cancelable, isEncrypt);
    }

    /**
     * 请求JSON数据
     *
     * @param url            请求接口路径
     * @param params         请求参数
     * @param isShowProgress 是否显示加载进度框
     * @param isShowError    是否显示错误提示
     * @param cancelable     是否当关闭进度框时取消相应请求
     * @param isEncrypt      是否加密请求
     */
    private <T> void request(String url, final T params, boolean isShowProgress, final boolean isShowError, boolean cancelable, final boolean isEncrypt) {
        final String sParams = params != null ? new Gson().toJson(params) : "";
        final String finalUrl = url.startsWith("http") ? url : Constant.API_URL_PREFIX + url+"&apitoken="+(ISaleApplication.getInstance().getConfig().getApitoken()==null?"":ISaleApplication.getInstance().getConfig().getApitoken());
        LogUtil.i("volley-request", finalUrl + "[" + sParams + "]");
        final GsonRequest<JsonObject> request = new GsonRequest<JsonObject>(Request.Method.POST, finalUrl, JsonObject.class, new Response.Listener<JsonObject>() {
            @Override
            public void onResponse(JsonObject response) {
                LogUtil.i("volley-response", finalUrl + "[" + response.toString() + "]");
//                if (progressdialog != null && progressdialog.isShowing())
//                    progressdialog.dismiss();
                try {
                    if (response.get("result").getAsInt() == 0) {
                        JsonObject body = response.getAsJsonObject("body");
                        filestart = filestart + length;
                        if (filestart == filesize) {
                            connectiontask = null;
                            fileid =body.get("fileid").getAsString();
                            taskdone = true;
                            upLoadOk = true;
                        } else {
                            connectiontask = null;
                            send();
                        }
                    } else if(response.get("result").getAsInt() == 2){
                        Toast.makeText(context, "身份认证已过期，请重新登录", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(context,LoginActivity.class);
                        context.startActivity(intent);
                        ISaleApplication.getInstance().dropTask();
                    }else if(response.get("result").getAsInt() == 3){
                        Toast.makeText(context,"身份认证不存在，请重新登录",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(context,LoginActivity.class);
                        context.startActivity(intent);
                        ISaleApplication.getInstance().dropTask();
                    }else if (isShowError) {
                        String code = response.get("code").getAsString();
                        String message = response.get("message").getAsString();
                        LogUtil.e("error", "onTaskSuccess ! OK");
                        if (progressdialog != null) {
                            progressdialog.cancel();
                            progressdialog = null;
                        }
                        connectiontask = null;
                        uploadeventlistener.onFailed(filetype, message);
                        taskdone = true;
                    }
                } catch (Exception e) {
                    if (isShowError) {
//                        showAlertDialog("提示", "数据解析失败",
//                                new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View arg0) {
//                                        alertDialog.cancel();
//                                    }
//                                }, "确定", null, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (progressdialog != null) {
                    progressdialog.cancel();
                    progressdialog = null;
                }
                connectiontask = null;
                uploadeventlistener.onFailed(filetype, volleyError.getMessage());
                taskdone = true;
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if (isEncrypt) {
                    // 不指定为json，防止appserver误解析
                    headers.put("Content-Type", "text/html; charset=UTF-8");
                }
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                if (params != null) {
                    if (isEncrypt) {
//                        LogUtil.i("TEST", Charset.defaultCharset().displayName());
                        return DESedeUtilsForServer.encrypt(sParams).getBytes();
                    } else {
                        return sParams.getBytes();
                    }
                }

                return super.getBody();
            }
        };

        if (isShowProgress) {
//            this.progressdialog = new CustomProgressDialog(this,
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            request.cancel();
//                        }
//                    }, cancelable);
//            this.progressdialog.show();
        }

        RequestManager.addRequest(request);
    }

}
