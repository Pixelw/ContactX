package com.pixel.mycontact.utils;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;

/**
 * @author Carl Su
 * @date 2019/11/26
 */
public class StyleUtils {
    /**
     * 自适应状态栏透明
     *
     * @param window         上下文
     * @param toolbar        标题栏，用于判断亮度
     * @param renderBelowBar 是否把view扩展到全屏
     */

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setStatusBarTransparent(Window window, Toolbar toolbar, boolean renderBelowBar) {

        int colorOfToolbar = ((ColorDrawable) toolbar.getBackground()).getColor();
        float luminanceOfToolbar = (float) ((0.2126 * Color.red(colorOfToolbar))
                + (0.7152 * Color.green(colorOfToolbar))
                + (0.0722 * Color.blue(colorOfToolbar)));
        Log.d("luminance:", String.valueOf(luminanceOfToolbar));

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setStatusBarTransparent(window, renderBelowBar);//default transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && luminanceOfToolbar >= 200.0) {
            setStatusBarLightModeM(window);
        }
    }

    public static void setStatusBarTransparent(Window window, Toolbar toolbar) {
        setStatusBarTransparent(window, toolbar, false);
    }

    /**
     * 直接透明
     *
     * @param window 上下文
     */
    public static void setStatusBarTransparent(Window window, boolean renderBelowBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarTransparentLollipop(window);
        } else {
            setStatusBarTransparentKitKat(window);
        }
        if (renderBelowBar) {
            renderBelowBar(window);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void setStatusBarLightModeM(Window window) {
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(decorView.getWindowVisibility()
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setStatusBarTransparentLollipop(Window window) {
        //full trans
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setStatusBarTransparentKitKat(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    private static void renderBelowBar(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
