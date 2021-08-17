package com.thanguit.tuichat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.databinding.ActivityOtpactivityBinding;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    private ActivityOtpactivityBinding activityOtpactivityBinding;
    private static final String TAG = "OTPActivity";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityOtpactivityBinding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(activityOtpactivityBinding.getRoot());

        getIntentData();
        initializeViews();
        listeners();
    }

    private void initializeViews() {

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

                    firebaseAuth = FirebaseAuth.getInstance();
                    PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(yourPhoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {

                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
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


        activityOtpactivityBinding.btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkOTP() == 1) {
                    activityOtpactivityBinding.edtOTPCode1.setError(getString(R.string.edtOTPError));
                } else if (checkOTP() == 2) {
                    activityOtpactivityBinding.edtOTPCode2.setError(getString(R.string.edtOTPError));
                } else if (checkOTP() == 3) {
                    activityOtpactivityBinding.edtOTPCode3.setError(getString(R.string.edtOTPError));
                } else if (checkOTP() == 4) {
                    activityOtpactivityBinding.edtOTPCode4.setError(getString(R.string.edtOTPError));
                } else if (checkOTP() == 5) {
                    activityOtpactivityBinding.edtOTPCode5.setError(getString(R.string.edtOTPError));
                } else if (checkOTP() == 6) {
                    activityOtpactivityBinding.edtOTPCode6.setError(getString(R.string.edtOTPError));
                } else if (checkOTP() == 0) {
                    Toast.makeText(OTPActivity.this, "Bruh", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private int checkOTP() {
        if (activityOtpactivityBinding.edtOTPCode1.getText().toString().trim().isEmpty()) {
            return 1;
//            activityOtpactivityBinding.edtOTPCode1.setError(getString(R.string.edtOTPError));
        } else if (activityOtpactivityBinding.edtOTPCode2.getText().toString().trim().isEmpty()) {
            return 2;
//            activityOtpactivityBinding.edtOTPCode2.setError(getString(R.string.edtOTPError));
        } else if (activityOtpactivityBinding.edtOTPCode3.getText().toString().trim().isEmpty()) {
            return 3;
//            activityOtpactivityBinding.edtOTPCode3.setError(getString(R.string.edtOTPError));
        } else if (activityOtpactivityBinding.edtOTPCode4.getText().toString().trim().isEmpty()) {
            return 4;
//            activityOtpactivityBinding.edtOTPCode4.setError(getString(R.string.edtOTPError));
        } else if (activityOtpactivityBinding.edtOTPCode5.getText().toString().trim().isEmpty()) {
            return 5;
//            activityOtpactivityBinding.edtOTPCode5.setError(getString(R.string.edtOTPError));
        } else if (activityOtpactivityBinding.edtOTPCode6.getText().toString().trim().isEmpty()) {
            return 6;
//            activityOtpactivityBinding.edtOTPCode6.setError(getString(R.string.edtOTPError));
        } else {
            return 0;
        }
    }
}