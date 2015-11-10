package com.zjrc.isale.client.util;

import android.os.Handler;
import android.os.HandlerThread;

import com.zjrc.isale.client.task.FileDownloadTask;
import com.zjrc.isale.client.task.IDownloadEventListener;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：串行下载文件
 */
public class SerialFileDownloader {
    private static SerialFileDownloader instance;

    // 待下载文件队列
    private Queue<FileDownloadTask> queueTask;
    // 待下载文件ID队列
    private Queue<String> queueFileId;
    // 当前正在下载的文件
    private FileDownloadTask mTask;

    private Handler handler;

    private boolean running = false;

    private SerialFileDownloader() {
        queueTask = new LinkedList<FileDownloadTask>();
        queueFileId = new LinkedList<String>();

        HandlerThread thread = new HandlerThread("SerialFileDownloader");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    public static SerialFileDownloader getInstance() {
        if (instance == null) {
            instance = new SerialFileDownloader();
        }

        return instance;
    }

    public void addTask(FileDownloadTask task) {
        // 不在队列中才入列
        if (!queueFileId.contains(task.getFileId())) {
            queueFileId.add(task.getFileId());
            queueTask.add(task);
        }
    }

    public void execute() {
        if (!running && !queueTask.isEmpty()) {
            running = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    while (!queueTask.isEmpty()) {
                        if (mTask == null) {
                            // 执行下一个下载任务
                            mTask = queueTask.poll();
                            // 重写任务的下载事件监听
                            final IDownloadEventListener origListener = mTask.getDownloadeventlistener();
                            mTask.setDownloadeventlistener(new IDownloadEventListener() {
                                @Override
                                public void onFinish(String filetype, String filename) {
                                    origListener.onFinish(filetype, filename);
                                    mTask = null;
                                    queueFileId.poll();
                                }

                                @Override
                                public void onFail(String filetype, String message) {
                                    origListener.onFail(filetype, message);
                                    mTask = null;
                                    queueFileId.poll();
                                }
                            });
                            mTask.execute();
                        }

                        try {
                            // 休息500毫秒
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    running = false;
                }
            });
        }
    }
}
