package com.zjrc.isale.client.bean;

import java.io.Serializable;

public class Audit implements Serializable {
	private String userid;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	private String username;

}
