package com.zjrc.isale.client.task;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：任务事件监听类
 */

import com.zjrc.isale.client.util.xml.XmlNode;

public interface ITaskEventListener {
	/**
	 * 请求处理成功消息 
	 * @param response
	 */
	public void onTaskSuccess(XmlNode response);
	
	/**
	 * 请求处理失败消息
	 * @param message
	 */
	public void onTaskFailed(String message);

}
