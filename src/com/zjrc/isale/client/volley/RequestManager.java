package com.zjrc.isale.client.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.content.Context;


/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：volley请求管理者
 */
public class RequestManager {

    /**
     * 请求队列，单例
     */
    private static RequestQueue mRequestQueue;


    private RequestManager() {
    }

    /**
     * @param context application 上下文
     */
    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * @return 请求队列
     * @throws IllegalStatException 若队列未初始化抛出异常
     */
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }

    /**
     * 增加请求
     * @param request 请求
     */
    public static void addRequest(Request request) {
        if (mRequestQueue != null) {
            mRequestQueue.add(request);
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }
}
