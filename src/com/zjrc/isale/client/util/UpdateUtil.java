package com.zjrc.isale.client.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.task.IUpdateEventListener;
import com.zjrc.isale.client.task.UpdateTask;
import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;

import java.io.File;

public class UpdateUtil {
    private String apkfilename;

    private IUpdateEventListener uploadeventlistener = new IUpdateEventListener() {
        @Override
        public void onFinish() {
            if (TextUtils.isEmpty(apkfilename)) {
                return;
            }
            
            install(apkfilename);
        }
    };

    private Activity mParent;
    private ISaleApplication application;

    private CustomAlertDialog alertDialog;

    private OnCancelEventListener onCancelEventListener;

    public UpdateUtil(Activity mParent) {
        this.mParent = mParent;
        this.application = ISaleApplication.getInstance();
    }

    /**
     * 检查是否需要升级
     *
     * @return 是否升级
     */
    public boolean checkUpdate(boolean forceUpdate, boolean showUpToDateMessage) {
        if (application != null) {
            // 检测新版本
            float fCurVersion;
            try {
                PackageManager pm = mParent.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(mParent.getPackageName(), 0);
                fCurVersion = Float.parseFloat(pi.versionName);
            } catch (Exception e) {
                fCurVersion = (float) 1.00;
            }

            float fLastVersion;
            try {
                fLastVersion = Float.parseFloat(application.getConfig().getClientversion());
            } catch (Exception e) {
                fLastVersion = (float) 1.00;
            }

            if (fLastVersion > fCurVersion) {
                showAlertDialog("升级提示",
                        forceUpdate ? "系统检测到新版本，若不升级将导致某些功能不能正常使用，请升级..." : "系统检测到新版本，是否升级？",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                apkfilename = FileUtil.getDownloadDir() + "iSaleClient_" + application.getConfig().getClientversion() + ".apk";
                                update();
                                alertDialog.cancel();
                            }
                        },
                        "确定",
                        forceUpdate ? null : new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.cancel();
                            }
                        },
                        forceUpdate ? null : "取消"
                );
                return true;
            } else if (showUpToDateMessage) {
                showAlertDialog("提示", "已是最新版本客户端！",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                alertDialog.cancel();
                            }
                        }, "确定", null, null);
            }
        }

        return false;
    }

    public void setOnCancelEventListener(OnCancelEventListener onCancelEventListener) {
        this.onCancelEventListener = onCancelEventListener;
    }

    private void update() {
        if (TextUtils.isEmpty(apkfilename)) {
            return;
        }

        if (new File(apkfilename).exists()) {
            install(apkfilename);
        } else if (application != null) {
            UpdateTask updatetask = new UpdateTask(
                    mParent,
                    application.getConfig().getClientdownloadurl(),
                    apkfilename,
                    uploadeventlistener);
            updatetask.execute();
        }
    }

    private void install(String filename) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filename)), "application/vnd.android.package-archive");
        mParent.startActivity(intent);
        mParent.finish();
    }

    private void showAlertDialog(String title, String msg,
                                 View.OnClickListener onPositiveListener, String positiveStr,
                                 final View.OnClickListener onNegativeListener, String negativeStr) {
        if (application != null) {
            if (application.checkActivity(mParent)) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                alertDialog = new CustomAlertDialog(mParent);
                alertDialog.show();
                alertDialog.setTitle(title);
                alertDialog.setMessage(msg);
                if (onPositiveListener != null) {
                    alertDialog.setOnPositiveListener(onPositiveListener);
                    if (positiveStr != null)
                        alertDialog.getBtn_positive().setText(positiveStr);
                } else {
                    alertDialog.getBtn_positive().setVisibility(View.GONE);
                }
                if (onNegativeListener != null) {
                    View.OnClickListener listener = onCancelEventListener == null ? onNegativeListener : new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onNegativeListener.onClick(view);
                            if (onCancelEventListener != null) {
                                onCancelEventListener.onCancel();
                            }
                        }
                    };
                    alertDialog.setOnNegativeListener(listener);
                    if (negativeStr != null)
                        alertDialog.getBtn_negative().setText(negativeStr);
                } else {
                    alertDialog.getBtn_negative().setVisibility(View.GONE);
                }
                if (onCancelEventListener != null) {
                    alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                alertDialog.cancel();
                                onCancelEventListener.onCancel();
                            }
                            return true;
                        }
                    });
                }
            }
        }
    }

    public interface OnCancelEventListener {
        public void onCancel();
    }
}
