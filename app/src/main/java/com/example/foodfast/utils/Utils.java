package com.example.foodfast.utils;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.inputmethod.InputMethodManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static void requestPermissions() {
        TedPermission.Builder builderTed = TedPermission.create();
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                return ;
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                return;
            }
        };
        builderTed.setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }
    public static String convertMoney(int money){
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(money);
    }
}
