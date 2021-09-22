package com.thanguit.tuichat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.stringee.listener.StatusListener;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.databinding.ActivityInComingCallBinding;
import com.thanguit.tuichat.utils.Common;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        String callId = getIntent().getStringExtra("CALL_ID");
        stringeeCall = Common.callsMap.get(callId);

        if (stringeeCall != null) {
            activityInComingCallBinding.tvFrom.setText(stringeeCall.getFrom());

            isSpeaker = stringeeCall.isVideoCall();
            activityInComingCallBinding.ibSpeaker.setImageResource(isSpeaker ? R.drawable.ic_speaker_on : R.drawable.ic_speaker_off);

            isVideo = stringeeCall.isVideoCall();
            activityInComingCallBinding.ibCamera.setImageResource(isVideo ? R.drawable.ic_video_camera_on : R.drawable.ic_video_camera_off);

            activityInComingCallBinding.flCamera.setVisibility(isVideo ? View.VISIBLE : View.GONE);
            activityInComingCallBinding.btnSwitch.setVisibility(isVideo ? View.VISIBLE : View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                List<String> lstPermissions = new ArrayList<>();

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    lstPermissions.add(Manifest.permission.RECORD_AUDIO);
                }

                if (stringeeCall.isVideoCall()) {
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

            startRinging();
        }

        listeners();
    }

    private void listeners() {
        activityInComingCallBinding.ibMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;
                activityInComingCallBinding.ibMicrophone.setImageResource(isMute ? R.drawable.ic_microphone_off : R.drawable.ic_microphone_on);
                if (stringeeCall != null) {
                    stringeeCall.mute(isMute);
                }
            }
        });

        activityInComingCallBinding.ibSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSpeaker = !isSpeaker;
                activityInComingCallBinding.ibSpeaker.setImageResource(isSpeaker ? R.drawable.ic_speaker_on : R.drawable.ic_speaker_off);
                if (mSignalingState == StringeeCall.SignalingState.ANSWERED || mMediaState == StringeeCall.MediaState.CONNECTED) {
                    if (stringeeAudioManager != null) {
                        stringeeAudioManager.setSpeakerphoneOn(isSpeaker);
                    }
                }
            }
        });

        activityInComingCallBinding.ibAnswerCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stringeeCall != null) {
                    activityInComingCallBinding.flSpeaker.setVisibility(View.VISIBLE);
                    activityInComingCallBinding.flCamera.setVisibility(View.VISIBLE);
                    activityInComingCallBinding.flMicrophone.setVisibility(View.VISIBLE);

                    activityInComingCallBinding.flAnswerCall.setVisibility(View.GONE);
                    stringeeCall.answer();
                }
            }
        });

        activityInComingCallBinding.ibEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityInComingCallBinding.tvState.setText("Ended");
                endCall(true, false);
            }
        });

        activityInComingCallBinding.ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideo = !isVideo;
                activityInComingCallBinding.ibCamera.setImageResource(isVideo ? R.drawable.ic_video_camera_on : R.drawable.ic_video_camera_off);
                if (stringeeCall != null) {
                    stringeeCall.enableVideo(isVideo);
                }
            }
        });

        activityInComingCallBinding.btnSwitch.setOnClickListener(new View.OnClickListener() {
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
                activityInComingCallBinding.tvState.setText("Ended");
                endCall(false, true);
            } else {
                startRinging();
            }
        }
    }

    private void startRinging() {
        stringeeAudioManager = StringeeAudioManager.create(InComingCallActivity.this);
        stringeeAudioManager.start(new StringeeAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StringeeAudioManager.AudioDevice audioDevice, Set<StringeeAudioManager.AudioDevice> set) {
            }
        });
        stringeeAudioManager.setSpeakerphoneOn(isVideo);

        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Stringee", "signalingState: " + signalingState);
                        mSignalingState = signalingState;
                        switch (signalingState) {
                            case ANSWERED:
                                activityInComingCallBinding.tvState.setText("Starting");
                                if (mMediaState == StringeeCall.MediaState.CONNECTED) {
                                    startCall();
                                }
                                break;
                            case ENDED:
                                activityInComingCallBinding.tvState.setText("Ended");
                                endCall(true, false);
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
                        Common.reportMessage(InComingCallActivity.this, s);
                    }
                });
            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (signalingState == StringeeCall.SignalingState.ANSWERED || signalingState == StringeeCall.SignalingState.BUSY) {
                            Common.reportMessage(InComingCallActivity.this, "This call is handled on another device.");
                            activityInComingCallBinding.tvState.setText("Ended");
                            endCall(true, false);
                        }
                    }
                });
            }

            @Override
            public void onMediaStateChange(StringeeCall stringeeCall, StringeeCall.MediaState mediaState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Stringee", "mediaState: " + mediaState);
                        mMediaState = mediaState;
                        if (mediaState == StringeeCall.MediaState.CONNECTED) {
                            if (mSignalingState == StringeeCall.SignalingState.ANSWERED) {
                                startCall();
                            }
                        } else {
                            activityInComingCallBinding.tvState.setText("Reconnecting...");
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
                            activityInComingCallBinding.flLocal.removeAllViews();
                            SurfaceView localView = stringeeCall.getLocalView();
                            activityInComingCallBinding.flLocal.addView(localView);
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
                            activityInComingCallBinding.flRemote.removeAllViews();
                            SurfaceView remoteView = stringeeCall.getRemoteView();
                            activityInComingCallBinding.flRemote.addView(remoteView);
                            stringeeCall.renderRemoteView(false);
                        }
                    }
                });
            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });

        stringeeCall.ringing(new StatusListener() {
            @Override
            public void onSuccess() {
                Log.d("Stringee", "send ringing success");
            }
        });
    }

    private void startCall() {
        activityInComingCallBinding.tvState.setText("Started");
    }

    private void endCall(boolean isHangup, boolean isReject) {
        if (isHangup) {
            if (stringeeCall != null) {
                stringeeCall.hangup();
            }
        }

        if (isReject) {
            if (stringeeCall != null) {
                stringeeCall.reject();
            }
        }

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

}