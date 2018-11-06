package com.team29.speakingpartners.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;

import com.team29.speakingpartners.R;

public class TopicGuideDetailActivity extends AppCompatActivity {

    AppCompatTextView tvTopicGuideLines;
    AppCompatTextView tvTopicTitle;

    String[] strArray;
    String guideLines = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_guide_detail);

        // ActionBar
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Guides");
        }

        tvTopicGuideLines = findViewById(R.id.tv_topic_guide_lines);
        tvTopicTitle = findViewById(R.id.topic_detail_title);

        if (!getIntent().getStringExtra("TITLE").equals("")) {
            tvTopicTitle.setText(getIntent().getStringExtra("TITLE"));
        }

        if (!getIntent().getStringExtra("GUIDES").equals("")) {
            strArray = getIntent().getStringExtra("GUIDES").split("#");
        }

        for (String s : strArray) {
            guideLines += "\n" + s + "\n";
        }
        tvTopicGuideLines.setText(guideLines);

    }
}
