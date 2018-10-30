package com.team29.speakingpartners.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.adapter.EachRecentListAdapter;
import com.team29.speakingpartners.model.RecentListModel;
import com.team29.speakingpartners.ui.DividerItemDecoration;

public class EachRecentListActivity extends AppCompatActivity {

    public static final String TAG = EachRecentListActivity.class.getSimpleName();
    public static final String NAME = "Partner";

    RecyclerView mEachRecentListView;
    List<RecentListModel> mLists;
    EachRecentListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_recent_list);

        // ActionBar
        setUpActionBar();

        mAdapter = new EachRecentListAdapter(this, mLists);

        mEachRecentListView = findViewById(R.id.each_recent_list);
        mEachRecentListView.setLayoutManager(new LinearLayoutManager(this));
        mEachRecentListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mEachRecentListView.setItemAnimator(new DefaultItemAnimator());
        mEachRecentListView.setAdapter(mAdapter);

        prepareData();

    }

    private void prepareData() {
        mLists.clear();

        getEachRecentAllData();
    }

    private void getEachRecentAllData() {

    }

    private void setUpActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(NAME);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
