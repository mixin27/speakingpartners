package com.team29.speakingpartners.activity.voicecall;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.CallingRequestListModel;
import com.team29.speakingpartners.model.UserModel;
import com.team29.speakingpartners.net.ConnectionChecking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class PreCallingDialogActivity extends AppCompatActivity {

    public static final String TAG = PreCallingDialogActivity.class.getSimpleName();

    String USER_EMAIL = "";
    String USER_LEVEL = "";

    AppCompatTextView tvUserName, tvUserLevel, tvUserCountry, tvUserEmail, tvUserGender;
    AppCompatButton btnCancel, btnRequest, btnDirectCall;
    AppCompatImageView imgUserProfile;
    AppCompatSpinner spTopic;

    FirebaseFirestore mFirestore;

    UserModel userModel;

    List<String> topicLists = new ArrayList<>();

    ArrayAdapter<String> mAdapter;

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

        spTopic = findViewById(R.id.sp_pre_calling_topic);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, topicLists);
        spTopic.setAdapter(mAdapter);

        btnRequest = findViewById(R.id.pre_calling_request);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (topicLists.size() > 0) {
                    doRequest();
                } else {
                    Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDirectCall = findViewById(R.id.pre_calling_request_call);
        btnDirectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionChecking.checkConnection(PreCallingDialogActivity.this)) {
                    if (topicLists.size() > 0) {
                        doCall();
                    } else {
                        Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
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

    void doRequest() {
        btnRequest.setEnabled(false);
        btnRequest.setTextColor(getResources().getColor(R.color.color_grey));

        if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(USER_EMAIL)) {
            final CallingRequestListModel model = new CallingRequestListModel(
                    UUID.randomUUID().toString(),
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    USER_EMAIL,
                    1,
                    0,
                    2,
                    spTopic.getSelectedItem().toString(),
                    new Date()
            );

            mFirestore.collection("calling")
                    .add(model)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Request Success");
                            Toast.makeText(getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Request Error");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "You cannot make call yourself!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void doCall() {
        btnDirectCall.setEnabled(false);

        if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(USER_EMAIL)) {
            final CallingRequestListModel model = new CallingRequestListModel(
                    UUID.randomUUID().toString(),
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    USER_EMAIL,
                    1,
                    0,
                    1,
                    spTopic.getSelectedItem().toString(),
                    new Date()
            );

            mFirestore.collection("calling")
                    .add(model)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Request Success");
                            Toast.makeText(getApplicationContext(), "Call Success", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(PreCallingDialogActivity.this, CallSplashActivity.class);
                            i.putExtra("REQ_MODEL", model);
                            startActivity(i);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Calling Error");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "You cannot make call yourself!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void setDataToTopicSpinner() {
        String id = "1";

        Log.d("level",tvUserLevel.getText().toString());
        switch (tvUserLevel.getText().toString()) {
            case "Elementary":
                id = "1";
                break;
            case "Upper-intermediate":
                id = "2";
                break;
            case "Intermediate":
                id = "3";
                break;
            case "Advance":
                id = "4";
                break;
            case "Native":
                id = "4";
                break;
        }

        Log.d(TAG, "TOPICS DOC_ID = " + id);
         mFirestore.collection("skills")
                 .document(id)
                 .collection("topics")
                 .get()
                 .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                     @Override
                     public void onSuccess(QuerySnapshot snapshots) {
                         topicLists.clear();
                         for (QueryDocumentSnapshot snapshot : snapshots) {
                             topicLists.add(snapshot.getString("title"));
                         }
                         mAdapter.notifyDataSetChanged();
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

                        setDataToTopicSpinner();

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
