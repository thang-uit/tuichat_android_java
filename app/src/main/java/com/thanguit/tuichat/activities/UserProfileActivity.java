package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.database.FirebaseManager;
import com.thanguit.tuichat.databinding.ActivityUserProfileBinding;
import com.thanguit.tuichat.utils.LoadingDialog;
import com.thanguit.tuichat.utils.OptionDialog;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding activityUserProfileBinding;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseManager firebaseManager;

    private AnimationScale animationScale;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_main_1));
        }
        activityUserProfileBinding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(activityUserProfileBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseManager = FirebaseManager.getInstance();
        animationScale = AnimationScale.getInstance();
        loadingDialog = LoadingDialog.getInstance();

        initializeViews();
        listeners();
    }

    private void initializeViews() {
        animationScale.eventButton(this, activityUserProfileBinding.btnLogout);
    }

    private void listeners() {
        activityUserProfileBinding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityUserProfileBinding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogoutDialog();
            }
        });
    }

    private void openLogoutDialog() {
        OptionDialog logoutDialog = new OptionDialog(this,
                getResources().getString(R.string.btnDialog23).trim(),
                getResources().getString(R.string.tvDialogContent3).trim(),
                getResources().getString(R.string.btnDialog11).trim(),
                getResources().getString(R.string.btnDialog23).trim(), true,
                new OptionDialog.SetActionButtonListener() {
                    @Override
                    public void setNegativeButtonListener(Dialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void setPositiveButtonListener(Dialog dialog) {
                        dialog.dismiss();
                        firebaseAuth.signOut();
                        moveTaskToBack(true);
                        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
        logoutDialog.show();
    }
}