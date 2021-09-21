package com.thanguit.tuichat.activities;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.adapters.ChatMessageAdapter;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.database.FirebaseManager;
import com.thanguit.tuichat.databinding.ActivityChatBinding;
import com.thanguit.tuichat.models.ChatMessage;
import com.thanguit.tuichat.models.User;
import com.thanguit.tuichat.utils.LoadingDialog;
import com.thanguit.tuichat.utils.MyToast;
import com.thanguit.tuichat.utils.OpenSoftKeyboard;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class ChatActivity extends AppCompat {
    private ActivityChatBinding activityChatBinding;
    private static final String TAG = "ChatActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseManager firebaseManager;

    private AnimationScale animationScale;
    private OpenSoftKeyboard openSoftKeyboard;
    private LoadingDialog loadingDialog;

    private User user = new User();
    private String avatar;

    private String senderRoom;
    private String receiverRoom;

    private List<ChatMessage> chatMessageList;
    private ChatMessageAdapter chatMessageAdapter;

    private static final String USER_CHAT_STORAGE = "USER_CHAT/";
    private static final String STATUS_DATABASE_ONLINE = "online";
    private static final String STATUS_DATABASE_OFFLINE = "offline";
    private static final String TYPING = "true";
    private static final String NOT_TYPING = "false";

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
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());

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
//        animationScale.eventImageView(this, activityChatBinding.ivBack);
//        animationScale.eventImageView(this, activityChatBinding.ivVideoCall);
//        animationScale.eventImageView(this, activityChatBinding.ivCall);
        animationScale.eventImageView(this, activityChatBinding.ivPicture);
        animationScale.eventImageView(this, activityChatBinding.ivSend);
        animationScale.eventLinearLayout(this, activityChatBinding.llToBottom);
        activityChatBinding.tvUserName.setSelected(true);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("USER")) {
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
                    firebaseDatabase.getReference().child("users/" + receiverID.trim() + "/status").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String status = snapshot.getValue(String.class);
                                if (status != null && !status.isEmpty()) {
                                    if (status.equals(STATUS_DATABASE_ONLINE.trim())) {
                                        activityChatBinding.ivStatusOnline.setVisibility(View.VISIBLE);
                                    } else if (status.equals(STATUS_DATABASE_OFFLINE.trim())) {
                                        activityChatBinding.ivStatusOnline.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        senderRoom = currentUser.getUid() + receiverID.trim();
                        receiverRoom = receiverID.trim() + currentUser.getUid();

                        if (user.getUid().trim().equals(currentUser.getUid().trim())) {
                            activityChatBinding.ibVideoCall.setVisibility(View.GONE);
                            activityChatBinding.ibCall.setVisibility(View.GONE);
                        } else {
                            firebaseDatabase.getReference().child("chats").child(senderRoom).child("friendTyping").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String isTyping = snapshot.getValue(String.class);

                                    if (isTyping != null && !isTyping.isEmpty()) {
                                        if (isTyping.equals(TYPING)) {
                                            activityChatBinding.lavAnimationTyping.setVisibility(View.VISIBLE);
                                        } else {
                                            activityChatBinding.lavAnimationTyping.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }

                        chatMessageList = new ArrayList<>();
                        chatMessageAdapter = new ChatMessageAdapter(this, chatMessageList, user.getUid(), avatar, senderRoom, receiverRoom);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                        linearLayoutManager.setStackFromEnd(true); // Scroll to end element in list
                        activityChatBinding.rvChatMessage.setLayoutManager(linearLayoutManager);
                        activityChatBinding.rvChatMessage.setAdapter(chatMessageAdapter);

                        firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages")
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
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                        activityChatBinding.rvChatMessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                if (dy > 0) {
                                    activityChatBinding.llToBottom.setVisibility(View.GONE);
                                } else if (dy < 0) {
                                    activityChatBinding.llToBottom.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                        firebaseDatabase.getReference().child("chats").child(senderRoom.trim()).child("lastMessage")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!user.getUid().trim().equals(currentUser.getUid().trim())) {
                                            activityChatBinding.llToBottom.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                        activityChatBinding.llToBottom.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                activityChatBinding.rvChatMessage.smoothScrollToPosition(activityChatBinding.rvChatMessage.getAdapter().getItemCount());
                                activityChatBinding.llToBottom.setVisibility(View.GONE);
                            }
                        });

                        activityChatBinding.ivSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String message = activityChatBinding.edtChatMessage.getText().toString().trim();
                                if (message.isEmpty()) {
                                    openSoftKeyboard.openSoftKeyboard(ChatActivity.this, activityChatBinding.edtChatMessage);
                                } else {
                                    String randomID = firebaseDatabase.getReference().push().getKey(); // It mean key

                                    Date date = new Date();
                                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                    String time = dateFormat.format(date);

                                    activityChatBinding.edtChatMessage.setText("");
                                    ChatMessage chatMessage = new ChatMessage(currentUser.getUid().trim(), message, "", time);
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
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    MyToast.makeText(ChatActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                                                }
                                                            })
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    activityChatBinding.rvChatMessage.smoothScrollToPosition(activityChatBinding.rvChatMessage.getAdapter().getItemCount());

                                                                    HashMap<String, Object> lastMessageObj = new HashMap<>();
                                                                    lastMessageObj.put("lastMessage", chatMessage.getMessage().trim());
                                                                    lastMessageObj.put("lastMessageTime", chatMessage.getTime().trim());
                                                                    firebaseDatabase.getReference().child("chats").child(senderRoom.trim()).updateChildren(lastMessageObj);
                                                                    firebaseDatabase.getReference().child("chats").child(receiverRoom.trim()).updateChildren(lastMessageObj);

                                                                    if (!user.getUid().trim().equals(currentUser.getUid().trim())) {
                                                                        firebaseManager.getUserName(currentUser.getUid().trim());
                                                                        firebaseManager.setReadUserName(new FirebaseManager.GetUserNameListener() {
                                                                            @Override
                                                                            public void getUserNameListener(String name) {
                                                                                sendNotification(user.getToken().trim(), name.trim(), message.trim());
                                                                            }
                                                                        });
                                                                    }
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
        activityChatBinding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityChatBinding.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        selectImageFromGallery();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        MyToast.makeText(ChatActivity.this, MyToast.WARNING, getString(R.string.toast7) + deniedPermissions.toString(), MyToast.SHORT).show();
                    }
                };

                TedPermission.with(ChatActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();
            }
        });

        activityChatBinding.ibVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoCallIntent = new Intent(ChatActivity.this, OutGoingCallActivity.class);
                videoCallIntent.putExtra("USER", user);
                videoCallIntent.putExtra("VIDEO_CALL", true);
                startActivity(videoCallIntent);
            }
        });

        activityChatBinding.ibCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(ChatActivity.this, OutGoingCallActivity.class);
                callIntent.putExtra("USER", user);
                callIntent.putExtra("VIDEO_CALL", false);
                startActivity(callIntent);
            }
        });

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            if (!user.getUid().trim().equals(currentUser.getUid().trim())) {
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, Object> isTyping = new HashMap<>();
                        isTyping.put("friendTyping", NOT_TYPING);
                        firebaseDatabase.getReference().child("chats").child(receiverRoom.trim()).updateChildren(isTyping);
                    }
                };
                activityChatBinding.edtChatMessage.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        HashMap<String, Object> isTyping = new HashMap<>();
                        isTyping.put("friendTyping", TYPING);
                        firebaseDatabase.getReference().child("chats").child(receiverRoom.trim()).updateChildren(isTyping);

                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(runnable, 1000);
                    }
                });
            }
        }
    }

    private void selectImageFromGallery() {
        TedBottomPicker.with(this).show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Uri uri) {
                if (uri != null) {
                    loadingDialog.startLoading(ChatActivity.this, false);

                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        Date date = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                        String time = dateFormat.format(date);

                        StorageReference storageReference = firebaseStorage.getReference().child(USER_CHAT_STORAGE.trim() + currentUser.getUid().trim() + "/" + senderRoom + "/" + time.trim());
                        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String image = uri.toString().trim();

                                            String message = "";
                                            String randomID = firebaseDatabase.getReference().push().getKey(); // It mean key

                                            ChatMessage chatMessage = new ChatMessage(currentUser.getUid().trim(), message, image, time);
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
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            MyToast.makeText(ChatActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                                                        }
                                                                    })
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            activityChatBinding.rvChatMessage.smoothScrollToPosition(activityChatBinding.rvChatMessage.getAdapter().getItemCount());

                                                                            HashMap<String, Object> lastMessageObj = new HashMap<>();
                                                                            lastMessageObj.put("lastMessage", "photo");
                                                                            lastMessageObj.put("lastMessageTime", chatMessage.getTime().trim());
                                                                            firebaseDatabase.getReference().child("chats").child(senderRoom.trim()).updateChildren(lastMessageObj);
                                                                            firebaseDatabase.getReference().child("chats").child(receiverRoom.trim()).updateChildren(lastMessageObj);

                                                                            if (!user.getUid().trim().equals(currentUser.getUid().trim())) {
                                                                                firebaseManager.getUserName(currentUser.getUid().trim());
                                                                                firebaseManager.setReadUserName(new FirebaseManager.GetUserNameListener() {
                                                                                    @Override
                                                                                    public void getUserNameListener(String name) {
                                                                                        sendNotification(user.getToken().trim(), name.trim(), "photo");
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                            loadingDialog.cancelLoading();
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
        });
    }

    private void sendNotification(String token, String name, String message) {
        // Document: https://firebase.google.com/docs/reference/fcmdata/rest
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", name.trim());
            data.put("body", message.trim());

            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", token.trim());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, notificationData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Authorization", "key=AAAAF0y1Ib4:APA91bEMO5OUyMax7T3PW0SvIT2Yfzug2lEoY0v8_rHOPZh0pftP1iJRXFdmi8loFI9VyRR1YncSaSTksIvfjTH7KFcFtjsNgBK81l0n2WLNcMg3eQqAov3BlQ97htJQP3s_vsryEXGj");
                    hashMap.put("Content_Type", "application/json");

                    return hashMap;
                }
            };
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
        }
    }
}