package com.thanguit.tuichat.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanguit.tuichat.adapters.UserAdapter;
import com.thanguit.tuichat.databinding.FragmentChatBinding;
import com.thanguit.tuichat.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private FragmentChatBinding fragmentChatBinding;

    private FirebaseDatabase firebaseDatabase;
    private List<User> userList;
    private UserAdapter userAdapter;

    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentChatBinding = FragmentChatBinding.inflate(inflater, container, false);
        return fragmentChatBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();

        initializeViews();
        listeners();

    }

    private void initializeViews() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userList);
        fragmentChatBinding.rvChat.setHasFixedSize(true);
        fragmentChatBinding.rvChat.setAdapter(userAdapter);
    }

    private void listeners() {
        firebaseDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentChatBinding = null;
    }


}