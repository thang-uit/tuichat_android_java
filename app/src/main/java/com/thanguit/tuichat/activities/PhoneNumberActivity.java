package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;
import com.thanguit.tuichat.R;

public class PhoneNumberActivity extends AppCompatActivity {
    private static final String TAG = "PhoneNumberActivity";

    private CountryCodePicker ccpCountryCodePicker;
    private EditText edtPNumber;
    private Button btnPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_phone_number);

        initializeViews();
        listeners();
    }

    private void initializeViews() {
        this.ccpCountryCodePicker = findViewById(R.id.ccpCountryCodePicker);
        this.edtPNumber = findViewById(R.id.edtPNumber);
        this.btnPhoneNumber = findViewById(R.id.btnPhoneNumber);
    }

    private void listeners() {
        this.btnPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
                startActivity(intent);
            }
        });
    }
}