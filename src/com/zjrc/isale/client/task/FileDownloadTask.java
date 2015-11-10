package com.zjrc.isale.client.task;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣,贺彬
 * 功能描述：文件下载任务类
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
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
import com.zjrc.isale.client.volley.GsonRequest;
import com.zjrc.isale.client.volley.RequestManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileDownloadTask extends AsyncTask<Void, Void, String> {
	public static final String TAG = "FileDownloadTask";

	// 应用程序
	private ISaleApplication isaleapplication = null;

	// 上下文
	private Context context = null;

	// 待下载文件的类型
	private String filetype;

	// 待下载文件的路径
	private String filename;

	// 待下载文件的大小
	private long filesize;

	// 已下载文件的大小
	private long filestart;

	// 本次下载的长度
	private long length;

	// 待下载的文件ID
	private String fileid;

	// 提示对话框
	private CustomProgressDialog progressdialog;

	// 传输任务类
	private ConnectionTask connectiontask;

	private IDownloadEventListener downloadeventlistener = null;

	// 处理是否成功
	private boolean taskdone;

	private boolean isShowDialog = true;

	private CustomAlertDialog alertDialog;
	
	private Activity activity;


	public FileDownloadTask(ISaleApplication isaleapplication, Context context,
			String filetype, String fileid,
			IDownloadEventListener downloadeventlistener, boolean isShowDialog) {
		this.isaleapplication = isaleapplication;
		this.context = context;
		this.filetype = filetype;
		this.fileid = fileid;
		this.downloadeventlistener = downloadeventlistener;
		this.isShowDialog = isShowDialog;
		this.activity =(Activity)context;
	}

	@Override
	protected String doInBackground(Void... arg0) {
		while (!taskdone) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		return fileid;
	}

	@Override
	protected void onPreExecute() {
		filesize = 0;
		filename = "";
		filestart = 0;
		length = 0;
		taskdone = false;
		if (isShowDialog) {
			this.progressdialog = new CustomProgressDialog(this.context,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							cancelTask();
						}
					});
			this.progressdialog.show();
			this.progressdialog.setMessage("正在下载文件,请稍等...");
		}
		send();
	}

	private void send() {
		if (this.isaleapplication != null) {
			try {
//				String srequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
//				srequest += "<root>";
//				srequest += "<functionno>" + Constant.FILE_DOWNLOAD
//						+ "</functionno>";
//				srequest += "<fileid>" + fileid + "</fileid>";
//				srequest += "<startpos>" + filestart + "</startpos>";
//				srequest += "<length>" + length + "</length>";
//				srequest += "</root>";
//				XmlNode requestxml = XmlParser.parserXML(srequest, "UTF-8");
//				connectiontask = new ConnectionTask(this.isaleapplication,
//						this.context, taskeventlistener, requestxml, false);
//				connectiontask.execute(30,0);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fileid", fileid);
                params.put("startpos", filestart+"");
                params.put("length", length+"");
                request("file!download?code=" + Constant.FILE_DOWNLOAD, params, BaseActivity.FLAG_SHOW_PROGRESS | BaseActivity.FLAG_SHOW_ERROR | BaseActivity.FLAG_CANCEL);
            } catch (Exception e) {
				LogUtil.e(TAG, e.toString());
			}
		}

	}

	@Override
	protected void onPostExecute(String result) {
		downloadeventlistener.onFinish(filetype, filename);
		if (isShowDialog && (this.progressdialog != null)) {
			this.progressdialog.cancel();
			this.progressdialog = null;
		}
	}

	public void cancelTask() {
		if (connectiontask != null) {
			connectiontask.cancelTask();
			connectiontask = null;
		}
		if (!taskdone) {
			File file = new File(filename);
			if (file.exists()) {
				file.delete();
			}
		}
		this.cancel(true);
		if (isShowDialog && (this.progressdialog != null)) {
			this.progressdialog.cancel();
			this.progressdialog = null;
		}
	}

    public void setDownloadeventlistener(IDownloadEventListener listener) {
        downloadeventlistener = listener;
    }

    public IDownloadEventListener getDownloadeventlistener() {
        return downloadeventlistener;
    }

    public String getFileId() {
        return fileid;
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
                if (progressdialog != null && progressdialog.isShowing())
                    progressdialog.dismiss();
                try {
                    if (response.get("result").getAsInt() == 0) {
                        String filepath = Environment.getExternalStorageDirectory()
                                .getPath()
                                + File.separator
                                + "isale"
                                + File.separator + "download";
                        JsonObject body = response.getAsJsonObject("body");
                        String fileshortname = body.get("filename").getAsString();
                        filename = filepath + File.separator + fileshortname;
                        filesize = Long.parseLong(body.get("filesize").getAsString());
                        filestart = Long.parseLong(body.get("startpos").getAsString());
                        length = Long
                                .parseLong(body.get("length").getAsString());
                        String filecontent =body.get("filecontent").getAsString();
                        try {
                            FileUtil.decodeAndWriteFile(filename, filecontent,
                                    filestart, length);
                        } catch (Exception e) {

                        }
                        filestart = filestart + length;
                        if (filestart == filesize) {
                            connectiontask = null;
                            taskdone = true;
                        } else {
                            connectiontask = null;
                            // length = FileUtil.FILECONTENT_MAXLENGTH;
                            if (filestart + length >= filesize) {
                                length = filesize - filestart;
                            }
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
                    } else if (isShowError) {
                        String code = response.get("code").getAsString();
                        String message = response.get("message").getAsString();
//
//                        showAlertDialog("提示", message,
//                                new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View arg0) {
//                                        alertDialog.cancel();
//                                    }
//                                }, "确定", null, null);
                        if (isShowDialog && (progressdialog != null)) {
                            progressdialog.cancel();
                            progressdialog = null;
                        }
                        connectiontask = null;
                        downloadeventlistener.onFail(filetype, message);
                        ISaleApplication application = ISaleApplication.getInstance();
                        if (application != null) {
                            if (application.checkActivity(activity)) {
                                alertDialog = new CustomAlertDialog(context);
                                alertDialog.show();
                                alertDialog.setTitle("提示");
                                alertDialog.setMessage(message);
                                alertDialog.getBtn_positive().setText("重试");
                                alertDialog.getBtn_negative().setText("取消");
                                alertDialog
                                        .setOnNegativeListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                taskdone = true;
                                                alertDialog.cancel();
                                            }
                                        });
                                alertDialog
                                        .setOnPositiveListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                if (isShowDialog) {
                                                    progressdialog = new CustomProgressDialog(
                                                            context,
                                                            new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    cancelTask();
                                                                }
                                                            });
                                                    progressdialog.show();
                                                    progressdialog
                                                            .setMessage("正在下载文件,请稍等...");
                                                    send();
                                                    alertDialog.cancel();
                                                }
                                            }
                                        });
                            }
                        }
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
                if (isShowDialog && (progressdialog != null)) {
                    progressdialog.cancel();
                    progressdialog = null;
                }
                connectiontask = null;
                downloadeventlistener.onFail(filetype, "下载失败");
                ISaleApplication application = ISaleApplication.getInstance();
                if (application != null) {
                    if (application.checkActivity(activity)) {
                        alertDialog = new CustomAlertDialog(context);
                        alertDialog.show();
                        alertDialog.setTitle("提示");
                        alertDialog.setMessage("下载失败");
                        alertDialog.getBtn_positive().setText("重试");
                        alertDialog.getBtn_negative().setText("取消");
                        alertDialog.setOnNegativeListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                taskdone = true;
                                alertDialog.cancel();
                            }
                        });
                        alertDialog.setOnPositiveListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if (isShowDialog) {
                                    progressdialog = new CustomProgressDialog(context,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    cancelTask();
                                                }
                                            });
                                    progressdialog.show();
                                    progressdialog.setMessage("正在下载文件,请稍等...");
                                    send();
                                    alertDialog.cancel();
                                }
                            }
                        });


                    }
                }

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
