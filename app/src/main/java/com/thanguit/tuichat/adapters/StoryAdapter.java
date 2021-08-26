package com.thanguit.tuichat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thanguit.tuichat.R;
import com.thanguit.tuichat.databinding.ItemStoryBinding;
import com.thanguit.tuichat.models.UserStory;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private Context context;
    private List<UserStory> userStoryList;

    public StoryAdapter(Context context, List<UserStory> userStoryList) {
        this.context = context;
        this.userStoryList = userStoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return userStoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemStoryBinding itemStoryBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemStoryBinding = ItemStoryBinding.bind(itemView);
        }
    }
}
