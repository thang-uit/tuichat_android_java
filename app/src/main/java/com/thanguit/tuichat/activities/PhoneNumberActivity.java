package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.thanguit.tuichat.R;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.databinding.ActivityPhoneNumberBinding;
import com.thanguit.tuichat.utils.MyToast;
import com.thanguit.tuichat.utils.OpenSoftKeyboard;

import java.util.Random;

public class PhoneNumberActivity extends AppCompatActivity {
    private ActivityPhoneNumberBinding activityPhoneNumberBinding;
    private static final String TAG = "PhoneNumberActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPhoneNumberBinding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(activityPhoneNumberBinding.getRoot());

        initializeViews();
        listeners();
    }

    private void initializeViews() {
        activityPhoneNumberBinding.ccpCountryCodePicker.setDefaultCountryUsingNameCode("VN");
        activityPhoneNumberBinding.ccpCountryCodePicker.resetToDefaultCountry();

        activityPhoneNumberBinding.edtPNumber.requestFocus();
    }

    private void listeners() {
        AnimationScale.getInstance().eventButton(this, activityPhoneNumberBinding.btnPhoneNumber);
        activityPhoneNumberBinding.btnPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = activityPhoneNumberBinding.ccpCountryCodePicker.getSelectedCountryCodeWithPlus().trim();
                String phoneNumber = activityPhoneNumberBinding.edtPNumber.getText().toString().trim();
                String yourPhoneNumber = countryCode + phoneNumber;

                int type = new Random().nextInt(4) + 1;
                MyToast.makeText(PhoneNumberActivity.this, type, "This is content. This is content. This is content.", MyToast.SHORT).show();

//                if (phoneNumber.isEmpty()) {
//                    activityPhoneNumberBinding.edtPNumber.setError(getString(R.string.edtPNumberError));
//                    OpenSoftKeyboard.getInstance().openSoftKeyboard(PhoneNumberActivity.this, activityPhoneNumberBinding.edtPNumber);
//                } else {
//                    Log.d(TAG, "Your Phone Number: " + yourPhoneNumber.trim());
//
//                    Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
//                    intent.putExtra("PHONE_NUMBER", yourPhoneNumber.trim());
//                    startActivity(intent);
//                }
            }
        });

    }
}