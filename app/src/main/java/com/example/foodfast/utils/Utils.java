package com.example.foodfast.utils;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.inputmethod.InputMethodManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class Utils {
//    kiểm soát lỗi khi nhập từ bàn phím
    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

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
}
