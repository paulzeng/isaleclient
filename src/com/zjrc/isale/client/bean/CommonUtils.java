package com.zjrc.isale.client.bean;

/**
 * 项目名称：销售管家 
 * 版本号：V1.20 
 * 创建者: 刘磊 
 * 功能描述：防止重复点击
 */

public class CommonUtils {
	private static long lastClickTime;

	public static boolean isNotFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return false;
		}
		lastClickTime = time;
		return true;
	}
}
