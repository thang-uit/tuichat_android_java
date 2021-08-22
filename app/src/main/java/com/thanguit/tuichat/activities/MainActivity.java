package com.thanguit.tuichat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.adapters.ViewPagerAdapter;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.animations.ZoomOutPageTransformer;
import com.thanguit.tuichat.database.FirebaseManager;
import com.thanguit.tuichat.databinding.ActivityMainBinding;
import com.thanguit.tuichat.databinding.ActivityPhoneNumberBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    private static final String TAG = "MainActivity";

    private FirebaseManager firebaseManager;

    private ViewPagerAdapter viewPagerAdapter;
    private FirebaseAuth firebaseAuth;

    private AnimationScale animationScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int startColor = getWindow().getStatusBarColor();
            int endColor = ContextCompat.getColor(this, R.color.color_5);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        }
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        firebaseManager = FirebaseManager.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();
        listeners();
    }

    private void initializeViews() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        activityMainBinding.vp2ViewPager2.setAdapter(viewPagerAdapter);
        activityMainBinding.vp2ViewPager2.setCurrentItem(0); // Set default fragment
        activityMainBinding.vp2ViewPager2.setOffscreenPageLimit(2);
        activityMainBinding.vp2ViewPager2.setPageTransformer(new ZoomOutPageTransformer()); // Set animation change page

//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser != null) {
//            Picasso.get()
//                    .load(firebaseManager.getUserAvatar(currentUser.getUid()))
//                    .placeholder(R.drawable.ic_logo)
//                    .error(R.drawable.ic_logo)
//                    .into(activityMainBinding.civAvatar);
//        }

    }

    private void listeners() {
        activityMainBinding.bnvNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menuActionChat) {
                    activityMainBinding.vp2ViewPager2.setCurrentItem(0);
                } else if (item.getItemId() == R.id.menuActionPeople) {
                    activityMainBinding.vp2ViewPager2.setCurrentItem(1);
                } else if (item.getItemId() == R.id.menuActionSetting) {
                    activityMainBinding.vp2ViewPager2.setCurrentItem(2);
                }
                return false;
            }
        });

        activityMainBinding.vp2ViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position) {
                    case 0: {
                        activityMainBinding.bnvNav.getMenu().findItem(R.id.menuActionChat).setChecked(true);
                        break;
                    }
                    case 1: {
                        activityMainBinding.bnvNav.getMenu().findItem(R.id.menuActionPeople).setChecked(true);
                        break;
                    }
                    case 2: {
                        activityMainBinding.bnvNav.getMenu().findItem(R.id.menuActionSetting).setChecked(true);
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
}