package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：图片查看界面
 */


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.ui.view.ImageZoomView;
import com.zjrc.isale.client.ui.view.SimpleZoomListener;
import com.zjrc.isale.client.ui.view.ZoomState;

public class ImageViewActivity extends Activity {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;	
	
	private ImageZoomView imagezoomview;
	
	private ZoomControls zoomcontrols;
	
	private ZoomState zoomstate;
	
	private SimpleZoomListener zoomlistener;
	
	private String imagefilename;
	
	private Bitmap bitmap;	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			imagezoomview.setImage(bitmap);
			zoomstate = new ZoomState();
			imagezoomview.setZoomState(zoomstate);
			zoomlistener = new SimpleZoomListener();
			zoomlistener.setZoomState(zoomstate);
			imagezoomview.setOnTouchListener(zoomlistener);
			resetZoomState();
		}
	};	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_small);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		btn_titlebar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_titlebar_title.setText("照片查看");
		
		imagezoomview = (ImageZoomView) findViewById(R.id.imagezoomview);
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras(); 
		
		imagefilename = bundle.getString("imagefilename");
		
		bitmap = null;
		
		if (!imagefilename.equalsIgnoreCase("")){
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					BitmapFactory.Options options = new BitmapFactory.Options();  
					bitmap = BitmapFactory.decodeFile(imagefilename, options);
					handler.sendEmptyMessage(0);
				}
			});
			thread.start();
		}
//		Thread thread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				BitmapFactory.Options options = new BitmapFactory.Options();  
//				options.inPreferredConfig=Bitmap.Config.RGB_565;//表示16位位图 565代表对应三原色占的位数
//				options.inInputShareable=true;
//				options.inPurgeable=true;//设置图片可以被回收
//			    InputStream is = getResources().openRawResource(R.drawable.demo_photo);
//			    bitmap = BitmapFactory.decodeStream(is, null, options);
//				handler.sendEmptyMessage(0);
//			}
//		});
//		thread.start();
		zoomcontrols = (ZoomControls) findViewById(R.id.zoomcontrols);
		zoomcontrols.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtils.isNotFastDoubleClick()) {
					float z = zoomstate.getZoom() + 0.25f;
					zoomstate.setZoom(z);
					zoomstate.notifyObservers();
				}
			}
		});
		zoomcontrols.setOnZoomOutClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtils.isNotFastDoubleClick()) {
					float z = zoomstate.getZoom() - 0.25f;
					zoomstate.setZoom(z);
					zoomstate.notifyObservers();
				}
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null)
			bitmap.recycle();
		if (zoomstate!=null){
			zoomstate.deleteObservers();
		}
	}	
	
	private void resetZoomState() {
		zoomstate.setPanX(0.5f);
		zoomstate.setPanY(0.5f);
		zoomstate.setZoom(1f);
		zoomstate.notifyObservers();
	}	
}
