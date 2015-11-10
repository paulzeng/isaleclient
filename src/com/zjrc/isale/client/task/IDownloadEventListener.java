package com.zjrc.isale.client.task;

public interface IDownloadEventListener {
	public void onFinish(String filetype, String filename);

	public void onFail(String filetype,String message);
}
