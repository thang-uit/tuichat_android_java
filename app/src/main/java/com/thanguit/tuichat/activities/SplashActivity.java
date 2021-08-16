package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanguit.tuichat.R;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private static final int TIME_SPLASH = 4500;

    private Animation topAnimation;
    private Animation bottomAnimation;

    private ImageView imvLogo;
    private TextView tvDeveloper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        this.topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        this.bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        this.imvLogo = findViewById(R.id.imvLogo);
        this.tvDeveloper = findViewById(R.id.tvDeveloper);

        this.imvLogo.setAnimation(this.topAnimation);
        this.tvDeveloper.setAnimation(this.bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, PhoneNumberActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME_SPLASH);
    }
}