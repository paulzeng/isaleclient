package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：情报收集系统客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：新闻公告详情界面
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.CommonUtils;

public class NoticeDetailActivity extends Activity {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;
	
	private WebView wv_web;
	private RelativeLayout rl_progress;
	private ProgressBar pb_process;
	private TextView tv_reload;
	private boolean bloaderror;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		tv_titlebar_title.setText("新闻公告内容");		
		wv_web = (WebView) findViewById(R.id.wv_web);
		rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
		pb_process = (ProgressBar) findViewById(R.id.pb_process);
        tv_reload = (TextView) findViewById(R.id.tv_reload);
		
		wv_web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wv_web.getSettings().setSupportZoom(true);
		wv_web.getSettings().setBuiltInZoomControls(true);
		wv_web.getSettings().setDefaultZoom(ZoomDensity.CLOSE);

		wv_web.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				SetProgress(progress);
			}

		});

		wv_web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				bloaderror = true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (bloaderror) {
					pb_process.setVisibility(View.GONE);
					rl_progress.setVisibility(View.VISIBLE);
                    tv_reload.setVisibility(View.VISIBLE);
					wv_web.setVisibility(View.GONE);
				}
			}
		});

		String sURL = getIntent().getStringExtra("url");
		if (sURL != null) {
			wv_web.loadUrl(sURL);
		} else {
			pb_process.setVisibility(View.GONE);
            tv_reload.setVisibility(View.VISIBLE);
		}

		rl_progress.setOnClickListener(new LinearLayout.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (pb_process.getVisibility() == 8) {
						bloaderror = false;
						rl_progress.setVisibility(View.VISIBLE);
						pb_process.setVisibility(View.VISIBLE);
						tv_reload.setVisibility(View.GONE);
						wv_web.reload();
					}
				}
			}
		});		
	}
	
	@Override
	protected void onDestroy() {
		wv_web.stopLoading();
		super.onDestroy();
	}
	
	private void SetProgress(int progress) {
		pb_process.setProgress(progress);
		if (progress == 100) {
			if (!bloaderror) {
				pb_process.setVisibility(View.GONE);
				rl_progress.setVisibility(View.GONE);
                tv_reload.setVisibility(View.GONE);
				wv_web.setVisibility(View.VISIBLE);
			}
		} else {
			pb_process.setVisibility(View.VISIBLE);
			rl_progress.setVisibility(View.VISIBLE);
		}
	}	
}
