package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thanguit.tuichat.databinding.ActivityOutGoingCallBinding;

public class OutGoingCallActivity extends AppCompatActivity {
    private ActivityOutGoingCallBinding activityOutGoingCallBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOutGoingCallBinding = ActivityOutGoingCallBinding.inflate(getLayoutInflater());
        setContentView(activityOutGoingCallBinding.getRoot());
    }
}