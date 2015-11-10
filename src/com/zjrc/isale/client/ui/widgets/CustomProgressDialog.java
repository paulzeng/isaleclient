package com.zjrc.isale.client.ui.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.util.LogUtil;

public class CustomProgressDialog extends Dialog {
	private ImageView iv_progress;

	private TextView tv_message;

	public View.OnClickListener cancelListener;

	public boolean cancelble = false;

	public CustomProgressDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
	}

	public CustomProgressDialog(Context context,
			View.OnClickListener cancelListener) {
		super(context, R.style.CustomProgressDialog);
		this.cancelListener = cancelListener;
	}

	public CustomProgressDialog(Context context,
			View.OnClickListener cancelListener, boolean cancelble) {
		super(context, R.style.CustomProgressDialog);
		this.cancelListener = cancelListener;
		this.cancelble = cancelble;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress);
		LayoutParams params = getWindow().getAttributes();
		params.height = LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(params);
		iv_progress = (ImageView) findViewById(R.id.iv_progress);
		tv_message = (TextView) findViewById(R.id.tv_message);
		if (cancelble) {
			this.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					cancelListener.onClick(null);
					LogUtil.i("info", "setOnCancelListener");
				}
			});
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		final Interpolator interpolator = new LinearInterpolator();
		RotateAnimation animation = new RotateAnimation(0f, 360f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(500);
		animation.setInterpolator(interpolator);
		animation.setRepeatMode(Animation.RESTART);
		animation.setRepeatCount(-1);
		iv_progress.startAnimation(animation);
	}

	public void setMessage(String message) {
		this.tv_message.setText(message);
	}

	public void setOnCancelListener(View.OnClickListener cancelListener) {
		this.cancelListener = cancelListener;
	}

}
