package com.thanguit.tuichat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.adapters.ChatMessageAdapter;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.databinding.ActivityChatBinding;
import com.thanguit.tuichat.models.ChatMessage;
import com.thanguit.tuichat.models.User;
import com.thanguit.tuichat.utils.MyToast;
import com.thanguit.tuichat.utils.OpenSoftKeyboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding activityChatBinding;
    private static final String TAG = "ChatActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private AnimationScale animationScale;
    private OpenSoftKeyboard openSoftKeyboard;

    private String avatar;

    private String senderRoom;
    private String receiverRoom;

    private List<ChatMessage> chatMessageList;
    private ChatMessageAdapter chatMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        animationScale = AnimationScale.getInstance();
        openSoftKeyboard = OpenSoftKeyboard.getInstance();

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

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("USER")) {
                User user = new User();
                user = (User) intent.getParcelableExtra("USER");
                if (user != null) {
                    String receiverID = user.getUid().trim();
                    String name = user.getName().trim();
                    avatar = user.getAvatar().trim();

                    activityChatBinding.tvUserName.setText(name);
                    Picasso.get()
                            .load(avatar)
                            .placeholder(R.drawable.ic_user_avatar)
                            .error(R.drawable.ic_user_avatar)
                            .into(activityChatBinding.civAvatar);

                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        if (user.getUid().trim().equals(currentUser.getUid().trim())) {
                            activityChatBinding.ivVideoCall.setVisibility(View.GONE);
                            activityChatBinding.ivCall.setVisibility(View.GONE);
                        }
                        senderRoom = currentUser.getUid() + receiverID.trim();
                        receiverRoom = receiverID.trim() + currentUser.getUid();

                        chatMessageList = new ArrayList<>();
                        chatMessageAdapter = new ChatMessageAdapter(this, chatMessageList, user.getUid(), avatar, senderRoom, receiverRoom);
                        activityChatBinding.rvChatMessage.setLayoutManager(new LinearLayoutManager(this));
                        activityChatBinding.rvChatMessage.setAdapter(chatMessageAdapter);

                        firebaseDatabase.getReference()
                                .child("chats")
                                .child(senderRoom)
                                .child("messages")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        chatMessageList.clear();
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                                            chatMessage.setMessageID(dataSnapshot.getKey());
                                            chatMessageList.add(chatMessage);
                                        }
                                        chatMessageAdapter.notifyDataSetChanged();

                                        if (chatMessageAdapter.getItemCount() > 0) {
                                            activityChatBinding.rvChatMessage.smoothScrollToPosition(activityChatBinding.rvChatMessage.getAdapter().getItemCount() - 1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                        activityChatBinding.ivSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String message = activityChatBinding.edtChatMessage.getText().toString().trim();
                                if (message.isEmpty()) {
                                    activityChatBinding.edtChatMessage.setError(getString(R.string.edtChatMessage));
                                    openSoftKeyboard.openSoftKeyboard(ChatActivity.this, activityChatBinding.edtChatMessage);
                                } else {
                                    String randomID = firebaseDatabase.getReference().push().getKey(); // It mean key

                                    Date date = new Date();
                                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                    String time = dateFormat.format(date);

                                    activityChatBinding.edtChatMessage.setText("");
                                    ChatMessage chatMessage = new ChatMessage(currentUser.getUid().trim(), message, time);
                                    firebaseDatabase.getReference()
                                            .child("chats")
                                            .child(senderRoom.trim())
                                            .child("messages")
                                            .child(randomID)
                                            .setValue(chatMessage)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    MyToast.makeText(ChatActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                                }
                                            })
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    firebaseDatabase.getReference()
                                                            .child("chats")
                                                            .child(receiverRoom.trim())
                                                            .child("messages")
                                                            .child(randomID)
                                                            .setValue(chatMessage)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    activityChatBinding.rvChatMessage.smoothScrollToPosition(activityChatBinding.rvChatMessage.getAdapter().getItemCount() - 1);

                                                                    HashMap<String, Object> lastMessageObj = new HashMap<>();
                                                                    lastMessageObj.put("lastMessage", chatMessage.getMessage().trim());
                                                                    lastMessageObj.put("lastMessageTime", chatMessage.getTime().trim());
                                                                    firebaseDatabase.getReference().child("chats").child(senderRoom.trim()).updateChildren(lastMessageObj);
                                                                    firebaseDatabase.getReference().child("chats").child(receiverRoom.trim()).updateChildren(lastMessageObj);
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }
                        });
                    }
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
    }
}