package com.thanguit.tuichat.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.databinding.ItemChatMessageReceiveBinding;
import com.thanguit.tuichat.databinding.ItemChatMessageSendBinding;
import com.thanguit.tuichat.models.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ChatMessageAdapter";

    private FirebaseAuth firebaseAuth;

    private Context context;
    private List<ChatMessage> chatMessageList;
    private String uid;
    private String avatar;
    private String senderRoom;
    private String receiverRoom;

    private final int ITEM_SEND = 1;
    private final int ITEM_RECEIVE = 2;

    private FirebaseDatabase firebaseDatabase;

    int[] emoticon = new int[]{
            R.drawable.ic_love,
            R.drawable.ic_laughing,
            R.drawable.ic_crying,
            R.drawable.ic_angry,
            R.drawable.ic_surprised,
            R.drawable.ic_cool
    };

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessageList, String uid, String avatar, String senderRoom, String receiverRoom) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.uid = uid;
        this.avatar = avatar;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_send, parent, false);
            return new SendViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_receive, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getUid().trim().equals(chatMessageList.get(position).getSenderID().trim())) {
                return ITEM_SEND;
            } else {
                return ITEM_RECEIVE;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(holder.getLayoutPosition());

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            if (uid.trim().equals(currentUser.getUid().trim())) {
                SendViewHolder sendViewHolder = (SendViewHolder) holder;

                sendViewHolder.itemChatMessageSendBinding.tvTime.setText(chatMessage.getTime().trim());
                if (!chatMessage.getImage().trim().isEmpty()) {
                    handleImage(sendViewHolder.itemChatMessageSendBinding.ivImage, chatMessage.getImage().trim());
                    sendViewHolder.itemChatMessageSendBinding.ivImage.setVisibility(View.VISIBLE);
                    sendViewHolder.itemChatMessageSendBinding.tvSend.setVisibility(View.GONE);
                } else {
                    sendViewHolder.itemChatMessageSendBinding.tvSend.setText(chatMessage.getMessage().trim());
                    sendViewHolder.itemChatMessageSendBinding.ivImage.setVisibility(View.GONE);
                    sendViewHolder.itemChatMessageSendBinding.tvSend.setVisibility(View.VISIBLE);
                }
            } else {
                ReactionsConfig config = new ReactionsConfigBuilder(context)
                        .withReactions(emoticon)
                        .withPopupColor(context.getResources().getColor(R.color.color_8))
                        .build();

                if (getItemViewType(position) == ITEM_SEND) {
                    SendViewHolder sendViewHolder = (SendViewHolder) holder;

                    sendViewHolder.itemChatMessageSendBinding.tvTime.setText(chatMessage.getTime().trim());
                    if (!chatMessage.getImage().trim().isEmpty()) {
                        handleImage(sendViewHolder.itemChatMessageSendBinding.ivImage, chatMessage.getImage().trim());
                        sendViewHolder.itemChatMessageSendBinding.ivImage.setVisibility(View.VISIBLE);
                        sendViewHolder.itemChatMessageSendBinding.tvSend.setVisibility(View.GONE);
                    } else {
                        sendViewHolder.itemChatMessageSendBinding.tvSend.setText(chatMessage.getMessage().trim());
                        sendViewHolder.itemChatMessageSendBinding.ivImage.setVisibility(View.GONE);
                        sendViewHolder.itemChatMessageSendBinding.tvSend.setVisibility(View.VISIBLE);
                    }

                    if (chatMessage.getEmoticon() >= 0) {
                        sendViewHolder.itemChatMessageSendBinding.ivEmoticon.setImageResource(emoticon[chatMessage.getEmoticon()]);
                        sendViewHolder.itemChatMessageSendBinding.ivEmoticon.setVisibility(View.VISIBLE);
                    } else {
                        sendViewHolder.itemChatMessageSendBinding.ivEmoticon.setVisibility(View.GONE);
                    }
                } else {
                    ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;

                    Picasso.get()
                            .load(avatar)
                            .placeholder(R.drawable.ic_user_avatar)
                            .error(R.drawable.ic_user_avatar)
                            .into(receiveViewHolder.itemChatMessageReceiveBinding.civAvatar);
                    receiveViewHolder.itemChatMessageReceiveBinding.tvTime.setText(chatMessage.getTime().trim());
                    if (!chatMessage.getImage().trim().isEmpty()) {
                        handleImage(receiveViewHolder.itemChatMessageReceiveBinding.ivImage, chatMessage.getImage().trim());
                        receiveViewHolder.itemChatMessageReceiveBinding.ivImage.setVisibility(View.VISIBLE);
                        receiveViewHolder.itemChatMessageReceiveBinding.tvReceive.setVisibility(View.GONE);
                    } else {
                        receiveViewHolder.itemChatMessageReceiveBinding.tvReceive.setText(chatMessage.getMessage().trim());
                        receiveViewHolder.itemChatMessageReceiveBinding.ivImage.setVisibility(View.GONE);
                        receiveViewHolder.itemChatMessageReceiveBinding.tvReceive.setVisibility(View.VISIBLE);
                    }

                    if (chatMessage.getEmoticon() >= 0) {
                        receiveViewHolder.itemChatMessageReceiveBinding.ivEmoticon.setImageResource(emoticon[chatMessage.getEmoticon()]);
                    } else {
                        receiveViewHolder.itemChatMessageReceiveBinding.ivEmoticon.setImageResource(R.drawable.ic_add_1);
                    }

                    ReactionPopup popup = new ReactionPopup(context, config, (index) -> {
                        if (index < 0) {
                            return false;
                        }
                        receiveViewHolder.itemChatMessageReceiveBinding.ivEmoticon.setImageResource(emoticon[index]);
                        receiveViewHolder.itemChatMessageReceiveBinding.ivEmoticon.setVisibility(View.VISIBLE);

                        firebaseDatabase.getReference()
                                .child("chats")
                                .child(senderRoom)
                                .child("messages")
                                .child(chatMessage.getMessageID())
                                .child("emoticon").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task1) {
                                firebaseDatabase.getReference()
                                        .child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .child(chatMessage.getMessageID())
                                        .child("emoticon").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task2) {
                                        String icon = index.toString().trim();
                                        String room1 = String.valueOf(task1.getResult().getValue());
                                        String room2 = String.valueOf(task2.getResult().getValue());

                                        if (!room1.isEmpty() && !room2.isEmpty()) {
                                            if (icon.equals(room1) && icon.equals(room2)) {
                                                firebaseDatabase.getReference()
                                                        .child("chats")
                                                        .child(senderRoom)
                                                        .child("messages")
                                                        .child(chatMessage.getMessageID())
                                                        .child("emoticon")
                                                        .setValue(-1);
                                                firebaseDatabase.getReference()
                                                        .child("chats")
                                                        .child(receiverRoom)
                                                        .child("messages")
                                                        .child(chatMessage.getMessageID())
                                                        .child("emoticon")
                                                        .setValue(-1);
                                            } else {
                                                firebaseDatabase.getReference()
                                                        .child("chats")
                                                        .child(senderRoom)
                                                        .child("messages")
                                                        .child(chatMessage.getMessageID())
                                                        .child("emoticon")
                                                        .setValue(index);
                                                firebaseDatabase.getReference()
                                                        .child("chats")
                                                        .child(receiverRoom)
                                                        .child("messages")
                                                        .child(chatMessage.getMessageID())
                                                        .child("emoticon")
                                                        .setValue(index);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        return true; // true is closing popup, false is requesting a new selection
                    });

                    receiveViewHolder.itemChatMessageReceiveBinding.ivEmoticon.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            popup.onTouch(view, motionEvent);
                            return false;
                        }
                    });
                }
            }
        }
    }

    private void handleImage(ImageView ivImage, String image) {
        Picasso.get()
                .load(image)
                .placeholder(R.drawable.ic_picture)
                .error(R.drawable.ic_picture)
                .into(ivImage);
    }

    @Override
    public int getItemCount() {
        if (chatMessageList != null) {
            return chatMessageList.size();
        }
        return 0;
    }

    public class SendViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatMessageSendBinding itemChatMessageSendBinding;

        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            itemChatMessageSendBinding = ItemChatMessageSendBinding.bind(itemView);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            int half = (width / 2) + (width / 4);
            itemChatMessageSendBinding.tvSend.setMaxWidth(half);

            itemChatMessageSendBinding.llChatSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemChatMessageSendBinding.tvTime.getVisibility() == View.GONE) {
                        itemChatMessageSendBinding.tvTime.setVisibility(View.VISIBLE);
                    } else {
                        itemChatMessageSendBinding.tvTime.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatMessageReceiveBinding itemChatMessageReceiveBinding;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            itemChatMessageReceiveBinding = ItemChatMessageReceiveBinding.bind(itemView);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            int half = (width / 2) + (width / 4);
            itemChatMessageReceiveBinding.tvReceive.setMaxWidth(half);

            itemChatMessageReceiveBinding.llChatReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemChatMessageReceiveBinding.tvTime.getVisibility() == View.GONE) {
                        itemChatMessageReceiveBinding.tvTime.setVisibility(View.VISIBLE);
                    } else {
                        itemChatMessageReceiveBinding.tvTime.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
