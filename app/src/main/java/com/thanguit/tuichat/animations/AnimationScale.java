package com.thanguit.tuichat.animations;

import android.content.Context;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.thanguit.tuichat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnimationScale {
    private static AnimationScale instance;
    private Animation scaleUpAnimation, scaleDownAnimation;

    private AnimationScale() {
    }

    public static AnimationScale getInstance() {
        if (instance == null) {
            synchronized (AnimationScale.class) {
                if (instance == null) {
                    instance = new AnimationScale();
                }
            }
        }
        return instance;
    }

    public void eventButton(Context context, Button button) {
        this.scaleUpAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        this.scaleDownAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_down);

        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                button.startAnimation(scaleDownAnimation);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                button.startAnimation(scaleUpAnimation);
            }
            return false;
        });
    }

    public void eventImageView(Context context, ImageView imageView) {
        this.scaleUpAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        this.scaleDownAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_down);

        imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                imageView.startAnimation(scaleDownAnimation);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                imageView.startAnimation(scaleUpAnimation);
            }
            return false;
        });
    }

    public void eventCircleImageView(Context context, CircleImageView circleImageView) {
        this.scaleUpAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        this.scaleDownAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_down);

        circleImageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                circleImageView.startAnimation(scaleDownAnimation);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                circleImageView.startAnimation(scaleUpAnimation);
            }
            return false;
        });
    }
}
