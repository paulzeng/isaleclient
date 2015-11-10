package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.umeng.analytics.MobclickAgent;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Config;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;
import com.zjrc.isale.client.util.DESedeUtilsForServer;
import com.zjrc.isale.client.util.LogUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.volley.GsonRequest;
import com.zjrc.isale.client.volley.RequestManager;

import java.util.Map;


/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者 高林荣
 * @创建者: 贺彬
 * @功能描述：基础Activity
 */
public abstract class BaseActivity extends Activity {
	protected  CustomAlertDialog alertDialog;
    // 加载loading框
    private CustomProgressDialog progressdialog;
    // 是否需要提示对话框
    private boolean needprogressdialog = true;
    // volley消息队列
    protected RequestQueue queue;
	public boolean needAudit = false;

	public void setEditText(Activity at, int iLayout, String sText) {
		EditText et = (EditText) at.findViewById(iLayout);
		if (et != null) {
			et.setText(sText);
		}
	}


	public void showAlertDialog(String title, String msg,
			OnClickListener onPositiveListener,
			OnClickListener onNegativeListener) {
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null) {
			if (application.checkActivity(this)) {
				if (alertDialog != null) {
					alertDialog.dismiss();
				}
				alertDialog = new CustomAlertDialog(this);
				alertDialog.show();
				alertDialog.setTitle(title);
				alertDialog.setMessage(msg);
				if (onPositiveListener != null) {
					alertDialog.setOnPositiveListener(onPositiveListener);
				} else {
					alertDialog.getBtn_positive().setVisibility(View.GONE);
				}
				if (onNegativeListener != null) {
					alertDialog.setOnNegativeListener(onNegativeListener);
				} else {
					alertDialog.getBtn_negative().setVisibility(View.GONE);
				}
			}
		}
	}

	public void showAlertDialog(String title, String msg,
			OnClickListener onPositiveListener, String positiveStr,
			String negativeStr, OnClickListener onNegativeListener) {
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null) {
			if (application.checkActivity(this)) {
				if (alertDialog != null) {
					alertDialog.dismiss();
				}
				alertDialog = new CustomAlertDialog(this);
				alertDialog.show();
				alertDialog.setTitle(title);
				alertDialog.setMessage(msg);
				if (onPositiveListener != null) {
					alertDialog.setOnPositiveListener(onPositiveListener);
					if (positiveStr != null)
						alertDialog.getBtn_positive().setText(positiveStr);
				} else {
					alertDialog.getBtn_positive().setVisibility(View.GONE);
				}
				if (onNegativeListener != null) {
					alertDialog.setOnNegativeListener(onNegativeListener);
					if (negativeStr != null)
						alertDialog.getBtn_negative().setText(negativeStr);
				} else {
					alertDialog.getBtn_negative().setVisibility(View.GONE);
				}
				}
			}
	}

	public void showAlertDialogNormal(String title, String msg) {
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null) {
			if (application.checkActivity(this)) {
				if (alertDialog != null) {
					alertDialog.dismiss();
				}
				alertDialog = new CustomAlertDialog(this);
				alertDialog.show();
				alertDialog.setTitle(title);
				alertDialog.setMessage(msg);
				alertDialog.setOnPositiveListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						alertDialog.dismiss();
					}
				});
				alertDialog.getBtn_negative().setVisibility(View.GONE);
			}
		}
	}

	public void showAlertDialogNormal(String title, String msg, boolean isNew) {
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null) {
			if (application.checkActivity(this)) {
				if (alertDialog == null
						|| (alertDialog != null && !alertDialog.isShowing())) {
					if (isNew) {
						final CustomAlertDialog alertDialog = new CustomAlertDialog(
								this);
						alertDialog.show();
						alertDialog.setTitle(title);
						alertDialog.setMessage(msg);
						alertDialog.setOnPositiveListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								alertDialog.dismiss();
							}
						});
						alertDialog.getBtn_negative().setVisibility(View.GONE);
					}
				}
				}
			}
	}

	@Override
 	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null) {
			Config config = application.getConfig();
            queue= RequestManager.getRequestQueue();
			needAudit = config.getNeedaudit();
			application.addActivity(this);
		}
	}

	@Override
	protected void onDestroy() {
        if (queue != null) {
            queue.cancelAll(this);// 页面关闭时取消所有volley队列中未完成的请求
        }
		if (alertDialog!=null&&alertDialog.isShowing()) {
			alertDialog.dismiss();
			alertDialog=null;
		}
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null) {
			application.removeActivity(this);
		}
		super.onDestroy();
	}

	public boolean isEmpty(String... args) {
		for (String arg : args) {
			if (!TextUtils.isEmpty(arg)) {
				return false;
			}
		}

		return true;
	}

	/*
	 * 友盟统计
	 */
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName()); // 统计页面
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(this);
	}

    /**
     * 网络请求返回处理方法<xml>old
     *
     * @param response
     */
    public abstract void onRecvData(XmlNode response);

    /**
     * 网络请求返回处理方法<json>
     *
     * @param response
     */
    public abstract void onRecvData(JsonObject response);

    // 默认不显示加载进度框，错误提示，不能取消请求
    public static final int FLAG_DEFAULT = 0;
    // 是否显示加载进度框
    public static final int FLAG_SHOW_PROGRESS = 1;
    // 是否显示错误提示
    public static final int FLAG_SHOW_ERROR = 2;
    // 是否当关闭进度框时取消相应请求
    public static final int FLAG_CANCEL = 4;
    // 是否加密请求
    public static final int FLAG_ENCRYPT = 8;
    /**
     * 请求JSON数据
     *
     * @param url    请求接口路径
     * @param params 请求参数
     */
    protected <T> void request(String url, T params) {
        request(url, params, FLAG_DEFAULT);
    }

    /**
     * 请求JSON数据
     *
     * @param url    请求接口路径
     * @param params 请求参数
     * @param flags  FLAG_DEFAULT,FLAG_SHOW_PROGRESS,FLAG_SHOW_ERROR,FLAG_CANCEL
     */
    public <T> void request(String url, T params, int flags) {
        boolean isShowProgress = (flags & FLAG_SHOW_PROGRESS) != 0;
        boolean isShowError = (flags & FLAG_SHOW_ERROR) != 0;
        boolean cancelable = (flags & FLAG_CANCEL) != 0;
        boolean isEncrypt = (flags & FLAG_ENCRYPT) != 0;

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
        final String finalUrl = url.startsWith("http") ? url : Constant.API_URL_PREFIX + url+"&apitoken="+(((ISaleApplication)getApplication()).getConfig().getApitoken()==null?"":((ISaleApplication)getApplication()).getConfig().getApitoken());
        LogUtil.i("volley-request", finalUrl + "[" + sParams + "]");
        final GsonRequest<JsonObject> request = new GsonRequest<JsonObject>(Request.Method.POST, finalUrl, JsonObject.class, new Response.Listener<JsonObject>() {
            @Override
            public void onResponse(JsonObject response) {
                LogUtil.i("volley-response", finalUrl + "[" + response.toString() + "]");
                if (progressdialog != null && progressdialog.isShowing())
                    progressdialog.dismiss();
                try {
                    if (response.get("result").getAsInt() == 0) {
                        onRecvData(response);
                    } else if(response.get("result").getAsInt() == 2){
                        Toast.makeText(BaseActivity.this,"身份认证已过期，请重新登录",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(BaseActivity.this,LoginActivity.class);
                        startActivity(intent);
                        ISaleApplication.getInstance().dropTask();
                    }else if(response.get("result").getAsInt() == 3){
                        Toast.makeText(BaseActivity.this,"身份认证不存在，请重新登录",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(BaseActivity.this,LoginActivity.class);
                        startActivity(intent);
                        ISaleApplication.getInstance().dropTask();
                    }else if (isShowError) {
                        String code = response.get("code").getAsString();
                        String message = response.get("message").getAsString();
                        showAlertDialog("提示", message,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        alertDialog.cancel();
                                    }
                                }, "确定", null, null);
                    }
                } catch (Exception e) {
                    if (isShowError) {
                        showAlertDialog("提示", "数据解析失败",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        alertDialog.cancel();
                                    }
                                }, "确定", null, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.i("info",volleyError.toString());
                if (progressdialog != null && progressdialog.isShowing())
                    progressdialog.dismiss();

                if (!isFinishing()) {
                    if (isShowError) {
                        showAlertDialog("提示", "数据请求失败",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        alertDialog.cancel();
                                    }
                                }, "确定", null, null);
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
            this.progressdialog = new CustomProgressDialog(this,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            request.cancel();
                        }
                    }, cancelable);
            this.progressdialog.show();
        }

        RequestManager.addRequest(request);
    }
}
