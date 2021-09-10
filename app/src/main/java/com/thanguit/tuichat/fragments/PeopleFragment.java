package com.thanguit.tuichat.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.thanguit.tuichat.adapters.PeopleAdapter;
import com.thanguit.tuichat.adapters.UserAdapter;
import com.thanguit.tuichat.databinding.FragmentPeopleBinding;
import com.thanguit.tuichat.models.User;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {
    private FragmentPeopleBinding fragmentPeopleBinding;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private List<User> userList;
    private PeopleAdapter peopleAdapter;

    public PeopleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPeopleBinding = FragmentPeopleBinding.inflate(inflater, container, false);
        return fragmentPeopleBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        initializeViews();
        listeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentPeopleBinding = null;
    }

    private void initializeViews() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firebaseDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        userList.add(user);
                    }
                    peopleAdapter = new PeopleAdapter(getContext(), userList);
                    fragmentPeopleBinding.rvPeople.setHasFixedSize(true);
                    fragmentPeopleBinding.rvPeople.setAdapter(peopleAdapter);

                    fragmentPeopleBinding.sflItemPeople.setVisibility(View.GONE);
                    fragmentPeopleBinding.rvPeople.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    private void listeners() {
        fragmentPeopleBinding.edtSearchPeople.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keyWord = editable.toString().toLowerCase().trim(); // Chuyển kí tự về dạng chữ viết thường để tìm kiếm cho nhanh
                searching(keyWord);
            }
        });
    }

    private void searching(String keyWord) {
        FirebaseRecyclerOptions<User> userFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(firebaseDatabase.getReference().child("users")
                        .orderByChild("name")
                        .startAt(keyWord).endAt(keyWord + "~"), User.class)
                .build();
    }
}