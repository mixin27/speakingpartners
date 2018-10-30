package com.team29.speakingpartners.fragment;


import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.adapter.ActiveUserListAdapter;
import com.team29.speakingpartners.model.UserModel;
import com.team29.speakingpartners.net.ConnectionChecking;
import com.team29.speakingpartners.ui.DividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveUserFragment extends Fragment {

    public static final String TAG = ActiveUserFragment.class.getSimpleName();

    // FirebaseFirestore
    FirebaseFirestore mFirestore;

    private ActiveUserListAdapter mAdapter;
    RecyclerView mActiveUserList;

    public ActiveUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_active_user, container, false);

        // Firestore
//        FirebaseFirestoreSettings firestoreSettings = new FirebaseFirestoreSettings.Builder()
//                .setTimestampsInSnapshotsEnabled(true)
//                .setPersistenceEnabled(true)
//                .build();
        mFirestore = FirebaseFirestore.getInstance();
//        mFirestore.setFirestoreSettings(firestoreSettings);

        mActiveUserList = root.findViewById(R.id.active_user_list);

        mAdapter = new ActiveUserListAdapter(getContext());
        mAdapter.setmLists(new ArrayList<UserModel>());

        mActiveUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        mActiveUserList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mActiveUserList.setItemAnimator(new DefaultItemAnimator());
        mActiveUserList.setAdapter(mAdapter);

        prepareData();

        return root;
    }

    private void prepareData() {
        fetchAllUser();
    }

    private void fetchAllUser() {

        Query query = mFirestore.collection("users").whereEqualTo("active_status", 1);
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
                    data.add(userModel);
                }
                mAdapter.setmLists(data);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
