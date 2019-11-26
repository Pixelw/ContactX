package com.pixel.mycontact.utils;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author Carl Su
 * @date 2019/11/26
 */
public class StyleUtils {
    /**
     * 自适应状态栏透明
     *
     * @param window         上下文
     * @param colorOfToolbar 标题栏颜色，判断亮度
     */
    public static void setStatusBarTransparent(Window window, int colorOfToolbar) {

        float luminanceOfToolbar = (float) ((0.2126 * Color.red(colorOfToolbar))
                + (0.7152 * Color.green(colorOfToolbar))
                + (0.0722 * Color.blue(colorOfToolbar)));
        Log.d("luminance:", String.valueOf(luminanceOfToolbar));

        int sdkInt = Build.VERSION.SDK_INT;

        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
                if (luminanceOfToolbar >= 200.0) {
                    if (sdkInt >= Build.VERSION_CODES.M) {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        window.setStatusBarColor(Color.TRANSPARENT);
                    } else { //system below 6.0 should avoid pure white status bar

                    }
                } else { //full trans
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    /**
     * 直接透明
     *
     * @param window
     */
    public static void setStatusBarTransparent(Window window) {
        int sdkInt = Build.VERSION.SDK_INT;

        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }
}
