package com.thanguit.tuichat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.stringee.listener.StatusListener;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.databinding.ActivityOutGoingCallBinding;
import com.thanguit.tuichat.models.User;
import com.thanguit.tuichat.utils.Common;
import com.thanguit.tuichat.utils.OptionDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OutGoingCallActivity extends AppCompatActivity {
    private ActivityOutGoingCallBinding activityOutGoingCallBinding;

    private StringeeCall stringeeCall;
    private StringeeAudioManager stringeeAudioManager;

    private String to;
    private boolean isVideoCall;
    private boolean isMute = false;
    private boolean isSpeaker = false;
    private boolean isVideo = false;

    private StringeeCall.MediaState mMediaState;
    private StringeeCall.SignalingState mSignalingState;

    private KeyguardManager.KeyguardLock lock;

    public static final int REQUEST_PERMISSION_CALL = 1;

    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add Flag for show on lockScreen and disable keyguard
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        activityOutGoingCallBinding = ActivityOutGoingCallBinding.inflate(getLayoutInflater());
        setContentView(activityOutGoingCallBinding.getRoot());

        lock = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).newKeyguardLock(Context.KEYGUARD_SERVICE);
        lock.disableKeyguard();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        Common.isInCall = true;

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("USER")) {
                user = (User) intent.getParcelableExtra("USER");
                if (user != null) {
                    to = user.getUid().trim();
                }
            } else if (intent.hasExtra("VIDEO_CALL")) {
                isVideoCall = intent.getBooleanExtra("VIDEO_CALL", false);
            }
        }

        isSpeaker = isVideoCall;
        activityOutGoingCallBinding.ibSpeaker.setImageResource(isSpeaker ? R.drawable.ic_speaker_on : R.drawable.ic_speaker_off);

        isVideo = isVideoCall;
        activityOutGoingCallBinding.ibCamera.setImageResource(isVideo ? R.drawable.ic_video_camera_on : R.drawable.ic_video_camera_off);

        activityOutGoingCallBinding.flCamera.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        activityOutGoingCallBinding.btnSwitch.setVisibility(isVideo ? View.VISIBLE : View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> lstPermissions = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                lstPermissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (isVideoCall) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    lstPermissions.add(Manifest.permission.CAMERA);
                }
            }

            if (lstPermissions.size() > 0) {
                String[] permissions = new String[lstPermissions.size()];
                for (int i = 0; i < lstPermissions.size(); i++) {
                    permissions[i] = lstPermissions.get(i);
                }
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CALL);
                return;
            }
        }

        //create audio manager to control audio device
        stringeeAudioManager = StringeeAudioManager.create(OutGoingCallActivity.this);
        stringeeAudioManager.start(new StringeeAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StringeeAudioManager.AudioDevice audioDevice, Set<StringeeAudioManager.AudioDevice> set) {
            }
        });

        stringeeAudioManager.setSpeakerphoneOn(isVideoCall);
        makeCall();

        listeners();
    }

    private void listeners() {
        activityOutGoingCallBinding.ibMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;
                activityOutGoingCallBinding.ibMicrophone.setImageResource(isMute ? R.drawable.ic_microphone_off : R.drawable.ic_microphone_on);
                if (stringeeCall != null) {
                    stringeeCall.mute(isMute);
                }
            }
        });

        activityOutGoingCallBinding.ibSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSpeaker = !isSpeaker;
                activityOutGoingCallBinding.ibSpeaker.setImageResource(isSpeaker ? R.drawable.ic_speaker_on : R.drawable.ic_speaker_off);
                if (stringeeAudioManager != null) {
                    stringeeAudioManager.setSpeakerphoneOn(isSpeaker);
                }
            }
        });

        activityOutGoingCallBinding.ibEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityOutGoingCallBinding.tvState.setText("Ended");
                endCall();
            }
        });

        activityOutGoingCallBinding.ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideo = !isVideo;
                activityOutGoingCallBinding.ibCamera.setImageResource(isVideo ? R.drawable.ic_video_camera_on : R.drawable.ic_video_camera_off);
                if (stringeeCall != null) {
                    stringeeCall.enableVideo(isVideo);
                }
            }
        });

        activityOutGoingCallBinding.btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stringeeCall != null) {
                    stringeeCall.switchCamera(new StatusListener() {
                        @Override
                        public void onSuccess() {
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = false;
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                } else {
                    isGranted = true;
                }
            }
        }
        if (requestCode == REQUEST_PERMISSION_CALL) {
            if (!isGranted) {
                finish();
            } else {
                makeCall();
            }
        }
    }

    private void makeCall() {
        stringeeCall = new StringeeCall(MainActivity.stringeeClient, MainActivity.stringeeClient.getUserId(), to);
        stringeeCall.setVideoCall(isVideoCall);

        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                Log.e("Stringee", "======== Custom data: " + stringeeCall.getCustomDataFromYourServer());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSignalingState = signalingState;
                        switch (signalingState) {
                            case CALLING:
                                activityOutGoingCallBinding.tvState.setText("Outgoing call");
                                break;
                            case RINGING:
                                activityOutGoingCallBinding.tvState.setText("Ringing");
                                break;
                            case ANSWERED:
                                activityOutGoingCallBinding.tvState.setText("Starting");
                                if (mMediaState == StringeeCall.MediaState.CONNECTED) {
                                    activityOutGoingCallBinding.tvState.setText("Started");
                                }
                                break;
                            case BUSY:
                                activityOutGoingCallBinding.tvState.setText("Busy");
                                endCall();
                            case ENDED:
                                activityOutGoingCallBinding.tvState.setText("Ended");
                                endCall();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(StringeeCall stringeeCall, int i, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Common.reportMessage(OutGoingCallActivity.this, s);
                    }
                });
            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s) {
            }

            @Override
            public void onMediaStateChange(StringeeCall stringeeCall, StringeeCall.MediaState mediaState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMediaState = mediaState;
                        if (mediaState == StringeeCall.MediaState.CONNECTED) {
                            if (mSignalingState == StringeeCall.SignalingState.ANSWERED) {
                                activityOutGoingCallBinding.tvState.setText("Started");
                            }
                        }
                    }
                });
            }

            @Override
            public void onLocalStream(StringeeCall stringeeCall) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (stringeeCall.isVideoCall()) {
                            activityOutGoingCallBinding.flLocal.removeAllViews();
                            activityOutGoingCallBinding.flLocal.addView(stringeeCall.getLocalView());
                            stringeeCall.renderLocalView(true);
                        }
                    }
                });
            }

            @Override
            public void onRemoteStream(StringeeCall stringeeCall) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (stringeeCall.isVideoCall()) {
                            activityOutGoingCallBinding.flRemote.removeAllViews();
                            activityOutGoingCallBinding.flRemote.addView(stringeeCall.getRemoteView());
                            stringeeCall.renderRemoteView(false);
                        }
                    }
                });
            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {
            }
        });

        stringeeCall.makeCall();
    }

    private void endCall() {
        stringeeCall.hangup();
        if (stringeeAudioManager != null) {
            stringeeAudioManager.stop();
            stringeeAudioManager = null;
        }
        Common.postDelay(new Runnable() {
            @Override
            public void run() {
                Common.isInCall = false;
                finish();
            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showDialog();
    }

    private void showDialog() {
        OptionDialog optionDialog = new OptionDialog(this, "End tha call", "Do you want to end this call?",
                "Cancel", "OK", true, new OptionDialog.SetActionButtonListener() {
            @Override
            public void setNegativeButtonListener(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void setPositiveButtonListener(Dialog dialog) {
                endCall();
                dialog.dismiss();
            }
        });
        optionDialog.show();
    }
}