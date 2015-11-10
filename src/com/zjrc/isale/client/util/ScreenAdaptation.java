package com.zjrc.isale.client.util;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * 以宽高最大比例等比缩放适配
 * 
 * @author zgm
 * 
 */
public class ScreenAdaptation {

	public static final float REFER_PX_WIDTH = 720.0f;
	public static final float REFER_PX_HEIGHT = 1230.0f;

	protected static float Vertical_scale = (float) 1.0;
	protected static float Horizontal_scale = (float) 1.0;
	private static float Scale_Geo = (float) 1.0;

	private static int mVisiableScreenWidth = 0;
	private static int mVisiableScreenHeight = 0;

	public static int getVisialbeScreenWith() {
		if (mVisiableScreenWidth == 0) {
			return (int) REFER_PX_WIDTH;
		}
		return mVisiableScreenWidth;
	}

	public static int getVisialbeScreenHeight() {
		if (mVisiableScreenHeight == 0) {

		}
		return mVisiableScreenHeight;
	}

	public static void InitDisplayWidthHeight(int width, int height) {
		mVisiableScreenWidth = width;
		mVisiableScreenHeight = height;
		Horizontal_scale = width / REFER_PX_WIDTH;
		Vertical_scale = height / REFER_PX_HEIGHT;
		if (Horizontal_scale > Vertical_scale)
			Scale_Geo = Horizontal_scale;
		else
			Scale_Geo = Vertical_scale;
	}

	public static void ScreenAdaptation(Activity context, int parentlayoutid) {

		ViewGroup vg = (ViewGroup) context.findViewById(parentlayoutid);
		SubViewAdaption(vg);

	}

	public static void SubViewAdaption(ViewGroup vg) {

		for (int i = 0, max = vg.getChildCount(); i < max; i++) {
			View view = null;
			view = vg.getChildAt(i);
			if (view == null) {
				// LogOut.E("--------> the view can't find in this layout!");
				return;
			}
			ResetwidthAndHeight(view);

			if (isViewGroup(view)) {
				SubViewAdaption((ViewGroup) view);
			}

		}
	}

	@SuppressLint("NewApi")
	public static void ResetwidthAndHeight(View view) {

		MarginLayoutParams prms = (MarginLayoutParams) view.getLayoutParams();
		log(prms);
		int vierson = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (vierson >= 16) {
			int minHeight = view.getMinimumHeight();
			view.setMinimumHeight(getloacVerticalpx(minHeight));
			int minWidth = view.getMinimumWidth();
			view.setMinimumWidth(getloacHorizontalpx(minWidth));
		}
		prms.height = getloacVerticalpx(prms.height);
		prms.width = getloacHorizontalpx(prms.width);
		prms.leftMargin = getloacHorizontalpx(prms.leftMargin);
		prms.rightMargin = getloacHorizontalpx(prms.rightMargin);
		prms.topMargin = getloacVerticalpx(prms.topMargin);
		prms.bottomMargin = getloacVerticalpx(prms.bottomMargin);
		view.setLayoutParams(prms);
		log(prms);
	}

	public static int getloacVerticalpx(int height) {
		if (height == ViewGroup.LayoutParams.FILL_PARENT
				|| height == ViewGroup.LayoutParams.MATCH_PARENT
				|| height == ViewGroup.LayoutParams.WRAP_CONTENT)
			return height;
		// float hdip = height/dm.density;
		int tempheight;
		if (height != 0) {
			tempheight = (int) (height * Scale_Geo);
			if (tempheight == 0)
				tempheight = 1;

			return tempheight;
		}

		return (int) (height * Scale_Geo);
	}

	public static int getloacHorizontalpx(int width) {
		if (width == ViewGroup.LayoutParams.FILL_PARENT
				|| width == ViewGroup.LayoutParams.MATCH_PARENT
				|| width == ViewGroup.LayoutParams.WRAP_CONTENT)
			return width;
		// float wdip = width/dm.density;
		int tempwidth;
		if (width != 0) {
			tempwidth = (int) (width * Scale_Geo);
			if (tempwidth == 0)
				tempwidth = 1;

			return tempwidth;
		}
		return (int) (width * Scale_Geo);
	}

	public static int getY(int height) {
		// return (int)(height*Vertical_scale);
		return (int) (height * Vertical_scale);
	}

	public static int getX(int width) {
		return (int) (width * Horizontal_scale);
	}

	public static boolean isViewGroup(View view) {
		if (view instanceof LinearLayout || view instanceof RelativeLayout
				|| view instanceof FrameLayout || view instanceof ScrollView

		) {
			return true;
		}
		return false;
	}

	public static float getVerticaScale() {
		return Vertical_scale;
	}

	public static float getHorizontalScale() {
		return Horizontal_scale;
	}

	public static void log(MarginLayoutParams prms) {
		// LogOut.E("---------------------");
		// LogOut.E( "height"+prms.height);
		// LogOut.E( "width"+prms.width);
		// LogOut.E( "leftMargin"+prms.leftMargin);
		// LogOut.E( "rightMargin"+prms.rightMargin);
		// LogOut.E( "height"+prms.height);
		// LogOut.E( "topMargin"+prms.topMargin);
		// LogOut.E("bottomMargin"+prms.bottomMargin);
		// LogOut.E("---------------------");
	}

}
