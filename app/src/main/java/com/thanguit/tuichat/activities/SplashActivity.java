package com.thanguit.tuichat.activities;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.thanguit.tuichat.R;
import com.thanguit.tuichat.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompat {
    private ActivitySplashBinding activitySplashBinding;
    private static final String TAG = "SplashActivity";

    private static final int TIME_SPLASH = 4500;

    private Animation topAnimation;
    private Animation bottomAnimation;

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
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_main_2));
        }
        activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(activitySplashBinding.getRoot());

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.d("KEY", "printHashKey() Hash Key: " + hashKey);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("Key", "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e("Key", "printHashKey()", e);
//        }

        this.topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        this.bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        activitySplashBinding.imvLogo.setAnimation(this.topAnimation);
        activitySplashBinding.tvDeveloper.setAnimation(this.bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME_SPLASH);
    }
}