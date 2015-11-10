package com.zjrc.isale.client.ui.widgets.menudrawer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Config;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.activity.LoginActivity;
import com.zjrc.isale.client.ui.adapter.MenuAdapter;
import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;
import com.zjrc.isale.client.ui.widgets.CustomProgressDialog;
import com.zjrc.isale.client.util.DESedeUtilsForServer;
import com.zjrc.isale.client.util.LogUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.volley.GsonRequest;
import com.zjrc.isale.client.volley.RequestManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseMenuActivity extends FragmentActivity implements
		MenuAdapter.MenuListener {
	private static final String STATE_ACTIVE_POSITION = "com.zjrc.isale.client.BaseMenuActivity.activePosition";
	protected MenuDrawer mMenuDrawer;
	protected MenuAdapter mAdapter;
	protected ListView mList;
    // 加载loading框
    private CustomProgressDialog progressdialog;
    // 是否需要提示对话框
    private boolean needprogressdialog = true;
    // volley消息队列
    protected RequestQueue queue;
    //提示框
    protected  CustomAlertDialog alertDialog;
    private int mActivePosition = 1;

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		ISaleApplication application = ISaleApplication.getInstance();
		if (application != null) {
			application.addActivity(this);
			}
		
		if (inState != null) {
			mActivePosition = inState.getInt(STATE_ACTIVE_POSITION);
		}

		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY,
				getDrawerPosition(), getDragMode());

		List<Object> items = new ArrayList<Object>();

        boolean isAudit = false;
        if (application != null) {
            Config config = application.getConfig();
            isAudit = config.getNeedaudit() && config.getIsaudit();
            items.add(new User("我的信息",
    				application.getConfig().getUsername() == null ? ""
    						: application.getConfig().getUsername(), application
    						.getConfig().getPhoneno() == null ? "" : application
    						.getConfig().getPhoneno(), "www.www.ww"));
        }
		items.add(new Item("我的工作", R.drawable.v2_menu_work, -1,true));
		items.add(new Item("我的业务", R.drawable.v2_menu_business, -1,false));
		items.add(new Item("我的客户", R.drawable.v2_menu_customer, -1,false));
        if (isAudit) {
            items.add(new Item("我的审批", R.drawable.v2_menu_audit, 0,false));
        }
		items.add(new Item("通讯录", R.drawable.v2_menu_roster, -1,false));
		items.add(new Item("新闻公告", R.drawable.v2_menu_notice, -1,false));
		items.add(new Item("设置", R.drawable.v2_menu_setting, -1,false));

		mList = new ListView(this);
		mList.setDividerHeight(0);
		mList.setCacheColorHint(Color.WHITE);
		mAdapter = new MenuAdapter(this, items);
		mAdapter.setListener(this);
		mAdapter.setActivePosition(mActivePosition);
		mList.setAdapter(mAdapter);
		mList.setSelector(R.color.v2_press);
		mList.setOnItemClickListener(mItemClickListener);
		mMenuDrawer.setMenuView(mList);
		
        
	}

	protected abstract void onMenuItemClicked(int position, Object item);

	protected abstract int getDragMode();

	protected abstract Position getDrawerPosition();

	private ListView.OnItemClickListener mItemClickListener = new ListView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position != 0) {
				mActivePosition = position;
				mMenuDrawer.setActiveView(view, position);
				mAdapter.setActivePosition(position);
				onMenuItemClicked(position, mAdapter.getItem(position));
			}
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
	}

	@Override
	public void onActiveViewChanged(View v) {
		mMenuDrawer.setActiveView(v, mActivePosition);
	}
	
	    @Override
	    public void onDestroy() {
            ISaleApplication application = ISaleApplication.getInstance();
            if (application != null) {
                application.removeActivity(this);
            }
	        super.onDestroy();
	    }
    public void showAlertDialog(String title, String msg,
                                View.OnClickListener onPositiveListener, String positiveStr,
                                String negativeStr, View.OnClickListener onNegativeListener) {
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
    protected static final int FLAG_DEFAULT = 0;
    // 是否显示加载进度框
    protected static final int FLAG_SHOW_PROGRESS = 1;
    // 是否显示错误提示
    protected static final int FLAG_SHOW_ERROR = 2;
    // 是否当关闭进度框时取消相应请求
    protected static final int FLAG_CANCEL = 4;
    // 是否加密请求
    protected static final int FLAG_ENCRYPT = 8;
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
    protected <T> void request(String url, T params, int flags) {
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
                        Toast.makeText(BaseMenuActivity.this, "身份认证已过期，请重新登录", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(BaseMenuActivity.this,LoginActivity.class);
                        startActivity(intent);
                        ISaleApplication.getInstance().dropTask();
                    }else if(response.get("result").getAsInt() == 3){
                        Toast.makeText(BaseMenuActivity.this,"身份认证不存在，请重新登录",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(BaseMenuActivity.this,LoginActivity.class);
                        startActivity(intent);
                        ISaleApplication.getInstance().dropTask();
                    } else if (isShowError) {
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
