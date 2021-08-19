package com.thanguit.tuichat.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.databinding.ActivitySetupProfileBinding;

public class SetupProfileActivity extends AppCompatActivity {
    private ActivitySetupProfileBinding activitySetupProfileBinding;
    private static final String TAG = "SetupProfileActivity";

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySetupProfileBinding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(activitySetupProfileBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();
        listeners();
    }

    private void initializeViews() {
    }

    private void listeners() {
        AnimationScale.getInstance().eventCircleImageView(this, activitySetupProfileBinding.civUserAvatar);
        activitySetupProfileBinding.civUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        AnimationScale.getInstance().eventButton(this, activitySetupProfileBinding.btnSetupProfile);
        activitySetupProfileBinding.btnSetupProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(data.getData() != null){
                activitySetupProfileBinding.civUserAvatar.setImageURI(data.getData());
            }
        }
    }
}