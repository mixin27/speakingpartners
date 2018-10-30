package com.team29.speakingpartners.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.adapter.RecentListAdpater;
import com.team29.speakingpartners.model.RecentListModel;
import com.team29.speakingpartners.ui.DividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment {

    public static final String TAG = RecentFragment.class.getSimpleName();

    // Firebase
    private FirebaseFirestore mFriestore;

    private List<RecentListModel> mLists = new ArrayList<>();
    private RecentListAdpater mAdapter;
    private RecyclerView mRecentListView;

    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_recent, container, false);

        // FirebaseFirestore
        mFriestore = FirebaseFirestore.getInstance();

        mAdapter = new RecentListAdpater(getContext(), mLists);

        // RecyclerView
        mRecentListView = (RecyclerView) root.findViewById(R.id.recent_list);
        mRecentListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecentListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mRecentListView.setItemAnimator(new DefaultItemAnimator());
        mRecentListView.setAdapter(mAdapter);

        prepareData();

        return root;
    }

    private void prepareData() {
        mLists.clear();

        mLists.add(new RecentListModel("Zayar Tun"));

        mAdapter.notifyDataSetChanged();
    }

}
