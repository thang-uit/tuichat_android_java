package com.thanguit.tuichat.database;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.thanguit.tuichat.models.User;

public class FirebaseManager {
    private static FirebaseManager instance;

    private static final String USER_AVATAR_STORAGE = "User_Avatar";
    private static final String USER_DATABASE = "users";

    private static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private FirebaseManager() {
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            synchronized (FirebaseManager.class) {
                if (instance == null) {
                    instance = new FirebaseManager();
                }
            }
        }
        return instance;
    }

    public interface GetUserAvatarListener {
        void getUserAvatarListener(String avatar);
    }

    private GetUserAvatarListener getUserAvatarListener;

    public void setReadUserAvatar(GetUserAvatarListener getUserAvatarListener) {
        this.getUserAvatarListener = getUserAvatarListener;
    }

    public interface GetUserInformationListener {
        void getUserInformationListener(User user);
    }

    private GetUserInformationListener getUserInformationListener;

    public void setReadUserInformation(GetUserInformationListener getUserInformationListener) {
        this.getUserInformationListener = getUserInformationListener;
    }


    public void createUserProfile(String uid, String name, String phoneNumber, Uri avatar) {
    }

    public void getUserAvatar(String uid) {
        firebaseDatabase.getReference().child("users/" + uid.trim() + "/avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    getUserAvatarListener.getUserAvatarListener(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("firebase", error.getMessage().trim());
            }
        });
    }

    public void getUserInfo(String uid) {
        firebaseDatabase.getReference().child("users/" + uid.trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    getUserInformationListener.getUserInformationListener(snapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("firebase", error.getMessage().trim());
            }
        });
    }
}
