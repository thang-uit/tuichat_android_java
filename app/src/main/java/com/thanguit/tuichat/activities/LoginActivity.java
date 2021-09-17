package com.thanguit.tuichat.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.animations.AnimationScale;
import com.thanguit.tuichat.databinding.ActivityLoginBinding;
import com.thanguit.tuichat.models.User;
import com.thanguit.tuichat.utils.MyToast;

import java.util.Arrays;

public class LoginActivity extends AppCompat {
    private static final String TAG = "LoginActivity";
    private static final String TAG_1 = "Login with Google";
    private static final String TAG_2 = "Login with Facebook";
    private ActivityLoginBinding activityLoginBinding;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;

    private AnimationScale animationScale;

    private static final String USER_DATABASE = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        callbackManager = CallbackManager.Factory.create();
        animationScale = AnimationScale.getInstance();

        initializeViews();
        listeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI();
        }
    }

    private void initializeViews() {
        animationScale.eventButton(this, activityLoginBinding.btnLoginPhoneNumber);
        animationScale.eventButton(this, activityLoginBinding.btnLoginGoogle);
        animationScale.eventButton(this, activityLoginBinding.btnLoginFacebook);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void listeners() {
        activityLoginBinding.btnLoginPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PhoneNumberActivity.class);
                startActivity(intent);
            }
        });

        activityLoginBinding.btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityResultLauncher.launch(new Intent(googleSignInClient.getSignInIntent()));
            }
        });

        activityLoginBinding.btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG_2, "facebook: onSuccess: " + loginResult.getAccessToken());

                        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                        firebaseAuth.signInWithCredential(credential)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG_2, "signInWithCredential:success");
                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                            if (firebaseUser != null) {
                                                firebaseDatabase.getReference()
                                                        .child(USER_DATABASE.trim())
                                                        .child(firebaseUser.getUid())
                                                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            User dataUser = task.getResult().getValue(User.class);
                                                            if (dataUser != null) {
                                                                Log.d(TAG_2, "User Information: " + dataUser.toString());
                                                                updateUI();
                                                            } else {
                                                                String uid = firebaseUser.getUid();
                                                                String name = firebaseUser.getDisplayName();
                                                                String phoneNumber = "Null";
                                                                if (firebaseUser.getPhoneNumber() != null) {
                                                                    phoneNumber = firebaseUser.getPhoneNumber();
                                                                }
                                                                String email = "Null";
                                                                if (firebaseUser.getEmail() != null) {
                                                                    email = firebaseUser.getEmail();
                                                                }
                                                                String avatar = "Null";
                                                                if (firebaseUser.getPhotoUrl() != null) {
                                                                    avatar = firebaseUser.getPhotoUrl() + "?height=500&access_token=" + loginResult.getAccessToken().getToken();
                                                                }

                                                                User user = new User(uid, name, phoneNumber, email, avatar);
                                                                addUserIntoDatabase(user);
                                                            }
                                                        } else {
                                                            Log.e("firebase", "Error getting data", task.getException());
                                                            MyToast.makeText(LoginActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.d(TAG_2, "signInWithCredential:failure", task.getException());
                                            MyToast.makeText(LoginActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG_2, "facebook: onCancel");
                        MyToast.makeText(LoginActivity.this, MyToast.INFORMATION, getString(R.string.toast5), MyToast.SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG_2, "facebook: onError ", error);
                        MyToast.makeText(LoginActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                    }
                });
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        Log.d(TAG_1, "firebaseAuthWithGoogle:" + account.getId());

                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG_1, "signInWithCredential:success");

                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        firebaseDatabase.getReference()
                                                .child(USER_DATABASE.trim())
                                                .child(firebaseUser.getUid())
                                                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    User dataUser = task.getResult().getValue(User.class);
                                                    if (dataUser != null) {
                                                        Log.d(TAG_1, "User Information: " + dataUser.toString());
                                                        updateUI();
                                                    } else {
                                                        Log.d(TAG_1, "User Information: " + firebaseUser.toString());

                                                        String uid = firebaseUser.getUid();
                                                        String name = firebaseUser.getDisplayName();
                                                        String phoneNumber = "Null";
                                                        if (firebaseUser.getPhoneNumber() != null) {
                                                            phoneNumber = firebaseUser.getPhoneNumber();
                                                        }
                                                        String email = "Null";
                                                        if (firebaseUser.getEmail() != null) {
                                                            email = firebaseUser.getEmail();
                                                        }
                                                        String avatar = "Null";
                                                        if (firebaseUser.getPhotoUrl() != null) {
                                                            avatar = firebaseUser.getPhotoUrl().toString().replace("s96-c", "s500-c");
                                                        }

                                                        User user = new User(uid, name, phoneNumber, email, avatar);
                                                        addUserIntoDatabase(user);
                                                    }
                                                } else {
                                                    Log.e("firebase", "Error getting data", task.getException());
                                                    MyToast.makeText(LoginActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d(TAG_1, "signInWithCredential:failure", task.getException());
                                    MyToast.makeText(LoginActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG_1, "Google sign in failed", e);
                    MyToast.makeText(LoginActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                }
            }
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addUserIntoDatabase(User user) {
//        Uri avatar = Uri.parse(user.getAvatar());
        firebaseDatabase.getReference()
                .child(USER_DATABASE.trim())
                .child(user.getUid())
                .setValue(user)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyToast.makeText(LoginActivity.this, MyToast.ERROR, getString(R.string.toast3), MyToast.SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}