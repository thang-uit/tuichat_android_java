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

public class OptionDialog extends Dialog {
    private Context context;
    private Dialog dialog;

    private SetActionButtonListener setActionButtonListener;

    public OptionDialog(@NonNull Context context) {
        super(context);
    }

    public OptionDialog(Context context, SetActionButtonListener setActionButtonListener) {
        super(context);
        this.context = context;
        this.setActionButtonListener = setActionButtonListener;
    }

    public interface SetActionButtonListener {
        void setNegativeButtonListener(Dialog dialog);

        void setPositiveButtonListener(Dialog dialog);
    }

    public Dialog setViewDialog(String title, String content, String negativeButton, String positiveButton, boolean setCancelable) {
        dialog = new Dialog(this.context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_textview_dialog);

        Window window = (Window) dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(setCancelable);

        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDialogContent = dialog.findViewById(R.id.tvDialogContent);
        Button btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);
        Button btnDialogAction = dialog.findViewById(R.id.btnDialogAction);

        AnimationScale.getInstance().eventButton(this.context, btnDialogCancel);
        AnimationScale.getInstance().eventButton(this.context, btnDialogAction);

        tvDialogTitle.setText(title.trim());
        tvDialogContent.setText(content.trim());
        btnDialogCancel.setText(negativeButton.trim());
        btnDialogAction.setText(positiveButton.trim());

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

        return dialog;
    }
}