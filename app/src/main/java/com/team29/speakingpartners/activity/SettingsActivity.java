package com.team29.speakingpartners.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.team29.speakingpartners.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
