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
import com.team29.speakingpartners.adapter.PendingListAdpater;
import com.team29.speakingpartners.model.PendingListModel;
import com.team29.speakingpartners.ui.DividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingFragment extends Fragment {

    public static final String TAG = PendingFragment.class.getSimpleName();

    // Firebase
    private FirebaseFirestore firestore;

    private List<PendingListModel> mLists = new ArrayList<>();
    private PendingListAdpater mAdapter;
    RecyclerView mPendingListView;

    public PendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pending, container, false);

        firestore = FirebaseFirestore.getInstance();

        mAdapter = new PendingListAdpater(getContext(), mLists);

        mPendingListView = (RecyclerView) root.findViewById(R.id.pending_list);
        mPendingListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPendingListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mPendingListView.setItemAnimator(new DefaultItemAnimator());
        mPendingListView.setAdapter(mAdapter);

        prepareData();

        return root;
    }

    private void prepareData() {
        mLists.clear();

        mLists.add(new PendingListModel("Henry", "Beginner"));

        mAdapter.notifyDataSetChanged();
    }

}
