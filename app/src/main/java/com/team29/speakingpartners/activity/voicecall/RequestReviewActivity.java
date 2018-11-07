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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.CallingRequestListModel;

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

                DocumentReference docRef = mFirestore.collection("calling")
                        .document(id);
                docRef.update("to_status", true)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "Accept Successful");
                            }
                        });

                Intent i = new Intent(RequestReviewActivity.this, ToCallingViewActivity.class);
                i.putExtra("REQ_MODEL", requestListModel);
                startActivity(i);
                finish();
            }
        });

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
