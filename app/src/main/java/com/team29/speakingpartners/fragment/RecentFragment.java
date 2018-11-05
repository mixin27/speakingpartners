package com.team29.speakingpartners.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.team29.speakingpartners.adapter.RecentListAdapter;
import com.team29.speakingpartners.model.RecentListModel;
import com.team29.speakingpartners.ui.DividerItemDecoration;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment implements RecentListAdapter.RecentItemClickListener {

    public static final String TAG = RecentFragment.class.getSimpleName();

    // Firebase
    FirebaseFirestore mFirestore;

    private List<RecentListModel> mLists = new ArrayList<>();
    private RecentListAdapter mAdapter;
    RecyclerView mRecentListView;

    AppCompatTextView emptyRecentView;

    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_recent, container, false);

        // FirebaseFirestore
        mFirestore = FirebaseFirestore.getInstance();

        emptyRecentView = root.findViewById(R.id.empty_recent_view);

        mAdapter = new RecentListAdapter(getContext());
        mAdapter.setItemLists(mLists);
        mAdapter.setRecentItemClickListener(this);

        // RecyclerView
        mRecentListView = root.findViewById(R.id.recent_list);
        mRecentListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecentListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mRecentListView.setItemAnimator(new DefaultItemAnimator());
        mRecentListView.setAdapter(mAdapter);

        prepareData();

        return root;
    }

    private void prepareData() {
        Query query = mFirestore
                .collection("recent")
                /*.whereEqualTo("to_email", FirebaseAuth.getInstance().getCurrentUser().getEmail())*/
                .orderBy("date_time");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                checkEmptyData(snapshots);

                mLists.clear();
                for (QueryDocumentSnapshot change : snapshots) {
                    RecentListModel requestListModel = change.toObject(RecentListModel.class).withId(change.getId());

                    if (requestListModel.getFrom_email().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        mLists.add(requestListModel);
                    } else if (requestListModel.getTo_email().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        mLists.add(requestListModel);
                    }
                }

                mAdapter.setItemLists(mLists);
            }
        });
    }

    private void checkEmptyData(QuerySnapshot snapshots) {
        if (snapshots.getDocuments().size() == 0) {
            mRecentListView.setVisibility(View.GONE);
            Log.d(TAG, "ListView : GONE");
            emptyRecentView.setVisibility(View.VISIBLE);
            Log.d(TAG, "EmptyView : VISIBLE");
        } else {
            emptyRecentView.setVisibility(View.GONE);
            Log.d(TAG, "EmptyView : GONE");
            mRecentListView.setVisibility(View.VISIBLE);
            Log.d(TAG, "ListView : VISIBLE");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setOnItemClick(RecentListModel model) {
        Toast.makeText(getContext(), model.getChannel_id(), Toast.LENGTH_SHORT).show();
    }
}
