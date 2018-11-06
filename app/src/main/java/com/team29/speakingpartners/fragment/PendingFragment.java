package com.team29.speakingpartners.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
import com.team29.speakingpartners.ui.DividerItemDecoration;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingFragment extends Fragment implements PendingListAdapter.ButtonItemClickListener {

    public static final String TAG = PendingFragment.class.getSimpleName();

    // Firebase
    private FirebaseFirestore mFirestore;
    private PendingListAdapter mAdapter;

    private List<CallingRequestListModel> requestListModelList = new ArrayList<>();
    RecyclerView mPendingListView;

    AppCompatTextView emptyTextView;

    public PendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pending, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        emptyTextView = root.findViewById(R.id.empty_pending_view);

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
                .whereEqualTo("to_status", false)
                .orderBy("date");

        callingQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                checkEmptyData(snapshots);

                requestListModelList.clear();
                for (QueryDocumentSnapshot change : snapshots) {
                    CallingRequestListModel requestListModel = change.toObject(CallingRequestListModel.class).withId(change.getId());
                    requestListModelList.add(requestListModel);
                }

                mAdapter.setItemLists(requestListModelList);
            }
        });
    }

    private void checkEmptyData(QuerySnapshot snapshots) {
        if (snapshots.getDocuments().size() == 0) {
            mPendingListView.setVisibility(View.GONE);
            Log.d(TAG, "ListView : GONE");
            emptyTextView.setVisibility(View.VISIBLE);
            Log.d(TAG, "EmptyView : VISIBLE");
        } else {
            emptyTextView.setVisibility(View.GONE);
            Log.d(TAG, "EmptyView : GONE");
            mPendingListView.setVisibility(View.VISIBLE);
            Log.d(TAG, "ListView : VISIBLE");
        }
    }

    @Override
    public void setOnAcceptButtonClick(CallingRequestListModel model, String docId) {
        Intent i = new Intent(getActivity(), RequestReviewActivity.class);
        i.putExtra("FROM_EMAIL", model.getFrom_email());
        i.putExtra("TO_EMAIL", model.getTo_email());
        i.putExtra("REQ_TOPIC", model.getReq_topic());
        i.putExtra("CHANNEL_ID", model.getChannel_id());
        i.putExtra("DOC_ID", docId);
        i.putExtra("FLAG", "to");
        startActivity(i);
    }

    @Override
    public void setOnRejectButtonClick(String docId) {
        DocumentReference docRef = mFirestore.collection("calling").document(docId);
        docRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Delete Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to delete");
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        //checkEmptyData();
    }
}
