package com.thanguit.tuichat.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;

import java.util.HashMap;
import java.util.Map;

public class Common {
    public static Map<String, StringeeCall> callsMap = new HashMap<>();
    public static boolean isInCall = false;

    public static void reportMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void postDelay(Runnable runnable, long delayMillis) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delayMillis);
    }
}
