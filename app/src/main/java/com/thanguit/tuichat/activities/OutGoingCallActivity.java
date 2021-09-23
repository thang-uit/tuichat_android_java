package com.thanguit.tuichat.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.databinding.ActivityOutGoingCallBinding;

public class OutGoingCallActivity extends AppCompat {
    private ActivityOutGoingCallBinding activityOutGoingCallBinding;

    private AnimationScale animationScale;

//    private StringeeCall stringeeCall;
//    private StringeeAudioManager stringeeAudioManager;
//
//    private String to;
//    private boolean isVideoCall;
//    private boolean isMute = false;
//    private boolean isSpeaker = false;
//    private boolean isVideo = false;
//
//    private StringeeCall.MediaState mMediaState;
//    private StringeeCall.SignalingState mSignalingState;
//
//    private KeyguardManager.KeyguardLock lock;
//
//    public static final int REQUEST_PERMISSION_CALL = 1;
//
//    private User user = new User();

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

        animationScale = AnimationScale.getInstance();

//        lock = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).newKeyguardLock(Context.KEYGUARD_SERVICE);
//        lock.disableKeyguard();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        initializeViews();
//        listeners();
    }

    private void initializeViews() {
        animationScale.eventImageButton(this, activityOutGoingCallBinding.ibEndCall);
        animationScale.eventImageButton(this, activityOutGoingCallBinding.ibSpeaker);
        animationScale.eventImageButton(this, activityOutGoingCallBinding.ibCamera);
        animationScale.eventImageButton(this, activityOutGoingCallBinding.ibMicrophone);

//        Common.isInCall = true;
//
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        if (bundle != null) {
//            user = (User) bundle.getParcelable("USER");
//            if (user != null) {
//                to = user.getUid().trim();
//            }
//            isVideoCall = bundle.getBoolean("VIDEO_CALL", false);
//        }
//
//        isSpeaker = isVideoCall;
//        activityOutGoingCallBinding.ibSpeaker.setImageResource(isSpeaker ? R.drawable.ic_speaker_on : R.drawable.ic_speaker_off);
//
//        isVideo = isVideoCall;
//        activityOutGoingCallBinding.ibCamera.setImageResource(isVideo ? R.drawable.ic_video_camera_on : R.drawable.ic_video_camera_off);
//
//        activityOutGoingCallBinding.flCamera.setVisibility(isVideo ? View.VISIBLE : View.GONE);
//        activityOutGoingCallBinding.btnSwitch.setVisibility(isVideo ? View.VISIBLE : View.GONE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            List<String> lstPermissions = new ArrayList<>();
//
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.RECORD_AUDIO)
//                    != PackageManager.PERMISSION_GRANTED) {
//                lstPermissions.add(Manifest.permission.RECORD_AUDIO);
//            }
//
//            if (isVideoCall) {
//                if (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    lstPermissions.add(Manifest.permission.CAMERA);
//                }
//            }
//
//            if (lstPermissions.size() > 0) {
//                String[] permissions = new String[lstPermissions.size()];
//                for (int i = 0; i < lstPermissions.size(); i++) {
//                    permissions[i] = lstPermissions.get(i);
//                }
//                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CALL);
//                return;
//            }
//        }
//
//        //create audio manager to control audio device
//        stringeeAudioManager = StringeeAudioManager.create(OutGoingCallActivity.this);
//        stringeeAudioManager.start(new StringeeAudioManager.AudioManagerEvents() {
//            @Override
//            public void onAudioDeviceChanged(StringeeAudioManager.AudioDevice audioDevice, Set<StringeeAudioManager.AudioDevice> set) {
//            }
//        });
//        stringeeAudioManager.setSpeakerphoneOn(isVideoCall);
//        makeCall();
    }

