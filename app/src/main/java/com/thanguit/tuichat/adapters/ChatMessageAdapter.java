package com.thanguit.tuichat.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private final int ITEM_SEND = 1;
    private final int ITEM_RECEIVE = 2;

    int[] emoticon = new int[]{
            R.drawable.ic_love,
            R.drawable.ic_laughing,
            R.drawable.ic_crying,
            R.drawable.ic_angry,
            R.drawable.ic_surprised,
            R.drawable.ic_cool
    };

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
    }

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessageList, String uid, String avatar) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.uid = uid;
        this.avatar = avatar;
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
        ChatMessage chatMessage = chatMessageList.get(position);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getUid().trim().equals(chatMessage.getSenderID().trim())) {
                return ITEM_SEND;
            } else {
                return ITEM_RECEIVE;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            if (uid.trim().equals(currentUser.getUid().trim())) {
                SendViewHolder sendViewHolder = (SendViewHolder) holder;
                sendViewHolder.itemChatMessageSendBinding.tvSend.setText(chatMessageList.get(position).getMessage().trim());
                sendViewHolder.itemChatMessageSendBinding.tvTime.setText(chatMessageList.get(position).getTime().trim());

                sendViewHolder.itemChatMessageSendBinding.tvSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sendViewHolder.itemChatMessageSendBinding.tvTime.getVisibility() == View.GONE) {
                            sendViewHolder.itemChatMessageSendBinding.tvTime.setVisibility(View.VISIBLE);
                        } else {
                            sendViewHolder.itemChatMessageSendBinding.tvTime.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                ReactionsConfig config = new ReactionsConfigBuilder(context)
                        .withReactions(emoticon)
                        .withPopupColor(context.getResources().getColor(R.color.color_8))
                        .build();

                if (getItemViewType(position) == ITEM_SEND) {
                    SendViewHolder sendViewHolder = (SendViewHolder) holder;
                    sendViewHolder.itemChatMessageSendBinding.tvSend.setText(chatMessageList.get(position).getMessage().trim());
                    sendViewHolder.itemChatMessageSendBinding.tvTime.setText(chatMessageList.get(position).getTime().trim());

                    sendViewHolder.itemChatMessageSendBinding.tvSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (sendViewHolder.itemChatMessageSendBinding.tvTime.getVisibility() == View.GONE) {
                                sendViewHolder.itemChatMessageSendBinding.tvTime.setVisibility(View.VISIBLE);
                            } else {
                                sendViewHolder.itemChatMessageSendBinding.tvTime.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
                    Picasso.get().load(avatar)
                            .placeholder(R.drawable.ic_user_avatar)
                            .error(R.drawable.ic_user_avatar)
                            .into(receiveViewHolder.itemChatMessageReceiveBinding.civAvatar);
                    receiveViewHolder.itemChatMessageReceiveBinding.tvReceive.setText(chatMessageList.get(position).getMessage().trim());
                    receiveViewHolder.itemChatMessageReceiveBinding.tvTime.setText(chatMessageList.get(position).getTime().trim());

                    receiveViewHolder.itemChatMessageReceiveBinding.tvReceive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (receiveViewHolder.itemChatMessageReceiveBinding.tvTime.getVisibility() == View.GONE) {
                                receiveViewHolder.itemChatMessageReceiveBinding.tvTime.setVisibility(View.VISIBLE);
                            } else {
                                receiveViewHolder.itemChatMessageReceiveBinding.tvTime.setVisibility(View.GONE);
                            }
                        }
                    });

                    ReactionPopup popup = new ReactionPopup(context, config, (index) -> {
                        receiveViewHolder.itemChatMessageReceiveBinding.ivEmoticon.setImageResource(emoticon[index]);
                        receiveViewHolder.itemChatMessageReceiveBinding.ivEmoticon.setVisibility(View.VISIBLE);
                        return true; // true is closing popup, false is requesting a new selection
                    });
                }
            }


//            receiveViewHolder.itemChatMessageReceiveBinding.tvReceive.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    popup.onTouch(view, motionEvent);
//                    return false;
//                }
//            });
        }

//        if (holder.getClass().toString().trim().equals(SendViewHolder.class.toString().trim())) {
//            SendViewHolder sendViewHolder = (SendViewHolder) holder;
//
//            sendViewHolder.itemChatMessageSendBinding.tvSend.setText(chatMessageList.get(position).getMessage().trim());
//        } else {
//            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
//
//            Picasso.get().load(avatar)
//                    .placeholder(R.drawable.ic_user_avatar)
//                    .error(R.drawable.ic_user_avatar)
//                    .into(receiveViewHolder.itemChatMessageReceiveBinding.civAvatar);
//            receiveViewHolder.itemChatMessageReceiveBinding.tvReceive.setText(chatMessageList.get(position).getMessage().trim());
//        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder {
        private ItemChatMessageSendBinding itemChatMessageSendBinding;

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
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {
        private ItemChatMessageReceiveBinding itemChatMessageReceiveBinding;

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
        }
    }
}
