package com.team29.speakingpartners.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.activity.voicecall.IncomingSplashActivity;
import com.team29.speakingpartners.adapter.IncomingRecentListAdapter;
import com.team29.speakingpartners.model.RecentListModel;
import com.team29.speakingpartners.ui.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncomingRecentFragment extends Fragment implements IncomingRecentListAdapter.RecentItemClickListener {

    public static final String TAG = IncomingRecentFragment.class.getSimpleName();

    ProgressDialog progressDialog;

    // Firebase
    FirebaseFirestore mFirestore;

    private List<RecentListModel> mLists = new ArrayList<>();
    private IncomingRecentListAdapter mAdapter;
    RecyclerView mRecentListView;

    AppCompatTextView emptyRecentView;

    public IncomingRecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_incoming_recent, container, false);

        // FirebaseFirestore
        mFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getContext());

        emptyRecentView = root.findViewById(R.id.empty_incoming_recent_view);

        mAdapter = new IncomingRecentListAdapter(getContext());
        mAdapter.setItemLists(mLists);
        mAdapter.setRecentItemClickListener(this);

        // RecyclerView
        mRecentListView = root.findViewById(R.id.incoming_recent_list);
        mRecentListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecentListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mRecentListView.setItemAnimator(new DefaultItemAnimator());
        mRecentListView.setAdapter(mAdapter);

        prepareData();

        return root;
    }

    private void prepareData() {
        Query query = mFirestore
                .collection("recent")
                .whereEqualTo("to_email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
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
                    mLists.add(requestListModel);
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

    @Override
    public boolean setOnItemLongClick(final RecentListModel model) {
        new AlertDialog.Builder(getContext())
                .setMessage("Are you sure want to delete?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();
                        DocumentReference docRef = mFirestore.collection("recent")
                                .document(model.id);
                        docRef.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
        .show();
        return false;
    }

}
