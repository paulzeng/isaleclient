package com.zjrc.isale.client.ui.widgets.pulltorefreshview.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingLayout extends FrameLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private final ImageView headerImage;
	private final ImageView headerProgress;
	private final TextView headerProgress_text;
	private final TextView headerText;

	private String pullLabel;
	private String refreshingLabel;
	private String releaseLabel;

	private final Animation rotateAnimation, resetRotateAnimation;
	private final Animation progressAnimation;

	public LoadingLayout(Context context, final int mode, String releaseLabel,
			String pullLabel, String refreshingLabel) {
		super(context);
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_header, this);
		headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
		headerImage = (ImageView) header
				.findViewById(R.id.pull_to_refresh_image);
		headerProgress = (ImageView) header
				.findViewById(R.id.pull_to_refresh_progress);
		headerProgress_text = (TextView) header
				.findViewById(R.id.pull_to_refresh_progress_text);

		final Interpolator interpolator = new LinearInterpolator();
		rotateAnimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setInterpolator(interpolator);
		rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		rotateAnimation.setFillAfter(true);

		resetRotateAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		resetRotateAnimation.setInterpolator(interpolator);
		resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		resetRotateAnimation.setFillAfter(true);

		progressAnimation = new RotateAnimation(0f, 360f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		progressAnimation.setDuration(500);
		progressAnimation.setInterpolator(interpolator);
		progressAnimation.setRepeatMode(Animation.RESTART);
		progressAnimation.setRepeatCount(-1);

		this.releaseLabel = releaseLabel;
		this.pullLabel = pullLabel;
		this.refreshingLabel = refreshingLabel;

		switch (mode) {
		case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
			headerImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
			break;
		case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
		default:
			headerImage.setImageResource(R.drawable.pulltorefresh_down_arrow);
			break;
		}
	}

	public void reset() {
		headerText.setText(pullLabel);
		headerImage.setVisibility(View.VISIBLE);
		headerText.setVisibility(View.VISIBLE);
		headerProgress.setVisibility(View.GONE);
		headerProgress_text.setVisibility(View.GONE);
	}

	public void releaseToRefresh() {
		headerText.setText(releaseLabel);
		headerImage.clearAnimation();
		headerImage.startAnimation(rotateAnimation);
	}

	public void setPullLabel(String pullLabel) {
		this.pullLabel = pullLabel;
	}

	public void refreshing() {
		headerImage.clearAnimation();
		headerImage.setVisibility(View.GONE);
		headerText.setVisibility(View.GONE);
		headerProgress.setVisibility(View.VISIBLE);
		headerProgress.startAnimation(progressAnimation);
		headerProgress_text.setVisibility(View.VISIBLE);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		headerProgress_text.setText("更新中...\n更新于：" + format.format(new Date()));
	}

	public void setRefreshingLabel(String refreshingLabel) {
		this.refreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
		this.releaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
		headerText.setText(pullLabel);
		headerImage.clearAnimation();
		headerImage.startAnimation(resetRotateAnimation);
	}

	public void setTextColor(int color) {
		headerText.setTextColor(color);
	}

}
