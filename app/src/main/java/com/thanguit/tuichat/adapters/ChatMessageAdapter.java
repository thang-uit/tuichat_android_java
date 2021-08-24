package com.thanguit.tuichat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    private Context context;
    private List<ChatMessage> chatMessageList;
    private String avatar;

    private final int ITEM_SEND = 1;
    private final int ITEM_RECEIVE = 2;

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
    }

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessageList, String avatar) {
        this.context = context;
        this.chatMessageList = chatMessageList;
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
        if (getItemViewType(position) == ITEM_SEND) {
            SendViewHolder sendViewHolder = (SendViewHolder) holder;
            sendViewHolder.itemChatMessageSendBinding.tvSend.setText(chatMessageList.get(position).getMessage().trim());
        } else {
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            Picasso.get().load(avatar)
                    .placeholder(R.drawable.ic_user_avatar)
                    .error(R.drawable.ic_user_avatar)
                    .into(receiveViewHolder.itemChatMessageReceiveBinding.civAvatar);
            receiveViewHolder.itemChatMessageReceiveBinding.tvReceive.setText(chatMessageList.get(position).getMessage().trim());
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
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {
        private ItemChatMessageReceiveBinding itemChatMessageReceiveBinding;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            itemChatMessageReceiveBinding = ItemChatMessageReceiveBinding.bind(itemView);
        }
    }
}
