package com.thanguit.tuichat.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thanguit.tuichat.R;

public class MyToast extends Toast {
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public static final int WARNING = 3;
    public static final int INFORMATION = 4;

    public static final int SHORT = 3500;
    public static final int LONG = 5000;

    public MyToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, int type, String content, int duration) {
        Toast toast = new Toast(context);

        toast.setDuration(duration);

        View layout = LayoutInflater.from(context).inflate(R.layout.layout_toast, null, false);
        ImageView ivIconToast = layout.findViewById(R.id.ivIconToast);
        TextView tvTextToast = layout.findViewById(R.id.tvTextToast);
        if (content.isEmpty()) {
            tvTextToast.setText("");
        } else {
            tvTextToast.setText(content.trim());
        }

        switch (type) {
            case SUCCESS: {
                ivIconToast.setImageResource(R.drawable.ic_success_2);
                break;
            }
            case ERROR: {
                ivIconToast.setImageResource(R.drawable.ic_error);
                break;
            }

            case WARNING: {
                ivIconToast.setImageResource(R.drawable.ic_warning);
                break;
            }

            case INFORMATION: {
                ivIconToast.setImageResource(R.drawable.ic_info);
                break;
            }
        }

        toast.setView(layout);
        return toast;
    }
}
