package com.team29.speakingpartners.activity.voicecall;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.background.CallingStateService;
import com.team29.speakingpartners.helper.CheckPermissionForApp;
import com.team29.speakingpartners.model.CallingRequestListModel;
import com.team29.speakingpartners.model.RecentListModel;
import com.team29.speakingpartners.utils.GlideApp;
import com.team29.speakingpartners.utils.GlideOptions;

import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class CallSplashActivity extends AppCompatActivity {

    private static final String TAG = IncomingSplashActivity.class.getSimpleName();

    /* Agora Engine */
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
        }

        @Override
        public void onUserMuteAudio(int uid, boolean muted) {
            super.onUserMuteAudio(uid, muted);
            onRemoteUserVoiceMuted(uid, muted);
        }

        @Override
        public void onUserOffline(final int uid, final int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                    // doReject();
                    finish();
                }
            });
        }
    };

    ConstraintLayout splashLayout;

    AppCompatImageView profileImageView;
    AppCompatImageButton btnEndCall, btnSpeaker, btnAudio;
    AppCompatTextView tvUserName, tvUserLevel, tvUserTopic;

    FirebaseAuth mAuth;
    FirebaseFirestore mDB;

    CallingRequestListModel callingRequestModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_call_splash);

        // Firebase
        setUpFirebase();

        // Get Intent Data
        getIntentData();

        // Bind UI View
        setUpUI();
        setOutgoingUserInformation();

        // Agora Engine
        initAgoraEngine();

        // Speaker
        actionButtonSpeaker();

        // End Call
        actionButtonEndCall();

        // Audio
        actionButtonAudio();

        //listenAcceptOrReject();
    }

    private void listenAcceptOrReject() {
        mDB.collection("calling")
                .whereEqualTo("channel_id", callingRequestModel.getChannel_id())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (QueryDocumentSnapshot snapshot : snapshots) {
                            if (Integer.parseInt(snapshot.get("to_status").toString()) == 2) {
                                finish();
                            }
                        }
                    }
                });
    }

    // Firebase
    private void setUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();
    }

    // Intent sharing data
    private void getIntentData() {
        if (!getIntent().getSerializableExtra("REQ_MODEL").equals("")) {
            callingRequestModel = (CallingRequestListModel) getIntent().getSerializableExtra("REQ_MODEL");
        }
    }

    // Initialize view
    private void setUpUI() {
        splashLayout = findViewById(R.id.calling_splash_layout);

        profileImageView = findViewById(R.id.calling_splash_profile_image);

        btnEndCall = findViewById(R.id.calling_splash_end_btn);

        btnSpeaker = findViewById(R.id.calling_splash_speaker_btn);
        btnAudio = findViewById(R.id.calling_splash_audio_btn);

        tvUserName = findViewById(R.id.calling_splash_name);
        tvUserLevel = findViewById(R.id.calling_splash_level);
        tvUserTopic = findViewById(R.id.calling_splash_topic);
    }

    // User Information
    private void setOutgoingUserInformation() {
        mDB.collection("users")
                .whereEqualTo("email", callingRequestModel.getTo_email())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Listen Error");
                            return;
                        }

                        if (snapshots != null) {
                            for (QueryDocumentSnapshot snapshot : snapshots) {

                                tvUserName.setText(snapshot.getString("user_name"));
                                tvUserLevel.setText(snapshot.getString("level"));
                                tvUserTopic.setText(callingRequestModel.getReq_topic());

                                if (!snapshot.getString("url_photo").equals("")) {
                                    profileImageView.setBackgroundDrawable(null);
                                    GlideApp.with(getApplicationContext())
                                            .load(snapshot.getString("url_photo"))
                                            .apply(GlideOptions.circleCropTransform())
                                            .into(profileImageView);
                                }
                            }
                        }

                    }
                });
    }

    private void initAgoraEngine() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CheckPermissionForApp.checkSelfPermission(
                    CallSplashActivity.this, Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                initAgoraEngineAndJoinChannel();
            }
        }
    }

    private void actionButtonEndCall() {
        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                // doReject();
            }
        });
    }

    private void doReject() {
        mDB.collection("calling")
                .whereEqualTo("channel_id", callingRequestModel.getChannel_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                doRejectUpdate(snapshot.getId());
                            }
                        }
                    }
                });
    }

    private void doRejectUpdate(String docId) {
        DocumentReference docRef = mDB.collection("calling")
                .document(docId);
        docRef.update("to_status", 2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                    }
                });
    }


    boolean isAudioOn = true;
    private void actionButtonAudio() {
        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioOn) {
                    mRtcEngine.muteLocalAudioStream(true);
                    btnAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_off_black_24dp));
                } else {
                    mRtcEngine.muteLocalAudioStream(false);
                    btnAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none_black_24dp));
                }
                isAudioOn = !isAudioOn;
            }
        });
    }

    boolean isSpeakerOn = true;
    private void actionButtonSpeaker() {
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSpeakerOn) {
                    mRtcEngine.setEnableSpeakerphone(false);
                    btnSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_off_black_24dp));
                } else {
                    mRtcEngine.setEnableSpeakerphone(true);
                    btnSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_black_24dp));
                }
                isSpeakerOn = !isSpeakerOn;
            }
        });
    }

    /* Agora Engine */
    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        joinChannel();
    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getResources().getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, callingRequestModel.getChannel_id(), "", 0);
        Log.d(TAG, "Join channel successful");
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }


    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showSnackBar(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }

    private void onRemoteUserLeft(int uid, int reason) {
        showSnackBar(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
    }

    private void showSnackBar(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(splashLayout, msg, BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    @Override
    public void onBackPressed() {
        Log.w(TAG, "Back key blocking");
    }
}