//    private void listeners() {
//        activityOutGoingCallBinding.ibMicrophone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isMute = !isMute;
//                activityOutGoingCallBinding.ibMicrophone.setImageResource(isMute ? R.drawable.ic_microphone_off : R.drawable.ic_microphone_on);
//                if (stringeeCall != null) {
//                    stringeeCall.mute(isMute);
//                }
//            }
//        });
//
//        activityOutGoingCallBinding.ibSpeaker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isSpeaker = !isSpeaker;
//                activityOutGoingCallBinding.ibSpeaker.setImageResource(isSpeaker ? R.drawable.ic_speaker_on : R.drawable.ic_speaker_off);
//                if (stringeeAudioManager != null) {
//                    stringeeAudioManager.setSpeakerphoneOn(isSpeaker);
//                }
//            }
//        });
//
//        activityOutGoingCallBinding.ibEndCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                activityOutGoingCallBinding.tvState.setText(getString(R.string.stringeeState2));
//                endCall();
//            }
//        });
//
//        activityOutGoingCallBinding.ibCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PermissionListener permissionlistener = new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted() {
//                        isVideo = !isVideo;
//                        activityOutGoingCallBinding.ibCamera.setImageResource(isVideo ? R.drawable.ic_video_camera_on : R.drawable.ic_video_camera_off);
//                        if (stringeeCall != null) {
//                            stringeeCall.enableVideo(isVideo);
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionDenied(List<String> deniedPermissions) {
//                        MyToast.makeText(OutGoingCallActivity.this, MyToast.WARNING, getString(R.string.toast7) + deniedPermissions.toString(), MyToast.SHORT).show();
//                    }
//                };
//
//                TedPermission.with(OutGoingCallActivity.this)
//                        .setPermissionListener(permissionlistener)
//                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//                        .setPermissions(Manifest.permission.CAMERA)
//                        .check();
//            }
//        });
//
//        activityOutGoingCallBinding.btnSwitch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (stringeeCall != null) {
//                    stringeeCall.switchCamera(new StatusListener() {
//                        @Override
//                        public void onSuccess() {
//                        }
//                    });
//                }
//            }
//        });
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        boolean isGranted = false;
//        if (grantResults.length > 0) {
//            for (int grantResult : grantResults) {
//                if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                    isGranted = false;
//                    break;
//                } else {
//                    isGranted = true;
//                }
//            }
//        }
//        if (requestCode == REQUEST_PERMISSION_CALL) {
//            if (!isGranted) {
//                finish();
//            } else {
//                makeCall();
//            }
//        }
//    }

//    private void makeCall() {
//        stringeeCall = new StringeeCall(MainActivity.stringeeClient, MainActivity.stringeeClient.getUserId(), to);
//        stringeeCall.setVideoCall(isVideoCall);
//
//        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
//            @Override
//            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
//                Log.e("Stringee", "======== Custom data: " + stringeeCall.getCustomDataFromYourServer());
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mSignalingState = signalingState;
//                        switch (signalingState) {
//                            case CALLING:
//                                activityOutGoingCallBinding.tvState.setText(getString(R.string.stringeeState4));
//                                break;
//                            case RINGING:
//                                activityOutGoingCallBinding.tvState.setText(getString(R.string.stringeeState5));
//                                break;
//                            case ANSWERED:
//                                activityOutGoingCallBinding.tvState.setText(getString(R.string.stringeeState6));
//                                if (mMediaState == StringeeCall.MediaState.CONNECTED) {
//                                    activityOutGoingCallBinding.tvState.setText(getString(R.string.stringeeState1));
//                                }
//                                break;
//                            case BUSY:
//                                activityOutGoingCallBinding.tvState.setText(getString(R.string.stringeeState7));
//                                endCall();
//                            case ENDED:
//                                activityOutGoingCallBinding.tvState.setText(getString(R.string.stringeeState2));
//                                endCall();
//                                break;
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onError(StringeeCall stringeeCall, int i, String s) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Common.reportMessage(OutGoingCallActivity.this, s);
//                    }
//                });
//            }
//
//            @Override
//            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s) {
//            }
//
//            @Override
//            public void onMediaStateChange(StringeeCall stringeeCall, StringeeCall.MediaState mediaState) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mMediaState = mediaState;
//                        if (mediaState == StringeeCall.MediaState.CONNECTED) {
//                            if (mSignalingState == StringeeCall.SignalingState.ANSWERED) {
//                                activityOutGoingCallBinding.tvState.setText(getString(R.string.stringeeState1));
//                            }
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onLocalStream(StringeeCall stringeeCall) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (stringeeCall.isVideoCall()) {
//                            activityOutGoingCallBinding.flLocal.removeAllViews();
//                            activityOutGoingCallBinding.flLocal.addView(stringeeCall.getLocalView());
//                            stringeeCall.renderLocalView(true);
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onRemoteStream(StringeeCall stringeeCall) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (stringeeCall.isVideoCall()) {
//                            activityOutGoingCallBinding.flRemote.removeAllViews();
//                            activityOutGoingCallBinding.flRemote.addView(stringeeCall.getRemoteView());
//                            stringeeCall.renderRemoteView(false);
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {
//            }
//        });
//
//        stringeeCall.makeCall();
//    }

//    private void endCall() {
//        stringeeCall.hangup();
//        if (stringeeAudioManager != null) {
//            stringeeAudioManager.stop();
//            stringeeAudioManager = null;
//        }
//        Common.postDelay(new Runnable() {
//            @Override
//            public void run() {
//                Common.isInCall = false;
//                finish();
//            }
//        }, 1000);
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
////        showDialog();
//    }
//
//    private void showDialog() {
//        OptionDialog optionDialog = new OptionDialog(this, getString(R.string.tvDialogTitle1), getString(R.string.tvDialogContent4),
//                getString(R.string.btnDialog12), getString(R.string.btnDialog24), true,
//                new OptionDialog.SetActionButtonListener() {
//                    @Override
//                    public void setNegativeButtonListener(Dialog dialog) {
//                        dialog.dismiss();
//                    }
//
//                    @Override
//                    public void setPositiveButtonListener(Dialog dialog) {
//                        endCall();
//                        dialog.dismiss();
//                    }
//                });
//        optionDialog.show();
//    }
}