package com.team29.speakingpartners.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Date;

import javax.annotation.Nullable;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.UserModel;
import com.team29.speakingpartners.net.ConnectionChecking;
import com.team29.speakingpartners.utils.GlideApp;

public class ProfileDetailActivity extends AppCompatActivity {

    public static final String TAG = ProfileDetailActivity.class.getSimpleName();

    AppCompatImageView imgProfile;
    AppCompatTextView tvUserName, tvUserEmail, tvUserLevel, tvUserCountry, tvUserDOB, tvUserGender, tvVerifiedEmailStatus;
    SwitchCompat switchOnlineOffline;
    AppCompatButton btnEditProfile, btnLogOutLayout, btnChangePasswordLayout, btnVerify;

    LinearLayout layoutVerifyEmail;

    ProgressBar progressVerify;

    String user_id = "";
    long active_status = 0;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_profile_detail);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

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

        /*layoutVerifyEmail = findViewById(R.id.verified_layout);

        tvVerifiedEmailStatus = findViewById(R.id.email_verified);*/

        /*progressVerify = findViewById(R.id.progress_verify);

        btnVerify = findViewById(R.id.btn_send_me);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnVerify.setVisibility(View.GONE);
                progressVerify.setVisibility(View.VISIBLE);
                mAuth.getCurrentUser()
                        .sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressVerify.setVisibility(View.GONE);
                                tvVerifiedEmailStatus.setText(getString(R.string.str_sent_mail));
                            }
                        });
            }
        });*/

        // FetchInformation
        if (ConnectionChecking.checkConnection(this)) {
            fetchUserInformation();
        }

        switchOnlineOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ConnectionChecking.checkConnection(getApplicationContext())) {
                    if (isChecked) {
                        switchOnlineOffline.setText(getResources().getString(R.string.str_online));
                        updateActiveStatus(1);
                    } else {
                        switchOnlineOffline.setText(getResources().getString(R.string.str_offline));
                        updateActiveStatus(0);
                    }
                }
            }
        });

        btnLogOutLayout = findViewById(R.id.btn_log_out);
        btnLogOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ProfileDetailActivity.this)
                        .setTitle("")
                        .setMessage("Are you sure want to log out?")
                        .setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mAuth.getCurrentUser() != null) {
                                    mAuth.signOut();
                                    Log.d(TAG, "Log out successful!");

                                    finish();
                                    startActivity(new Intent(ProfileDetailActivity.this, LoginActivity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            }
                        }).show();
            }
        });

        btnChangePasswordLayout = findViewById(R.id.btn_reset_password);
        btnChangePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
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
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

        // Don't use this for now
//        if (!mAuth.getCurrentUser().isEmailVerified()) {
//            layoutVerifyEmail.setVisibility(View.VISIBLE);
//        } else {
//            layoutVerifyEmail.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (ConnectionChecking.checkConnection(getApplicationContext())) {
//            if (!mAuth.getCurrentUser().isEmailVerified()) {
//                layoutVerifyEmail.setVisibility(View.VISIBLE);
//            } else {
//                layoutVerifyEmail.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        UserModel model = snapshot.toObject(UserModel.class).withId(snapshot.getId());
                        user_id = model.id;

                        active_status = model.getActive_status();
                        if (active_status == 1) {
                            switchOnlineOffline.setChecked(true);
                        } else {
                            switchOnlineOffline.setChecked(false);
                        }

                        tvUserName.setText(model.getUser_name());
                        tvUserEmail.setText(model.getEmail());
                        tvUserLevel.setText(model.getLevel());
                        tvUserCountry.setText(model.getCountry());
                        tvUserDOB.setText(model.getDateOfBirthString());
                        tvUserGender.setText(model.getGender());

                        if (!model.getUrl_photo().equals("")) {
                            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
                            circularProgressDrawable.setStrokeWidth(5f);
                            circularProgressDrawable.setCenterRadius(30f);
                            circularProgressDrawable.start();
                            GlideApp.with(getApplicationContext())
                                    .load(Uri.parse(model.getUrl_photo()))
                                    /*.apply(GlideOptions.circleCropTransform())*/
                                    .placeholder(circularProgressDrawable)
                                    .into(imgProfile);
                        }
                    }
                }
            });
        }
    }

    public void btnBack(View view) {
        finish();
    }
}
