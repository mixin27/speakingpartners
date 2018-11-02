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
        mAdapter.setItemLists(new ArrayList<UserModel>());
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
        Query query = mFirestore.collection("users").orderBy("user_name");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                List<UserModel> data = new ArrayList<>();
                for (QueryDocumentSnapshot change : queryDocumentSnapshots) {

                    UserModel userModel = change.toObject(UserModel.class);
                    Log.d(TAG, "Model" + userModel.getEmail());
                    data.add(userModel);
                }
                mAdapter.setItemLists(data);
            }
        });
    }

    @Override
    public void setOnAcceptButtonClick(UserModel userModel) {
        Intent i = new Intent(getActivity(), RequestReviewActivity.class);
        i.putExtra("TO_EMAIL", "");
        startActivity(i);
    }

    @Override
    public void setOnRejectButtonClick() {

    }
}
