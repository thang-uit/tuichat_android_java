package com.thanguit.tuichat.activities;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.database.FirebaseManager;
import com.thanguit.tuichat.databinding.ActivityUserProfileBinding;
import com.thanguit.tuichat.models.User;
import com.thanguit.tuichat.utils.LoadingDialog;
import com.thanguit.tuichat.utils.MyToast;
import com.thanguit.tuichat.utils.OptionDialog;

import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class UserProfileActivity extends AppCompat {
    private ActivityUserProfileBinding activityUserProfileBinding;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseManager firebaseManager;

    private AnimationScale animationScale;
    private LoadingDialog loadingDialog;

    private static final String USER_DATABASE = "users";
    private static final String USER_AVATAR_STORAGE = "USER_AVATAR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_main_1));
        }
        activityUserProfileBinding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(activityUserProfileBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseManager = FirebaseManager.getInstance();
        animationScale = AnimationScale.getInstance();
        loadingDialog = LoadingDialog.getInstance();

        initializeViews();
        listeners();
    }

    private void initializeViews() {
//        activityUserProfileBinding.ccpCountryCodePicker.setDefaultCountryUsingNameCode("VN");
//        activityUserProfileBinding.ccpCountryCodePicker.resetToDefaultCountry();

        animationScale.eventConstraintLayout(this, activityUserProfileBinding.cslAvatar);
        animationScale.eventButton(this, activityUserProfileBinding.btnLogout);
        animationScale.eventButton(this, activityUserProfileBinding.btnSave);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firebaseManager.getUserInfo(currentUser.getUid().trim());
            firebaseManager.setReadUserInformation(new FirebaseManager.GetUserInformationListener() {
                @Override
                public void getUserInformationListener(User user) {
                    if (user != null) {
                        Picasso.get()
                                .load(user.getAvatar().trim())
                                .placeholder(R.drawable.ic_user_avatar)
                                .error(R.drawable.ic_user_avatar)
                                .into(activityUserProfileBinding.civAvatarFrame);

                        activityUserProfileBinding.tvUserName.setText(user.getName().trim());
                        activityUserProfileBinding.edtName.setText(user.getName().trim());
                        activityUserProfileBinding.edtEmail.setText(user.getEmail().trim());
                        activityUserProfileBinding.edtPhoneNumber.setText(user.getPhoneNumber().trim());
                    }
                }
            });
        }
    }

    private void listeners() {
        activityUserProfileBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String name = activityUserProfileBinding.edtName.getText().toString().trim();
                    String email = activityUserProfileBinding.edtEmail.getText().toString().trim();
//                    String phoneNumber = activityUserProfileBinding.edtPhoneNumber.getText().toString().trim();

                    if (name.isEmpty()) {
                        activityUserProfileBinding.edtName.setError(getString(R.string.edtSetupProfileNameError));
                    } else {
                        loadingDialog.startLoading(UserProfileActivity.this, false);

                        HashMap<String, Object> updateData = new HashMap<>();
                        updateData.put("name", name);
                        updateData.put("email", email);

                        firebaseDatabase.getReference()
                                .child(USER_DATABASE.trim())
                                .child(currentUser.getUid())
                                .updateChildren(updateData)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.cancelLoading();
                                        MyToast.makeText(UserProfileActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        loadingDialog.cancelLoading();
                                        MyToast.makeText(UserProfileActivity.this, MyToast.SUCCESS, getString(R.string.toast12), MyToast.SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

        activityUserProfileBinding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityUserProfileBinding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogoutDialog();
            }
        });

        activityUserProfileBinding.cslAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        selectImageFromGallery();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        MyToast.makeText(UserProfileActivity.this, MyToast.WARNING, getString(R.string.toast7) + deniedPermissions.toString(), MyToast.SHORT).show();
                    }
                };

                TedPermission.with(UserProfileActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();
            }
        });
    }

    private void openLogoutDialog() {
        OptionDialog logoutDialog = new OptionDialog(this,
                getResources().getString(R.string.btnDialog23).trim(),
                getResources().getString(R.string.tvDialogContent3).trim(),
                getResources().getString(R.string.btnDialog11).trim(),
                getResources().getString(R.string.btnDialog23).trim(), true,
                new OptionDialog.SetActionButtonListener() {
                    @Override
                    public void setNegativeButtonListener(Dialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void setPositiveButtonListener(Dialog dialog) {
                        dialog.dismiss();
                        firebaseAuth.signOut();
                        moveTaskToBack(true);
                        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
        logoutDialog.show();
    }

    private void selectImageFromGallery() {
        TedBottomPicker.with(this).show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Uri uri) {
                if (uri != null) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        loadingDialog.startLoading(UserProfileActivity.this, false);

                        StorageReference storageReference = firebaseStorage.getReference().child(USER_AVATAR_STORAGE.trim()).child(firebaseUser.getUid());
                        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String avatar = uri.toString().trim();

                                            firebaseDatabase.getReference()
                                                    .child(USER_DATABASE.trim())
                                                    .child(firebaseUser.getUid())
                                                    .child("avatar")
                                                    .setValue(avatar)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            loadingDialog.cancelLoading();
                                                            MyToast.makeText(UserProfileActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                                        }
                                                    })
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            loadingDialog.cancelLoading();
                                                            MyToast.makeText(UserProfileActivity.this, MyToast.SUCCESS, getString(R.string.toast12), MyToast.SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}