package com.team29.speakingpartners.activity.voicecall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.Locale;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.RecentListModel;
import com.team29.speakingpartners.utils.GlideApp;
import com.team29.speakingpartners.utils.GlideOptions;

import javax.annotation.Nullable;

public class CallingViewActivity extends AppCompatActivity {

    public static final String TAG = CallingViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;

    private static String CHANNEL_ID = "Channel1";
    private static String FROM_EMAIL = "";
    private static String TO_EMAIL = "";
    private static String REQ_TOPIC = "";
    private static String DOC_ID = "";
    private static String FLAG = "";

    AppCompatImageButton btnSpeaker, btnLocalAudio, btnEndCall;
    AppCompatTextView tvChannelId, tvFromEmail, tvSpeakingTopic, tvConnectionStatus;
    AppCompatImageView imgProfile;

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onConnectionLost() {
            super.onConnectionLost();
            tvConnectionStatus.setText(getString(R.string.str_connection_lost));
        }

        @Override
        public void onUserOffline(final int uid, final int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                }
            });
        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) {
            onRemoteUserVoiceMuted(uid, muted);
        }

        @Override
        public void onConnectionInterrupted() {
            super.onConnectionInterrupted();
            tvConnectionStatus.setText(getString(R.string.str_connection_interrupted));
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            tvConnectionStatus.setText(getString(R.string.str_something_wrong));
        }

        /*@Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            tvConnectionStatus.setText(getString(R.string.str_connection_success));
        }*/

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
        }
    }

    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }

    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_calling_view);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                initAgoraEngineAndJoinChannel();
            }
        }

        getIntentExtraData();

        tvConnectionStatus = findViewById(R.id.connection_status);

        imgProfile = findViewById(R.id.profile_image);

        tvChannelId = findViewById(R.id.channel_id);
        tvChannelId.setText("Channel ID : " + CHANNEL_ID);

        tvFromEmail = findViewById(R.id.from_email);
        if (FLAG.equals("from")) {
            tvFromEmail.setText("With " + TO_EMAIL);
        } else if (FLAG.equals("to")) {
            tvFromEmail.setText("With " + FROM_EMAIL);
        }

        tvSpeakingTopic = findViewById(R.id.speaking_topic);
        tvSpeakingTopic.setText("Topic : " + REQ_TOPIC);

        btnSpeaker = findViewById(R.id.btn_speaker);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakerOnOff(v);
            }
        });

        btnLocalAudio = findViewById(R.id.btn_local_audio);
        btnLocalAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localAudioMuteUnMute(v);
            }
        });

        btnEndCall = findViewById(R.id.btn_end_call);
        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Have to store for recent data */
                storeRecentData();
                finish();
                /* Have to delete recent calling data */
                // deleteCallingData();
            }
        });
    }

    private void fetchJoiningStatus(final String s) {

        if (s.equals("from")) {
            setUserData(TO_EMAIL);
            tvFromEmail.setText("With " + TO_EMAIL);
            tvConnectionStatus.setText(getResources().getString(R.string.str_connected_process));
        } else if (s.equals("to")) {
            setUserData(FROM_EMAIL);
            tvFromEmail.setText("With " + FROM_EMAIL);
            tvConnectionStatus.setText(getResources().getString(R.string.str_connected_process));
        }
    }

    private void setUserData(String email) {
        mFirestore.collection("users")
                .whereEqualTo("email", email)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Listen Error");
                            return;
                        }

                        for (QueryDocumentSnapshot snapshot : snapshots) {
                            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
                            circularProgressDrawable.setCenterRadius(30f);
                            circularProgressDrawable.setStrokeWidth(5f);
                            imgProfile.setBackgroundDrawable(null);
                            GlideApp.with(getApplicationContext())
                                    .load(snapshot.getString("url_photo"))
                                    .apply(GlideOptions.centerCropTransform())
                                    .placeholder(circularProgressDrawable)
                                    .into(imgProfile);
                        }

                    }
                });
    }

    private void deleteCallingData() {
        DocumentReference delete = mFirestore.collection("calling").document(DOC_ID);
        delete.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Delete successful");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to delete");
                        finish();
                    }
                });
    }

    private void storeRecentData() {
        RecentListModel recentModel = new RecentListModel(
                CHANNEL_ID,
                REQ_TOPIC,
                FROM_EMAIL,
                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                new Date()
        );
        mFirestore.collection("recent")
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

    private void getIntentExtraData() {
        if (!getIntent().getExtras().getString("CHANNEL_ID").equals("")) {
            CHANNEL_ID = getIntent().getExtras().getString("CHANNEL_ID");
        }

        if (!getIntent().getExtras().getString("FROM_EMAIL").equals("")) {
            FROM_EMAIL = getIntent().getExtras().getString("FROM_EMAIL");
        }

        if (!getIntent().getExtras().getString("TO_EMAIL").equals("")) {
            TO_EMAIL = getIntent().getExtras().getString("TO_EMAIL");
        }

        if (!getIntent().getExtras().getString("REQ_TOPIC").equals("")) {
            REQ_TOPIC = getIntent().getExtras().getString("REQ_TOPIC");
        }

        if (!getIntent().getExtras().getString("FLAG").equals("")) {
            FLAG = getIntent().getExtras().getString("FLAG");
        }
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        joinChannel();
    }

    private void joinChannel() {
        if (mAuth.getCurrentUser().getEmail() != null) {
            mRtcEngine.joinChannel(null, CHANNEL_ID, "To : " + FROM_EMAIL, 0);
        }
    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getResources().getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
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
        /*storeRecentData();
        super.onBackPressed();*/
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void speakerOnOff(View view) {
        AppCompatImageButton iv = (AppCompatImageButton) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.color_blue_500), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    public void localAudioMuteUnMute(View view) {
        AppCompatImageButton iv = (AppCompatImageButton) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.color_blue_500), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

}
