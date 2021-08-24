package com.thanguit.tuichat.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.activities.ChatActivity;
import com.thanguit.tuichat.databinding.ItemConversationBinding;
import com.thanguit.tuichat.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";

    private Context context;
    private List<User> userList;

    private FirebaseAuth firebaseAuth;

    public UserAdapter() {
    }

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        handleUser(holder, position);

        Picasso.get()
                .load(userList.get(position).getAvatar())
                .placeholder(R.drawable.ic_user_avatar)
                .error(R.drawable.ic_user_avatar)
                .into(holder.itemConversationBinding.civAvatar);
        holder.itemConversationBinding.tvChatName.setText(userList.get(position).getName().trim());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("USER", userList.get(holder.getLayoutPosition()));
                context.startActivity(intent);
            }
        });
    }

    private void handleUser(ViewHolder holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            if (userList.get(position).getUid().trim().equals(currentUser.getUid().trim())) {
                holder.itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemConversationBinding itemConversationBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemConversationBinding = ItemConversationBinding.bind(itemView);
            itemConversationBinding.tvChatName.setSelected(true);
        }
    }
}
