package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：系统启动界面
 */

import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;

public class StartActivity extends Activity {

	private final static int SHOW_LOGIN_ACTIVITY = 0x40000001;

	private TextView tv_version;

	private String ssoid;

	private String ecCode;

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_LOGIN_ACTIVITY:
				ISaleApplication application = (ISaleApplication) getApplication();
				boolean isFirstLogin=application.getConfig().getFirstLogin();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("ssoid", ssoid);
				bundle.putString("ecCode", ecCode);
				bundle.putString("comeFrom", "StartActivity");
				intent.putExtras(bundle);
				if (isFirstLogin) {
					intent.setClass(StartActivity.this, GuideActivity.class);
				}else
				intent.setClass(StartActivity.this, LoginActivity.class);
				startActivity(intent);
				StartActivity.this.finish();
				break;
			}
		}
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		tv_version = (TextView) findViewById(R.id.tv_version);
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			tv_version.setText("销售管家 V" + pi.versionName);
		} catch (Exception e) {
			tv_version.setText("销售管家 V" + R.string.login_version);
		}
		// 获取彩云传递过来的参数
		Intent intent = this.getIntent();
		if (intent != null) {
			ssoid = intent.getStringExtra("token");
			ecCode = intent.getStringExtra("ecCode");
		} else {
			ssoid = "";
			ecCode = "";
		}
		if (!existShortcut()) {
			createShortcut();
		}
		//禁用常规页面统计，更好的统计fragment
		MobclickAgent.openActivityDurationTrack(false);
		mhandler.sendMessageDelayed(
				mhandler.obtainMessage(SHOW_LOGIN_ACTIVITY), 1000L);
	}

	private void createShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重建
		shortcut.putExtra("duplicate", false);
		// 设置名字
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				this.getString(R.string.app_name));
		// 设置图标
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this,
						R.drawable.ic_launcher));
		// 设置意图和快捷方式关联程序
		Intent intent = new Intent(this, this.getClass());
		// 桌面图标和应用绑定，卸载应用后系统会同时自动删除图标
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		sendBroadcast(shortcut);
	}

	public boolean existShortcut() {
		boolean isInstallShortcut = false;
		final ContentResolver cr = getContentResolver();
		String AUTHORITY = getAuthorityFromPermission("com.android.launcher.permission.READ_SETTINGS");
		if (AUTHORITY == null) {
			AUTHORITY = getAuthorityFromPermission("com.android.launcher.permission.WRITE_SETTINGS");
		}
		if (AUTHORITY == null) {
			return true;
		}
		Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI,
				new String[] { "title", "iconResource" }, "title=?",
				new String[] { getString(R.string.app_name) }, null);
		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
		}
		return isInstallShortcut;
	}

	/**
	 * 获取系统的SDK版本号
	 * 
	 * @return
	 */
	private int getSystemVersion() {
		return Build.VERSION.SDK_INT;
	}

	private String getAuthorityFromPermission(String permission) {
		if (permission == null)
			return null;
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(
				PackageManager.GET_PROVIDERS);
		if (packs != null) {
			for (PackageInfo pack : packs) {
				ProviderInfo[] providers = pack.providers;
				if (providers != null) {
					for (ProviderInfo provider : providers) {
						if (permission.equals(provider.readPermission))
							return provider.authority;
						if (permission.equals(provider.writePermission))
							return provider.authority;
					}
				}
			}
		}
		return null;
	}

}