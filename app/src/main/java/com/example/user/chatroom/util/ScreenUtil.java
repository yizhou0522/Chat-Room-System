package com.example.user.chatroom.util;

import android.content.Context;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

public class ScreenUtil {

    public static int dip2px(Context mContext, float dpValue) {
        final float scale = mContext.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dip(Context mContext, float pxValue) {
        final float scale = mContext.getResources()
                .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static int[] getScreenSize(Context mContext) {
        DisplayMetrics dm = mContext
                .getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        return new int[]{screenWidth, screenHeight};
    }

    /**
     *
     */
    public static int getStatusBarHeight(Context mContext) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = mContext.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     *
     */
    public static int getScreenWidth(Context mContext) {
        int screen[] = getScreenSize(mContext);
        return screen[0];
    }

    /**
     *
     * @param mContext
     * @return
     */
    public static int getScreenHeight(Context mContext) {
        int screen[] = getScreenSize(mContext);
        return screen[1];
    }
}
