package com.zjrc.isale.client.task;

public interface IUploadEventListener {
	public void onFinish(String filetype, String fileid);

	public void onFailed(String filetype, String message);

}
