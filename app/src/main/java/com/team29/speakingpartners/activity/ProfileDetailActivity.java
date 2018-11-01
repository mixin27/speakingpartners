package com.team29.speakingpartners.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Date;

import javax.annotation.Nullable;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.net.ConnectionChecking;

public class ProfileDetailActivity extends AppCompatActivity {

    public static final String TAG = ProfileDetailActivity.class.getSimpleName();

    AppCompatImageView imgProfile;
    AppCompatTextView tvUserName, tvUserEmail, tvUserLevel, tvUserCountry, tvUserDOB, tvUserGender;
    SwitchCompat switchOnlineOffline;
    AppCompatButton btnEditProfile;

    LinearLayout btnLogOutLayout, btnChangePasswordLayout;

    String user_id = "";
    long active_status = 0;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestoreSettings firestoreSettings = new FirebaseFirestoreSettings.Builder()
//                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(true)
                .build();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.setFirestoreSettings(firestoreSettings);

        // ActionBar
        setUpActionBar();

        imgProfile = findViewById(R.id.img_profile);

        tvUserName = findViewById(R.id.tv_profile_user_name);

        tvUserEmail = findViewById(R.id.tv_profile_email);

        tvUserLevel = findViewById(R.id.tv_profile_level);

        tvUserCountry = findViewById(R.id.tv_profile_country);

        tvUserDOB = findViewById(R.id.tv_profile_dob);

        tvUserGender = findViewById(R.id.tv_profile_gender);

        switchOnlineOffline = findViewById(R.id.btn_switch_active);

        // FetchInformation
        if (ConnectionChecking.checkConnection(this)) {
            fetchUserInformation();
        }

        switchOnlineOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchOnlineOffline.setText(getResources().getString(R.string.str_online));
                    updateActiveStatus(1);
                } else {
                    switchOnlineOffline.setText(getResources().getString(R.string.str_offline));
                    updateActiveStatus(0);
                }
            }
        });

        btnLogOutLayout = findViewById(R.id.btn_log_out_layout);
        btnLogOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    Log.d(TAG, "Log out successful!");

                    finish();
                    startActivity(new Intent(ProfileDetailActivity.this, LoginActivity.class));
                }
            }
        });

        btnChangePasswordLayout = findViewById(R.id.btn_change_password_layout);
        btnChangePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Change Password", Toast.LENGTH_SHORT).show();
            }
        });

        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileDetailActivity.this, EditProfileActivity.class);
                if (mAuth.getCurrentUser() != null) {
                    i.putExtra("EMAIL", mAuth.getCurrentUser().getEmail());
                }
                startActivity(i);
            }
        });
    }

    private void updateActiveStatus(int status) {
        DocumentReference docRef = mFirestore.collection("users").document(user_id);
        docRef.update("active_status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Update Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Update failed");
                    }
                });
    }

    private String getDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth == null) {
            return "";
        }

        return DateFormat.getDateInstance(DateFormat.FULL).format(dateOfBirth);
    }

    private void setUpActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Me");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void fetchUserInformation() {
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            Query query = mFirestore.collection("users")
                    .whereEqualTo("email", email);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen error", e);
                        return;
                    }

                    for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                        if (change.getType() == DocumentChange.Type.ADDED) {
                            Log.d(TAG, "New city:" + change.getDocument().getData());
                        }

                        String source = queryDocumentSnapshots.getMetadata().isFromCache() ?
                                "local cache" : "server";
                        Log.d(TAG, "Data fetched from " + source);

                        user_id = change.getDocument().getId();
                        Log.d(TAG, "CurrentUserId: " + user_id);

                        active_status = (long) change.getDocument().get("active_status");
                        Log.d(TAG, "CurrentUserStatus: " + active_status);

                        if (active_status == 1) {
                            switchOnlineOffline.setChecked(true);
                        } else {
                            switchOnlineOffline.setChecked(false);
                        }

                        tvUserName.setText(change.getDocument().getString("user_name"));
                        tvUserEmail.setText(change.getDocument().getString("email"));
                        tvUserLevel.setText(change.getDocument().getString("level"));
                        tvUserCountry.setText(change.getDocument().getString("country"));

                        String dob = getDateOfBirth(change.getDocument().getDate("date_of_birth"));
                        tvUserDOB.setText(dob);

                        tvUserGender.setText(change.getDocument().getString("gender"));

                        String url_img = change.getDocument().getString("url_photo");
                        if (!url_img.equals("")) {
                            Glide.with(getApplicationContext())
                                    .load(Uri.parse(url_img))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imgProfile);
                        }
                    }
                }
            });
        }
    }
}
