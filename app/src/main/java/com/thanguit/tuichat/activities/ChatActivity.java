package com.thanguit.tuichat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.adapters.ChatMessageAdapter;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.databinding.ActivityChatBinding;
import com.thanguit.tuichat.models.ChatMessage;
import com.thanguit.tuichat.models.User;
import com.thanguit.tuichat.utils.OpenSoftKeyboard;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding activityChatBinding;
    private static final String TAG = "ChatActivity";

    private AnimationScale animationScale;
    private OpenSoftKeyboard openSoftKeyboard;

    private String avatar;

    private List<ChatMessage> chatMessageList;
    private ChatMessageAdapter chatMessageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());

        animationScale = AnimationScale.getInstance();
        openSoftKeyboard = OpenSoftKeyboard.getInstance();

        getDataIntent();
        initializeViews();
        listeners();
    }


    private void initializeViews() {
        animationScale.eventImageView(this, activityChatBinding.ivBack);
        animationScale.eventImageView(this, activityChatBinding.ivVideoCall);
        animationScale.eventImageView(this, activityChatBinding.ivCall);
        animationScale.eventImageView(this, activityChatBinding.ivPicture);
        animationScale.eventImageView(this, activityChatBinding.ivSend);
        activityChatBinding.tvUserName.setSelected(true);
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("USER")) {
                User user = new User();
                user = (User) intent.getParcelableExtra("USER");
                if (user != null) {
                    String name = user.getName().trim();
                    avatar = user.getAvatar().trim();

                    activityChatBinding.tvUserName.setText(name);
                    Picasso.get()
                            .load(avatar)
                            .placeholder(R.drawable.ic_user_avatar)
                            .error(R.drawable.ic_user_avatar)
                            .into(activityChatBinding.civAvatar);
                }
            }
        }
    }

    private void listeners() {
        activityChatBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityChatBinding.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityChatBinding.edtChatMessage.getText().toString().trim().isEmpty()) {
                    activityChatBinding.edtChatMessage.setError(getString(R.string.edtChatMessage));
                    openSoftKeyboard.openSoftKeyboard(ChatActivity.this, activityChatBinding.edtChatMessage);
                }
            }
        });
    }
}