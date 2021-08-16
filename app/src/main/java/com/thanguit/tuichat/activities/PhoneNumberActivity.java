package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.thanguit.tuichat.R;
import com.thanguit.tuichat.databinding.ActivityPhoneNumberBinding;

public class PhoneNumberActivity extends AppCompatActivity {
    private ActivityPhoneNumberBinding activityPhoneNumberBinding;
    private static final String TAG = "PhoneNumberActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityPhoneNumberBinding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(activityPhoneNumberBinding.getRoot());

        initializeViews();
        listeners();
    }

    private void initializeViews() {
        activityPhoneNumberBinding.ccpCountryCodePicker.setDefaultCountryUsingNameCode("VN");
        activityPhoneNumberBinding.ccpCountryCodePicker.resetToDefaultCountry();
    }

    private void listeners() {
        activityPhoneNumberBinding.btnPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = activityPhoneNumberBinding.ccpCountryCodePicker.getSelectedCountryCodeWithPlus().trim();
                String phoneNumber = activityPhoneNumberBinding.edtPNumber.getText().toString().trim();
                String yourPhoneNumber = countryCode + phoneNumber;

                if (phoneNumber.isEmpty()) {
                    activityPhoneNumberBinding.edtPNumber.setError(getString(R.string.edtPNumberError));
                } else {
                    Log.d(TAG, "Your Phone Number: " + yourPhoneNumber.trim());

                    Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
                    intent.putExtra("PHONE_NUMBER", yourPhoneNumber);
                    startActivity(intent);
                }
            }
        });

    }
}