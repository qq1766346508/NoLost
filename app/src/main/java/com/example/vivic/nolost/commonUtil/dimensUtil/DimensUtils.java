package com.example.vivic.nolost.commonUtil.dimensUtil;

import android.content.Context;

import com.example.vivic.nolost.NoLostApplication;

/**
 */
public class DimensUtils {
    public static final float DENSITY;

    static {
        DENSITY = NoLostApplication.getContext().getResources().getDisplayMetrics().density;
    }

    public static int dp2px(float dpValue) {
        return (int) (dpValue * DENSITY + 0.5f);
    }

    public static int dip2pixel(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int pixel2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2pixel(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int pixel2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getStatusBarHeight() {
        return getStatusBarHeight(NoLostApplication.getContext());
    }
}
