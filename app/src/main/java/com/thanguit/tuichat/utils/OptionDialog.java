package com.thanguit.tuichat.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.thanguit.tuichat.R;
import com.thanguit.tuichat.animations.AnimationScale;

public class OptionDialog {
    private final Context context;
    private final String title;
    private final String content;
    private final String negativeButton;
    private final String positiveButton;
    private final boolean setCancelable;
    private final SetActionButtonListener setActionButtonListener;

    public OptionDialog(Context context, String title, String content, String negativeButton, String positiveButton, boolean setCancelable, SetActionButtonListener setActionButtonListener) {
        this.context = context;
        this.title = title;
        this.content = content;
        this.negativeButton = negativeButton;
        this.positiveButton = positiveButton;
        this.setCancelable = setCancelable;
        this.setActionButtonListener = setActionButtonListener;
    }

    public interface SetActionButtonListener {
        void setNegativeButtonListener(Dialog dialog);

        void setPositiveButtonListener(Dialog dialog);
    }

    public void show() {
        final Dialog dialog = new Dialog(this.context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_textview_dialog);

        Window window = (Window) dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(this.setCancelable);

        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDialogContent = dialog.findViewById(R.id.tvDialogContent);
        Button btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);
        Button btnDialogAction = dialog.findViewById(R.id.btnDialogAction);

        AnimationScale.getInstance().eventButton(this.context, btnDialogCancel);
        AnimationScale.getInstance().eventButton(this.context, btnDialogAction);

        tvDialogTitle.setText(this.title.trim());
        tvDialogContent.setText(this.content.trim());
        btnDialogCancel.setText(this.negativeButton.trim());
        btnDialogAction.setText(this.positiveButton.trim());

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActionButtonListener.setNegativeButtonListener(dialog);
            }
        });

        btnDialogAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActionButtonListener.setPositiveButtonListener(dialog);
            }
        });

        dialog.show();
    }
}
