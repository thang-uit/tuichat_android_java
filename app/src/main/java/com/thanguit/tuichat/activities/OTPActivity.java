package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;

import com.thanguit.tuichat.R;

public class OTPActivity extends AppCompatActivity {
    private static final String TAG = "OTPActivity";

    private EditText edtOTPCode1, edtOTPCode2, edtOTPCode3, edtOTPCode4, edtOTPCode5, edtOTPCode6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otpactivity);

        initializeViews();
        listeners();
    }

    private void initializeViews() {
        this.edtOTPCode1 = findViewById(R.id.edtOTPCode1);
        this.edtOTPCode2 = findViewById(R.id.edtOTPCode2);
        this.edtOTPCode3 = findViewById(R.id.edtOTPCode3);
        this.edtOTPCode4 = findViewById(R.id.edtOTPCode4);
        this.edtOTPCode5 = findViewById(R.id.edtOTPCode5);
        this.edtOTPCode6 = findViewById(R.id.edtOTPCode6);
    }

    private void listeners() {
        numberOTPNext();
    }

    private void numberOTPNext() {
        this.edtOTPCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTPCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        this.edtOTPCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTPCode3.requestFocus();
                } else {
                    edtOTPCode1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        this.edtOTPCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTPCode4.requestFocus();
                } else {
                    edtOTPCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        this.edtOTPCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTPCode5.requestFocus();
                } else {
                    edtOTPCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        this.edtOTPCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTPCode6.requestFocus();
                } else {
                    edtOTPCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        this.edtOTPCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    edtOTPCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}