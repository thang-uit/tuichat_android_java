package com.thanguit.tuichat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.utils.LoadingDialog;
import com.thanguit.tuichat.utils.MyToast;
import com.thanguit.tuichat.utils.OpenSoftKeyboard;
import com.thanguit.tuichat.databinding.ActivityOtpactivityBinding;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    private ActivityOtpactivityBinding activityOtpactivityBinding;
    private static final String TAG = "OTPActivity";

    private FirebaseAuth firebaseAuth;
    private String verificationID;

    private AnimationScale animationScale;
    private LoadingDialog loadingDialog;
    private OpenSoftKeyboard openSoftKeyboard;

    private static final String CODE_1 = "CODE_1";
    private static final String CODE_2 = "CODE_2";
    private static final String CODE_3 = "CODE_3";
    private static final String CODE_4 = "CODE_4";
    private static final String CODE_5 = "CODE_5";
    private static final String CODE_6 = "CODE_6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOtpactivityBinding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(activityOtpactivityBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        animationScale = AnimationScale.getInstance();
        loadingDialog = LoadingDialog.getInstance();
        openSoftKeyboard = OpenSoftKeyboard.getInstance();

        getIntentData();
        initializeViews();
        listeners();
    }

    private void initializeViews() {
        animationScale.eventButton(this, activityOtpactivityBinding.btnVerifyOTP);

        loadingDialog.startLoading(OTPActivity.this, false);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("PHONE_NUMBER")) {
                String yourPhoneNumber = intent.getStringExtra("PHONE_NUMBER");
                if (yourPhoneNumber.trim().isEmpty()) {
                    activityOtpactivityBinding.tvYourPhoneNumber.setText(getString(R.string.tvErrorPhoneNumber));
                } else {
                    Log.d(TAG, "Your Phone Number: " + yourPhoneNumber.trim());
                    activityOtpactivityBinding.tvYourPhoneNumber.setText(yourPhoneNumber);

                    PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(yourPhoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    // This callback will be invoked in two situations:
                                    // 1 - Instant verification. In some cases the phone number can be instantly
                                    //     verified without needing to send or enter a verification code.
                                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                    //     detect the incoming verification SMS and perform verification without
                                    //     user action.
                                    Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential);
//                                    signInWithPhoneAuthCredential(credential);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    // This callback is invoked in an invalid request for verification is made,
                                    // for instance if the the phone number format is not valid.
                                    Log.d(TAG, "onVerificationFailed", e);

                                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                        MyToast.makeText(OTPActivity.this, MyToast.ERROR, getString(R.string.toast2), MyToast.SHORT).show();
                                    } else if (e instanceof FirebaseTooManyRequestsException) {
                                        MyToast.makeText(OTPActivity.this, MyToast.ERROR, getString(R.string.toast1), MyToast.SHORT).show();
                                    } else {
                                        MyToast.makeText(OTPActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                    }
                                    finish();
                                }

                                @Override
                                public void onCodeSent(@NonNull String code, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(code, forceResendingToken);
                                    // The SMS verification code has been sent to the provided phone number, we
                                    // now need to ask the user to enter the code and then construct a credential
                                    // by combining the code with a verification ID.
                                    Log.d(TAG, "onCodeSent: " + code);

                                    loadingDialog.cancelLoading();
                                    verificationID = code;
                                }
                            })
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
                }
            }
        }
    }

    private void listeners() {
        numberOTPNext();

        activityOtpactivityBinding.btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String OTP = checkOTP();
                if (OTP.equals(CODE_1)) {
                    activityOtpactivityBinding.edtOTPCode1.setError(getString(R.string.edtOTPError));
                    openSoftKeyboard.openSoftKeyboard(OTPActivity.this, activityOtpactivityBinding.edtOTPCode1);
                } else if (OTP.equals(CODE_2)) {
                    activityOtpactivityBinding.edtOTPCode2.setError(getString(R.string.edtOTPError));
                    openSoftKeyboard.openSoftKeyboard(OTPActivity.this, activityOtpactivityBinding.edtOTPCode2);
                } else if (OTP.equals(CODE_3)) {
                    activityOtpactivityBinding.edtOTPCode3.setError(getString(R.string.edtOTPError));
                    openSoftKeyboard.openSoftKeyboard(OTPActivity.this, activityOtpactivityBinding.edtOTPCode3);
                } else if (OTP.equals(CODE_4)) {
                    activityOtpactivityBinding.edtOTPCode4.setError(getString(R.string.edtOTPError));
                    openSoftKeyboard.openSoftKeyboard(OTPActivity.this, activityOtpactivityBinding.edtOTPCode4);
                } else if (OTP.equals(CODE_5)) {
                    activityOtpactivityBinding.edtOTPCode5.setError(getString(R.string.edtOTPError));
                    openSoftKeyboard.openSoftKeyboard(OTPActivity.this, activityOtpactivityBinding.edtOTPCode5);
                } else if (OTP.equals(CODE_6)) {
                    activityOtpactivityBinding.edtOTPCode6.setError(getString(R.string.edtOTPError));
                    openSoftKeyboard.openSoftKeyboard(OTPActivity.this, activityOtpactivityBinding.edtOTPCode6);
                } else {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID, OTP);
                    firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");

                                Intent intent = new Intent(OTPActivity.this, SetupProfileActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                Log.d(TAG, "signInWithCredential:failure", task.getException());

                                MyToast.makeText(OTPActivity.this, MyToast.ERROR, getString(R.string.toast3), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void numberOTPNext() {
        activityOtpactivityBinding.edtOTPCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    activityOtpactivityBinding.edtOTPCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        activityOtpactivityBinding.edtOTPCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    activityOtpactivityBinding.edtOTPCode3.requestFocus();
                } else {
                    activityOtpactivityBinding.edtOTPCode1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        activityOtpactivityBinding.edtOTPCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    activityOtpactivityBinding.edtOTPCode4.requestFocus();
                } else {
                    activityOtpactivityBinding.edtOTPCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        activityOtpactivityBinding.edtOTPCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    activityOtpactivityBinding.edtOTPCode5.requestFocus();
                } else {
                    activityOtpactivityBinding.edtOTPCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        activityOtpactivityBinding.edtOTPCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    activityOtpactivityBinding.edtOTPCode6.requestFocus();
                } else {
                    activityOtpactivityBinding.edtOTPCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        activityOtpactivityBinding.edtOTPCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    activityOtpactivityBinding.edtOTPCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private String checkOTP() {
        String OTP = activityOtpactivityBinding.edtOTPCode1.getText().toString().trim() +
                activityOtpactivityBinding.edtOTPCode2.getText().toString().trim() +
                activityOtpactivityBinding.edtOTPCode3.getText().toString().trim() +
                activityOtpactivityBinding.edtOTPCode4.getText().toString().trim() +
                activityOtpactivityBinding.edtOTPCode5.getText().toString().trim() +
                activityOtpactivityBinding.edtOTPCode6.getText().toString().trim();

        if (activityOtpactivityBinding.edtOTPCode1.getText().toString().trim().isEmpty()) {
            return CODE_1;
        } else if (activityOtpactivityBinding.edtOTPCode2.getText().toString().trim().isEmpty()) {
            return CODE_2;
        } else if (activityOtpactivityBinding.edtOTPCode3.getText().toString().trim().isEmpty()) {
            return CODE_3;
        } else if (activityOtpactivityBinding.edtOTPCode4.getText().toString().trim().isEmpty()) {
            return CODE_4;
        } else if (activityOtpactivityBinding.edtOTPCode5.getText().toString().trim().isEmpty()) {
            return CODE_5;
        } else if (activityOtpactivityBinding.edtOTPCode6.getText().toString().trim().isEmpty()) {
            return CODE_6;
        } else {
            return OTP.trim();
        }
    }

}