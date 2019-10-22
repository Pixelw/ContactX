package com.pixel.mycontact.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class PermissionsUtils {


    public static boolean getPermissionForCamera(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
            return false;
        }
        return true;
    }
}
