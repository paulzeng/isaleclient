package com.zjrc.isale.client.ui.widgets.menudrawer;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

final class ViewHelper {

    private ViewHelper() {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static int getLeft(View v) {
        if (MenuDrawer.USE_TRANSLATIONS) {
            return (int) (v.getLeft() + v.getTranslationX());
        }

        return v.getLeft();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static int getTop(View v) {
        if (MenuDrawer.USE_TRANSLATIONS) {
            return (int) (v.getTop() + v.getTranslationY());
        }

        return v.getTop();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static int getRight(View v) {
        if (MenuDrawer.USE_TRANSLATIONS) {
            return (int) (v.getRight() + v.getTranslationX());
        }

        return v.getRight();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static int getBottom(View v) {
        if (MenuDrawer.USE_TRANSLATIONS) {
            return (int) (v.getBottom() + v.getTranslationY());
        }

        return v.getBottom();
    }
}
