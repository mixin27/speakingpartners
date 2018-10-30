package com.team29.speakingpartners.activity.voicecall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import com.team29.speakingpartners.R;

public class CallingViewActivity extends AppCompatActivity {

    public static final String TAG = CallingViewActivity.class.getSimpleName();

    private static final String CHANNEL_ID = "Channel1";

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;

    AppCompatTextView tvJoinChannel;

    FirebaseAuth mAuth;

    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onConnectionLost() {
            super.onConnectionLost();
            tvJoinChannel.setText("Connection Lost");
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
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
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
        setContentView(R.layout.activity_calling_view);

        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                initAgoraEngineAndJoinChannel();
            }
        }

        tvJoinChannel = findViewById(R.id.tvJoinChannel);
        tvJoinChannel.setText("From : " + mAuth.getCurrentUser().getEmail());

    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        joinChannel();
    }

    private void joinChannel() {
        if (mAuth.getCurrentUser().getEmail() != null) {
            mRtcEngine.joinChannel(null, CHANNEL_ID, "Extra optional data", 0);
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

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void btnClickSpeaker(View view) {
        AppCompatButton iv = (AppCompatButton) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.setTextColor(getResources().getColor(R.color.color_black));
        } else {
            iv.setSelected(true);
            iv.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    public void btnClickAudio(View view) {
        AppCompatButton iv = (AppCompatButton) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.setTextColor(getResources().getColor(R.color.color_black));
        } else {
            iv.setSelected(true);
            iv.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    public void btnClickEnd(View view) {
        finish();
    }

}
