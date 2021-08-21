package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding activityLoginBinding;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        initializeViews();
        listeners();
    }

    private void initializeViews() {
        AnimationScale.getInstance().eventButton(this, activityLoginBinding.btnLoginPhoneNumber);
        AnimationScale.getInstance().eventButton(this, activityLoginBinding.btnLoginGoogle);
        AnimationScale.getInstance().eventButton(this, activityLoginBinding.btnLoginFacebook);
    }

    private void listeners() {
        activityLoginBinding.btnLoginPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PhoneNumberActivity.class);
                startActivity(intent);
            }
        });
    }
}