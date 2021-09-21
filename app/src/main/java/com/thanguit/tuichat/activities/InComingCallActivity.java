package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.thanguit.tuichat.databinding.ActivityInComingCallBinding;
import com.thanguit.tuichat.utils.Common;

public class InComingCallActivity extends AppCompatActivity {
    private ActivityInComingCallBinding activityInComingCallBinding;

    private StringeeCall stringeeCall;
    private StringeeAudioManager stringeeAudioManager;
    private boolean isMute = false;
    private boolean isSpeaker = false;
    private boolean isVideo = false;

    private StringeeCall.MediaState mMediaState;
    private StringeeCall.SignalingState mSignalingState;

    private KeyguardManager.KeyguardLock lock;

    public static final int REQUEST_PERMISSION_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        activityInComingCallBinding = ActivityInComingCallBinding.inflate(getLayoutInflater());
        setContentView(activityInComingCallBinding.getRoot());

        lock = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).newKeyguardLock(Context.KEYGUARD_SERVICE);
        lock.disableKeyguard();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        Common.isInCall = true;
    }


}