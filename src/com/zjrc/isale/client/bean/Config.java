package com.zjrc.isale.client.bean;

import com.zjrc.isale.client.service.LocationTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Config implements Serializable {
	// 服务器地址
	private String serverip;

	// 服务器端口
	private int serverport;

	// 电话号码
	private String phoneno;

	// 密码
	private String password;

	// 是否保存密码
	private Boolean savepassword;

	// 是否自动登录
	private Boolean autologin;

	// 企业ID
	private String companyid;

	// 用户ID
	private String userid;

	// 用户名称
	private String username;
    //身份认证
    private String apitoken;

	// 客户端版本号

    public String getApitoken() {
        return apitoken;
    }

    public void setApitoken(String apitoken) {
        this.apitoken = apitoken;
    }

    private String clientversion;

	// 客户端下载地址
	private String clientdownloadurl;

	// 定位任务个数
	private int taskcount;

	// 定位任务数据
	private List<LocationTask> tasks;

	// 是否需要审批功能
	private String needaudit;
	
	// 审批权限
	private String isaudit;
	
	//是否曾登陆
	private boolean firstLogin;
    //用户所属区域
    private String areas;
    //审核人员
    private String audits;

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public String getAreas() {
        return areas;
    }

    public void setAreas(String areas) {
        this.areas = areas;
    }

    public String getAudits() {
        return audits;
    }

    public void setAudits(String audits) {
        this.audits = audits;
    }

    public String getTerminalTypes() {
        return terminalTypes;
    }

    public void setTerminalTypes(String terminalTypes) {
        this.terminalTypes = terminalTypes;
    }

    //网点类型
    private String terminalTypes;
	public boolean getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	/**
	 * 构造函数
	 */
	public Config() {
		serverip = "";
		serverport = 0;
		phoneno = "";
		password = "";
		taskcount = 0;
		tasks = new ArrayList<LocationTask>();
	}

	public Config(String content) {
		serverip = "";
		serverport = 0;
		phoneno = "";
		password = "";
		taskcount = 0;
		tasks = new ArrayList<LocationTask>();
		if (content != null) {
			String[] sContents = content.split("|");
			if (sContents != null) {
				if (sContents.length > 1) {
					String[] sParams = sContents[1].split(",");
					if (sParams != null) {
						if (sParams.length > 2) {
							serverip = sParams[0];
							try {
								serverport = Integer.parseInt(sParams[1]);
							} catch (Exception e) {
								serverport = 9002;
							}
							phoneno = sParams[2];
						}
					}
				}
			}
		}
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	public String getServerip() {
		return serverip;
	}

	public int getServerport() {
		return serverport;
	}

	public void setServerport(int serverport) {
		this.serverport = serverport;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public Boolean getSavepassword() {
		return savepassword;
	}

	public void setSavepassword(Boolean savepassword) {
		this.savepassword = savepassword;
	}

	public Boolean getAutologin() {
		return autologin;
	}

	public void setAutologin(Boolean autologin) {
		this.autologin = autologin;
	}

	public void setCompanyid(String companyid) {
		this.companyid = companyid;
	}

	public String getCompanyid() {
		return companyid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUserid() {
		return userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getClientversion() {
		return clientversion;
	}

	public void setClientversion(String clientversion) {
		this.clientversion = clientversion;
	}

	public String getClientdownloadurl() {
		return clientdownloadurl;
	}

	public void setClientdownloadurl(String clientdownloadurl) {
		this.clientdownloadurl = clientdownloadurl;
	}

	public void setTaskcount(int taskcount) {
		this.taskcount = taskcount;
	}

	public int getTaskcount() {
		return taskcount;
	}

	public void setNeedaudit(String taskcount) {
		this.needaudit = taskcount;
	}

	public boolean getNeedaudit() {
		if (needaudit != null && needaudit.compareTo("1") == 0) {
			return true;
		}
		return false;
	}

	public void setTasks(List<LocationTask> tasks) {
		this.tasks = tasks;
	}

	public List<LocationTask> getTasks() {
		return tasks;
	}

	public void setIsaudit(String taskcount) {
		this.isaudit = taskcount;
	}

	public boolean getIsaudit() {
		if (isaudit != null && isaudit.compareTo("1") == 0) {
			return true;
		}
		return false;
	}

}
