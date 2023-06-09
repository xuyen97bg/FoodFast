package com.example.foodfast.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.foodfast.R;
import com.example.foodfast.databinding.DialogShowErrorBinding;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static void showDialogError(Context context, String errorString){
        Dialog dialog = new Dialog(context);
        DialogShowErrorBinding binding = DialogShowErrorBinding.inflate(LayoutInflater.from(context));
        binding.content.setText(errorString);
        dialog.setContentView(R.layout.dialog_show_error);
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog);
        binding.submit.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static String convertDateType1(long c){
        Date date = new Date(c);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm aa");
        return sdf.format(date);
    }
}
