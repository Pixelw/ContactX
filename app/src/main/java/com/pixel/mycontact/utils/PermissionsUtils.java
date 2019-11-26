package com.pixel.mycontact.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;


/**
 * @author Carl Su
 * @date 2019/10/26
 * <p>
 * The type Permissions utils.
 * 权限工具类
 */
public class PermissionsUtils {

    public static boolean hasOrRequestFor(String permission, Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(activity, permission)
                        != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    public static boolean hasOrRequestForCamera(Activity activity, int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            return false;
        }
        return true;
    }

    public static boolean hasOrRequestForCall(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, requestCode);
            return false;
        }
        return true;
    }
}
