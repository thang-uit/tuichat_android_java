package com.thanguit.tuichat.fragments;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.adapters.StoryAdapter;
import com.thanguit.tuichat.adapters.UserAdapter;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.databinding.FragmentChatBinding;
import com.thanguit.tuichat.models.User;
import com.thanguit.tuichat.models.UserStory;
import com.thanguit.tuichat.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private FragmentChatBinding fragmentChatBinding;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private List<User> userList;
    private UserAdapter userAdapter;

    private StoryAdapter storyAdapter;
    private List<UserStory> userStoryList;

    private AnimationScale animationScale;

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        animationScale = AnimationScale.getInstance();

        initializeViews();
        listeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentChatBinding = null;
    }

    private void initializeViews() {
        animationScale.eventConstraintLayout(getContext(), fragmentChatBinding.cslStory);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firebaseDatabase.getReference().child("users/" + currentUser.getUid().trim() + "/avatar").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.d("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        Picasso.get()
                                .load(String.valueOf(task.getResult().getValue()))
                                .placeholder(R.drawable.ic_user_avatar)
                                .error(R.drawable.ic_user_avatar)
                                .into(fragmentChatBinding.civStory);
                    }
                }
            });
        }

        storyAdapter = new StoryAdapter(getContext(), userStoryList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        fragmentChatBinding.rvStory.setLayoutManager(linearLayoutManager);
        fragmentChatBinding.rvStory.setAdapter(storyAdapter);
    }

    private void listeners() {
        firebaseDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter = new UserAdapter(getContext(), userList);
                fragmentChatBinding.rvChat.setHasFixedSize(true);
                fragmentChatBinding.rvChat.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        fragmentChatBinding.cslStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                activityResultLauncher.launch("image/*");

                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        selectImageFromGallery();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        MyToast.makeText(getContext(), MyToast.WARNING, getString(R.string.toast7) + deniedPermissions.toString(), MyToast.SHORT).show();
                    }
                };

                TedPermission.with(getContext())
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();
            }
        });
    }

    private void selectImageFromGallery() {
        TedBottomPicker.with((FragmentActivity) getContext())
                .setPeekHeight(1600)
                .showTitle(false)
                .setSelectMaxCount(10)
                .setSelectMaxCountErrorText(getString(R.string.tedBottomPicker3))
                .setCompleteButtonText(getString(R.string.tedBottomPicker1))
                .setEmptySelectionText(getString(R.string.tedBottomPicker2))
//                .setSelectedUriList(selectedUriList)
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        if (uriList != null && !uriList.isEmpty()) {

                        }
                    }
                });
    }

//    ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.GetContent(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri result) {
//                    if (result != null) {
//                    }
//                }
//            });
}