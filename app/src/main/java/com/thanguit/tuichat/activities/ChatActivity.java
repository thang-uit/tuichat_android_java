package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thanguit.tuichat.R;
import com.thanguit.tuichat.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding activityChatBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());

        initializeViews();
        listeners();
    }

    private void initializeViews() {

    }

    private void listeners() {

    }
}