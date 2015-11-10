package com.zjrc.isale.client.service;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：定位请求任务类
 */

import java.io.Serializable;

import android.util.Log;

@SuppressWarnings("serial")
public class LocationTask implements Serializable {
	private static String TAG="LocationTask";
	
	//请求顺序号
	private int token = 0; 
	
	//请求流水号
	private String serialno="";
	
	//定位操作
	//0:停止定时定位
	//1:单次定位
	//2:启动定时定位
	private int operatetype = 0;
	
	//定位次数
	private int locationtimes = 0;
	
	//定位间隔
	private int interaltime = 0;
	
	//已上报次数
	private int submitcount = 0;
	
	/**
	 * 构造函数
	 */
	public LocationTask(){
		token = 0;
		serialno="";
		operatetype=0;
		locationtimes = 0;
		interaltime=15;
		submitcount = 0;		
	}
	
	public LocationTask(int token,String content){
		this.token = 9998-token;
		serialno="";
		operatetype=0;
		locationtimes = 0;
		interaltime=15;
		submitcount = 0;
		if (content!=null && content.length()>0){
			String[] sContents = content.split(",");
			Log.i(TAG,"sContents[0]:"+sContents[0]+",sContents[1]:"+sContents[1]+",sContents[2]:"+sContents[2]+",sContents[3]:"+sContents[3]);
			if (sContents!=null && sContents.length>3){
				serialno = sContents[0];
				try{
					operatetype = Integer.parseInt(sContents[1]);
				}catch(Exception e){
					operatetype = 0;
				}
				try{
					locationtimes = Integer.parseInt(sContents[2]);
				}catch(Exception e){
					locationtimes = 0;
				}	
				try{
					interaltime = Integer.parseInt(sContents[3]);
				}catch(Exception e){
					interaltime = 0;
				}					
			}
		}
	}
	
	public int getToken() {
		return token;
	}

	public void setToken(int token) {
		this.token = token;
	}	
	
	/**
	 * 获取定位流水号
	 * @return 
	 */
	public String getSerialno(){
		return this.serialno;
	}
	
	/**
	 * 设置定位流水号
	 * @param value
	 */
	public void setSerialno(String value){
		this.serialno = value;
	}
	
	/**
	 * 获取定位操作类型
	 * @return
	 */
	public int getOperatetype(){
		return this.operatetype;
	}
	
	/**
	 * 设置定位操作类型
	 * @param value
	 */
	public void setOperatetype(int value){
		this.operatetype = value;
	}
	
	/**
	 * 获取定位时间间隔
	 * @return
	 */
	public int getInteraltime(){
		return this.interaltime;
	}
	
	/**
	 * 设置定位间隔时间
	 * @param value
	 */
	public void setInteraltime(int value){
		this.interaltime = value;
	}

	public void setLocationtimes(int locationtimes) {
		this.locationtimes = locationtimes;
	}

	public int getLocationtimes() {
		return locationtimes;
	}

	public void setSubmitcount(int submitcount) {
		this.submitcount = submitcount;
	}

	public int getSubmitcount() {
		return submitcount;
	}
}
