package com.team29.speakingpartners.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.activity.voicecall.RequestReviewActivity;
import com.team29.speakingpartners.adapter.PendingListAdapter;
import com.team29.speakingpartners.model.CallingRequestListModel;
import com.team29.speakingpartners.model.UserModel;
import com.team29.speakingpartners.ui.DividerItemDecoration;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingFragment extends Fragment implements PendingListAdapter.ButtonItemClickListener {

    public static final String TAG = PendingFragment.class.getSimpleName();

    // Firebase
    private FirebaseFirestore mFirestore;

    private List<UserModel> mLists = new ArrayList<>();
    private PendingListAdapter mAdapter;
    RecyclerView mPendingListView;

    public PendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pending, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        mAdapter = new PendingListAdapter(getContext());
        mAdapter.setItemLists(new ArrayList<CallingRequestListModel>());
        mAdapter.setButtonClickListener(this);

        mPendingListView = root.findViewById(R.id.pending_list);
        mPendingListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPendingListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mPendingListView.setItemAnimator(new DefaultItemAnimator());
        mPendingListView.setAdapter(mAdapter);

        prepareData();

        return root;
    }

    private void prepareData() {

        Query callingQuery = mFirestore
                .collection("calling")
                .whereEqualTo("to_email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .whereEqualTo("from_status", true)
                .orderBy("date");
        callingQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                List<CallingRequestListModel> requestListModelList = new ArrayList<>();
                for (QueryDocumentSnapshot change : snapshots) {
                    CallingRequestListModel requestListModel = change.toObject(CallingRequestListModel.class);
                    requestListModelList.add(requestListModel);
                }

                mAdapter.setItemLists(requestListModelList);
            }
        });
    }

    @Override
    public void setOnAcceptButtonClick(String reqTopic, String fromEmail, String channelId) {
        Intent i = new Intent(getActivity(), RequestReviewActivity.class);
        i.putExtra("FROM_EMAIL", fromEmail);
        i.putExtra("REQ_TOPIC", reqTopic);
        i.putExtra("CHANNEL_ID", channelId);
        startActivity(i);
    }

    @Override
    public void setOnRejectButtonClick() {

    }
}
