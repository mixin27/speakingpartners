package com.team29.speakingpartners.activity.voicecall;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.CallingRequestListModel;
import com.team29.speakingpartners.model.UserModel;

import java.util.Date;

import javax.annotation.Nullable;

public class PreCallingDialogActivity extends AppCompatActivity {

    public static final String TAG = PreCallingDialogActivity.class.getSimpleName();

    String USER_EMAIL = "";
    String USER_LEVEL = "";

    AppCompatTextView tvUserName, tvUserLevel, tvUserCountry, tvUserEmail, tvUserGender, btnCancel, btnRequest;
    AppCompatImageView imgUserProfile;
    AppCompatSpinner spTopic;

    FirebaseFirestore mFirestore;

    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_calling_layout);

        // FirebaseFirestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get intent data
        getIntentData();

        imgUserProfile = findViewById(R.id.pre_calling_profile_img);

        tvUserName = findViewById(R.id.tv_pre_calling_user_name);
        tvUserLevel = findViewById(R.id.tv_pre_calling_level);
        tvUserCountry = findViewById(R.id.tv_pre_calling_country);
        tvUserGender = findViewById(R.id.tv_pre_calling_gender);
        tvUserEmail = findViewById(R.id.tv_pre_calling_email);

        // DataSet
        setPartnerDetail();

        btnRequest = findViewById(R.id.pre_calling_request);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(USER_EMAIL)) {
                    CallingRequestListModel model = new CallingRequestListModel(
                            FirebaseAuth.getInstance().getUid(),
                            true,
                            false,
                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            USER_EMAIL,
                            new Date()
                    );

                    mFirestore.collection("calling")
                            .add(model)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "Join Channel Success");
                                    Toast.makeText(getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error at joining channel");
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot make call yourself!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btnCancel = findViewById(R.id.pre_calling_request_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setPartnerDetail() {
        if (!USER_EMAIL.equals("")) {
            Query query = mFirestore.collection("users").whereEqualTo("email", USER_EMAIL);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen error", e);
                        return;
                    }

                    if (snapshots != null) {
                        for (QueryDocumentSnapshot snapshot: snapshots) {
                            userModel = snapshot.toObject(UserModel.class);
                            Log.d(TAG, userModel.getEmail());
                            tvUserName.setText(userModel.getUser_name());
                            tvUserLevel.setText(userModel.getLevel());
                            tvUserCountry.setText(userModel.getCountry());
                            tvUserGender.setText(userModel.getGender());
                            tvUserEmail.setText(userModel.getEmail());

                            if (!userModel.getUrl_photo().equals("")) {
                                Glide.with(getApplicationContext())
                                        .load(userModel.getUrl_photo())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(imgUserProfile);
                            }
                        }
                    }
                }
            });
        }
    }

    private void getIntentData() {
        if (!getIntent().getStringExtra("USER_EMAIL").isEmpty()) {
            USER_EMAIL = getIntent().getStringExtra("USER_EMAIL");
        }

        if (!getIntent().getStringExtra("USER_LEVEL").isEmpty()) {
            USER_LEVEL = getIntent().getStringExtra("USER_LEVEL");
        }
    }
}
