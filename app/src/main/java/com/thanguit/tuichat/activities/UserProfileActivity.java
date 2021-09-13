package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.database.FirebaseManager;
import com.thanguit.tuichat.databinding.ActivityUserProfileBinding;
import com.thanguit.tuichat.databinding.LayoutTextviewDialogBinding;
import com.thanguit.tuichat.utils.LoadingDialog;
import com.thanguit.tuichat.utils.OpenSoftKeyboard;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding activityUserProfileBinding;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseManager firebaseManager;

    private AnimationScale animationScale;
    private OpenSoftKeyboard openSoftKeyboard;
    private LoadingDialog loadingDialog;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int startColor = getWindow().getStatusBarColor();
            int endColor = ContextCompat.getColor(this, R.color.color_main_1);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        }
        activityUserProfileBinding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(activityUserProfileBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseManager = FirebaseManager.getInstance();
        animationScale = AnimationScale.getInstance();
        openSoftKeyboard = OpenSoftKeyboard.getInstance();
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
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        LayoutTextviewDialogBinding layoutTextviewDialogBinding = LayoutTextviewDialogBinding.inflate(layoutInflater);
        dialog.setContentView(R.layout.layout_textview_dialog);

        Window window = (Window) dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);

        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDialogContent = dialog.findViewById(R.id.tvDialogContent);
        Button btnDialogAction = dialog.findViewById(R.id.btnDialogAction);
        Button btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);

        animationScale.eventButton(this, btnDialogCancel);
        animationScale.eventButton(this, btnDialogAction);

        tvDialogTitle.setText(getResources().getString(R.string.btnDialog23));
        tvDialogContent.setText(getResources().getString(R.string.tvDialogContent3));
        btnDialogCancel.setText(getResources().getString(R.string.btnDialog11).trim());
        btnDialogAction.setText(getResources().getString(R.string.btnDialog23).trim());

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnDialogAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                firebaseAuth.signOut();
                moveTaskToBack(true);
                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        dialog.show();
    }
}