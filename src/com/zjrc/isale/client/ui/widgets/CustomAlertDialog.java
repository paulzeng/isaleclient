package com.zjrc.isale.client.ui.widgets;

import com.zjrc.isale.client.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class CustomAlertDialog extends Dialog {
	private TextView tv_title;
	private TextView tv_message;
	private Button btn_positive;

	public Button getBtn_positive() {
		return btn_positive;
	}

	public void setBtn_positive(Button btn_positive) {
		this.btn_positive = btn_positive;
	}

	public Button getBtn_negative() {
		return btn_negative;
	}

	public void setBtn_negative(Button btn_negative) {
		this.btn_negative = btn_negative;
	}

	private Button btn_negative;

	public View.OnClickListener negativeListener;
	public View.OnClickListener positiveListener;

	public CustomAlertDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
	}

	public CustomAlertDialog(Context context,
			View.OnClickListener positiveListener,
			View.OnClickListener negativeListener) {
		super(context, R.style.CustomProgressDialog);
		this.positiveListener = positiveListener;
		this.negativeListener = negativeListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alertdialog);
		LayoutParams params = getWindow().getAttributes();
		params.height = LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(params);
		tv_title = (TextView) findViewById(R.id.tv_alertdialog_title);
		tv_message = (TextView) findViewById(R.id.tv_alertdialog_msg);
		btn_positive = (Button) findViewById(R.id.btn_alertdialog_positive);
		btn_negative = (Button) findViewById(R.id.btn_alertdialog_negative);
		btn_positive.setOnClickListener(positiveListener);
		btn_negative.setOnClickListener(negativeListener);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	}

	public void setMessage(String message) {
		this.tv_message.setText(message);
	}

	public void setTitle(String title) {
		this.tv_title.setText(title);
	}

	public void setOnNegativeListener(View.OnClickListener negativeListener) {
		this.negativeListener = negativeListener;
		btn_negative.setOnClickListener(negativeListener);
	}

	public void setOnPositiveListener(View.OnClickListener positiveListener) {
		this.positiveListener = positiveListener;
		btn_positive.setOnClickListener(positiveListener);
	}
}
