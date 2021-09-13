package com.thanguit.tuichat.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.thanguit.tuichat.R;

public class LoadingDialog {
    private static LoadingDialog instance;
    private Dialog dialog;

    private LoadingDialog() {
    }

    public static LoadingDialog getInstance() {
        if (instance == null) {
            synchronized (LoadingDialog.class) {
                if (instance == null) {
                    instance = new LoadingDialog();
                }
            }
        }
        return instance;
    }

    public void startLoading(Context context, boolean setCancelable) {
        dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_loading);

        Window window = (Window) dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(setCancelable);

        dialog.show();
    }

    public void cancelLoading() {
        dialog.dismiss();
    }
}
