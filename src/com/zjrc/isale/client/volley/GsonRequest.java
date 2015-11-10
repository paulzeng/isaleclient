package com.zjrc.isale.client.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：gson请求，通过gson将volley返回值直接转为java对象
 */
public class GsonRequest<T> extends Request<T> {

	/**
	 * Gson
	 */
	private final Gson mGson;

	/**
	 * 请求返回java对象类型
	 */
	private final Class<T> mClass;

	/**
	 * 请求成功回调
	 */
	private final Listener<T> mListener;

	/**
	 * 请求错误回调
	 */
	private final ErrorListener mErrorListener;

	public GsonRequest(int method, String url, Class<T> objectClass,
			Listener<T> listener,
			ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mClass = objectClass;
		this.mListener = listener;
		this.mErrorListener = errorListener;
		this.mGson = new Gson();
	}

	/* 
	 * 解析http返回
	 * @see com.android.volley.Request#parseNetworkResponse(com.android.volley.NetworkResponse)
	 */
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
            LogUtil.i("http",json);
			return Response.success(mGson.fromJson(json, mClass),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}

	}

	/* 响应成功回调
	 * @see com.android.volley.Request#deliverResponse(java.lang.Object)
	 */
	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	/* 响应错误回调
	 * @see com.android.volley.Request#deliverError(com.android.volley.VolleyError)
	 */
	@Override
	public void deliverError(VolleyError error) {
		mErrorListener.onErrorResponse(error);
	}

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("User-Agent", Constant.USER_AGENT);
        return headers;
    }
}
