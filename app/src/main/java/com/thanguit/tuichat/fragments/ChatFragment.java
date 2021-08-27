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
import com.thanguit.tuichat.adapters.StoryAdapter;
import com.thanguit.tuichat.adapters.UserAdapter;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.database.FirebaseManager;
import com.thanguit.tuichat.databinding.FragmentChatBinding;
import com.thanguit.tuichat.models.Story;
import com.thanguit.tuichat.models.User;
import com.thanguit.tuichat.models.UserStory;
import com.thanguit.tuichat.utils.LoadingDialog;
import com.thanguit.tuichat.utils.MyToast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private FragmentChatBinding fragmentChatBinding;

    private static final String USER_STORY_STORAGE = "USER_STORY/";
    private static final String USER_DATABASE = "users";
    private static final String STORY_DATABASE = "users_stories";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private FirebaseManager firebaseManager;

    private List<User> userList;
    private UserAdapter userAdapter;

    private StoryAdapter storyAdapter;
    private List<UserStory> userStoryList;

    private AnimationScale animationScale;
    private LoadingDialog loadingDialog;

    private User mUser;

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
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseManager = FirebaseManager.getInstance();
        animationScale = AnimationScale.getInstance();
        loadingDialog = LoadingDialog.getInstance();

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

            firebaseManager.getUserInfo(currentUser.getUid().trim());
            firebaseManager.setReadUserInformation(new FirebaseManager.GetUserInformationListener() {
                @Override
                public void getUserInformationListener(User user) {
                    mUser = new User();
                    mUser = user;
                }
            });

            firebaseDatabase.getReference().child(STORY_DATABASE.trim()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userStoryList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserStory userStory = new UserStory();
                            userStory.setName(dataSnapshot.child("name").getValue(String.class));
                            userStory.setAvatar(dataSnapshot.child("avatar").getValue(String.class));
                            userStory.setLastUpdated(dataSnapshot.child("lastUpdated").getValue(String.class));

                            List<Story> storyList = new ArrayList<>();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.child("stories").getChildren()) {
                                Story story = dataSnapshot1.getValue(Story.class);
                                storyList.add(story);
                            }
                            userStory.setStoryList(storyList);
                            userStoryList.add(userStory);
                        }
                        storyAdapter = new StoryAdapter(getContext(), userStoryList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                        fragmentChatBinding.rvStory.setLayoutManager(linearLayoutManager);
                        fragmentChatBinding.rvStory.setAdapter(storyAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void listeners() {
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
                .setSelectMaxCount(5)
                .setSelectMaxCountErrorText(getString(R.string.tedBottomPicker3))
                .setCompleteButtonText(getString(R.string.tedBottomPicker1))
                .setEmptySelectionText(getString(R.string.tedBottomPicker2))
//                .setSelectedUriList(selectedUriList)
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        if (uriList != null && !uriList.isEmpty()) {
                            addDataToFirebase(uriList);
                        }
                    }
                });
    }

    private void addDataToFirebase(List<Uri> uriList) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            loadingDialog.startLoading(getContext(), false);

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String time = dateFormat.format(date);

            StorageReference storageReference = firebaseStorage.getReference()
                    .child(USER_STORY_STORAGE.trim() + currentUser.getUid().trim() + "/" + time.trim());
            for (int i = 0; i < uriList.size(); i++) {
                Uri individualImage = uriList.get(i);
                StorageReference imageName = storageReference.child(individualImage.getLastPathSegment().trim());
                imageName.putFile(individualImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String storyImage = uri.toString().trim();

                                    UserStory userStory = new UserStory();
                                    userStory.setName(mUser.getName());
                                    userStory.setAvatar(mUser.getAvatar());
                                    userStory.setLastUpdated(time.trim());

                                    HashMap<String, Object> userStoryObj = new HashMap<>();
                                    userStoryObj.put("name", userStory.getName());
                                    userStoryObj.put("avatar", userStory.getAvatar());
                                    userStoryObj.put("lastUpdated", userStory.getLastUpdated());

                                    Story story = new Story(storyImage, userStory.getLastUpdated());

                                    firebaseDatabase.getReference()
                                            .child(STORY_DATABASE.trim())
                                            .child(currentUser.getUid().trim())
                                            .updateChildren(userStoryObj);

                                    firebaseDatabase.getReference()
                                            .child(STORY_DATABASE.trim())
                                            .child(currentUser.getUid().trim())
                                            .child("stories")
                                            .push()
                                            .setValue(story);

                                    loadingDialog.cancelLoading();
                                    MyToast.makeText(getContext(), MyToast.SUCCESS, getString(R.string.toast9), MyToast.SHORT).show();
                                }
                            });
                        } else {
                            MyToast.makeText(getContext(), MyToast.ERROR, getString(R.string.toast8), MyToast.SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void addToDatabaseRealtime(String storyImage) {
    }

    private String handleDate(String date) {
        // Code lấy từ link này nè: https://vnsharebox.com/blog/convert-string-to-datetime-android/
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date datetime_1 = simpleDateFormat.parse(date); // Chuyển String ngày nhập vào thành Date

            String currentTime = simpleDateFormat.format(calendar.getTime()); // calendar.getTime(): Trả về đối tượng Date dựa trên giá trị của Calendar.
            Date datetime_2 = simpleDateFormat.parse(currentTime); // Chuyển String ngày nhập vào thành Date

            long diff = datetime_2.getTime() - datetime_1.getTime();
            int hours = (int) (diff / (60 * 60 * 1000));
            int minutes = (int) (diff / (1000 * 60)) % 60;
            int days = (int) (diff / (24 * 60 * 60 * 1000));

            if (days > 0) {
                return days + getResources().getString(R.string.days);
            } else {
                if (hours > 0) {
                    return hours + getResources().getString(R.string.hours);
                } else {
                    return minutes + getResources().getString(R.string.minutes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getResources().getString(R.string.today);
        }
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