package com.team29.speakingpartners.activity.voicecall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.MainActivity;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.helper.CheckPermissionForApp;
import com.team29.speakingpartners.model.CallingRequestListModel;
import com.team29.speakingpartners.model.RecentListModel;
import com.team29.speakingpartners.utils.GlideApp;
import com.team29.speakingpartners.utils.GlideOptions;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class IncomingSplashActivity extends AppCompatActivity {

    private static final String TAG = IncomingSplashActivity.class.getSimpleName();

    MediaPlayer mediaPlayer;

    /* Agora Engine */
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Toast.makeText(getApplicationContext(), "User Joined", Toast.LENGTH_SHORT).show();
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
    LinearLayout incomingButtonLayout, incomingAcceptButtonLayout;

    AppCompatImageView profileImageView;
    AppCompatImageButton btnAcceptCall, btnRejectCall, btnEndCall, btnSpeaker, btnAudio;
    AppCompatTextView tvUserName, tvUserLevel, tvUserTopic, tvDuration;

    FirebaseAuth mAuth;
    FirebaseFirestore mDB;

    CallingRequestListModel callingRequestModel;
    String docId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full Screen Mode
        enableFullScreen();

        setContentView(R.layout.activity_incoming_splash);

        // Ringtone
        setUpRingTone();

        // Firebase
        setUpFirebase();

        // Get Intent Data
        getIntentData();

        // Bind UI View
        setUpUI();
        setIncomingUserInformation();

        // Reject Call
        actionButtonReject();
        // Accept Call
        actionButtonAccept();
        // Speaker
        actionButtonSpeaker();
        // End Call
        actionButtonEndCall();
        // Audio
        actionButtonAudio();

        listenRejected();

    }

    private void setUpRingTone() {
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        try {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void listenRejected() {
        mDB.collection("calling")
                .whereEqualTo("channel_id", callingRequestModel.getChannel_id())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen Error");
//                                return;
                        }

                        for (DocumentChange change : snapshots.getDocumentChanges()) {

                            if(change.getType().equals(DocumentChange.Type.REMOVED)) {
                                Log.d(TAG, "REMOVE");
                                finish();
                            }
                        }
                    }
                });
    }

    // Intent sharing data
    private void getIntentData() {
        if (!getIntent().getSerializableExtra("REQ_MODEL").equals("")) {
            callingRequestModel = (CallingRequestListModel) getIntent().getSerializableExtra("REQ_MODEL");
        }

        if (!getIntent().getStringExtra("ID").equals("")) {
            docId = getIntent().getStringExtra("ID");
            Log.d(TAG, "DOC_ID => " + docId);
        }
    }

    private void actionButtonReject() {
        btnRejectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                doReject();
            }
        });
    }

    private void doReject() {
        DocumentReference docRef = mDB.collection("calling")
                .document(docId);
        docRef.delete()
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

    private void actionButtonEndCall() {
        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReject();
                finish();
            }
        });
    }

    private void actionButtonAccept() {

        btnAcceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.stop();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                toggleButtonLayout();

                tvDuration.setVisibility(View.VISIBLE);
                tvDuration.setText("Join Successful");
                //stopService(new Intent(IncomingSplashActivity.this, CallingStateService.class));
                doAccept();

                // Agora Engine Initialization
                initAgoraEngine();
            }
        });
    }

    private void doAccept() {
        DocumentReference docRef = mDB.collection("calling")
                .document(docId);
        docRef.update("to_status", 1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void storeRecentData() {
        RecentListModel recentModel = new RecentListModel(
                callingRequestModel.getChannel_id(),
                callingRequestModel.getReq_topic(),
                callingRequestModel.getFrom_email(),
                callingRequestModel.getTo_email(),
                new Date()
        );
        mDB.collection("recent")
                .add(recentModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d(TAG, "Recent add successful!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Recent add failed!");
                        Toast.makeText(getApplicationContext(), "Storing recent data failed", Toast.LENGTH_SHORT).show();
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

    // Toggle Button Layout
    private void toggleButtonLayout() {
        incomingButtonLayout.setVisibility(View.GONE);
        incomingAcceptButtonLayout.setVisibility(View.VISIBLE);
    }

    private void initAgoraEngine() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CheckPermissionForApp.checkSelfPermission(
                    IncomingSplashActivity.this, Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                initAgoraEngineAndJoinChannel();
            }
        }
    }

    // User Information
    private void setIncomingUserInformation() {
        mDB.collection("users")
                .whereEqualTo("email", callingRequestModel.getFrom_email())
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

    // Firebase
    private void setUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings firestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(false)
                /*.setPersistenceEnabled(false)*/
                .build();
        mDB.setFirestoreSettings(firestoreSettings);
    }

    // Initialize view
    private void setUpUI() {
        splashLayout = findViewById(R.id.incoming_splash_layout);
        incomingButtonLayout = findViewById(R.id.incoming_call_btn_layout);
        incomingAcceptButtonLayout = findViewById(R.id.incoming_accept_call_btn_layout);

        profileImageView = findViewById(R.id.incoming_splash_profile_image);

        btnAcceptCall = findViewById(R.id.incoming_splash_accept_btn);
        btnRejectCall = findViewById(R.id.incoming_splash_reject_btn);
        btnEndCall = findViewById(R.id.incoming_splash_end_btn);

        btnSpeaker = findViewById(R.id.incoming_splash_speaker_btn);
        btnAudio = findViewById(R.id.incoming_splash_audio_btn);

        tvUserName = findViewById(R.id.incoming_splash_name);
        tvUserLevel = findViewById(R.id.incoming_splash_level);
        tvUserTopic = findViewById(R.id.incoming_splash_topic);
        tvDuration = findViewById(R.id.incoming_splash_duration);
    }

    /* Full Screen Mode */
    private void enableFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /* Agora Engine */
    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        joinChannel();
    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, callingRequestModel.getChannel_id(), "", 0);
        Log.d(TAG, "Join channel successful");
    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getResources().getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showSnackBar("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;

        storeRecentData();
    }

    @Override
    public void onBackPressed() {
        Log.w(TAG, "Back key blocking");
    }
}
