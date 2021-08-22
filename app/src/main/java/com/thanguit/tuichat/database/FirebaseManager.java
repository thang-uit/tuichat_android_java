package com.thanguit.tuichat.database;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseManager {
    private static FirebaseManager instance;

    private static final String USER_AVATAR_STORAGE = "User_Avatar";
    private static final String USER_DATABASE = "users";

    private static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private String url = "";


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

    public void createUserProfile(String uid, String name, String phoneNumber, Uri avatar) {
    }

    public String getUserAvatar(String uid) {
        firebaseDatabase.getReference().child("users").child(uid).child("avatar").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("firebase", "Error getting data", task.getException());
                } else {
                    url = task.getResult().getValue().toString();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
        return url;
    }
}
