package com.team29.speakingpartners.activity.voicecall;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.Date;
import java.util.UUID;

import javax.annotation.Nullable;

public class RequestReviewActivity extends AppCompatActivity {

    public static final String TAG = RequestReviewActivity.class.getSimpleName();

    CallingRequestListModel requestListModel;
    String id;

    FirebaseFirestore mFirestore;

    private AppCompatImageView imgProfile;
    private AppCompatTextView tvUserName, tvUserLevel, tvUserCountry, tvUserGender, tvUserEmail, tvRequestTopic;
    private AppCompatButton btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_review);

        mFirestore = FirebaseFirestore.getInstance();

        getIntentExtraData();

        imgProfile = findViewById(R.id.calling_request_profile_img);
        tvUserName = findViewById(R.id.tv_calling_request_user_name);
        tvUserLevel = findViewById(R.id.tv_calling_request_level);
        tvUserGender = findViewById(R.id.tv_calling_request_gender);
        tvUserCountry = findViewById(R.id.tv_calling_request_country);

        fetchRequestUserInformation();

        tvUserEmail = findViewById(R.id.tv_calling_request_email);
        tvUserEmail.setText(requestListModel.getFrom_email());

        tvRequestTopic = findViewById(R.id.tv_calling_request_topic);
        tvRequestTopic.setText(requestListModel.getReq_topic());

        btnStart = findViewById(R.id.calling_request_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCall();
            }
        });

    }

    void doCall() {
        if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(requestListModel.getFrom_email())) {
            final CallingRequestListModel model = new CallingRequestListModel(
                    UUID.randomUUID().toString(),
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    requestListModel.getFrom_email(),
                    1,
                    0,
                    1,
                    requestListModel.getReq_topic(),
                    new Date()
            );

            mFirestore.collection("calling")
                    .add(model)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Request Success");
                            Toast.makeText(getApplicationContext(), "Call Success", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RequestReviewActivity.this, CallSplashActivity.class);
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

    private void getIntentExtraData() {
        if (getIntent().getSerializableExtra("REQ_MODEL") != null) {
            requestListModel = (CallingRequestListModel) getIntent().getSerializableExtra("REQ_MODEL");
            Log.d(TAG, "From : " + requestListModel.getFrom_email());
        }

        if (getIntent().getStringExtra("ID") != null) {
            id = getIntent().getStringExtra("ID");
        }
    }

    private void fetchRequestUserInformation() {
        Query query = mFirestore.collection("users")
                .whereEqualTo("email", requestListModel.getFrom_email());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Listen Error");
                    return;
                }

                for (QueryDocumentSnapshot snapshot : snapshots) {
                    String user_name = snapshot.getString("user_name");
                    String gender = snapshot.getString("gender");
                    String country = snapshot.getString("country");
                    String level = snapshot.getString("level");
                    String url_photo = snapshot.getString("url_photo");

                    tvUserName.setText(user_name);
                    tvUserGender.setText(gender);
                    tvUserCountry.setText(country);
                    tvUserLevel.setText(level);

                    if (!url_photo.equals("")) {
                        Glide.with(getApplicationContext())
                                .load(url_photo)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imgProfile);
                    }

                }
            }
        });
    }
}
