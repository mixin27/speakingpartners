package com.team29.speakingpartners.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.activity.TopicGuideDetailActivity;
import com.team29.speakingpartners.adapter.TopicGuideListAdapter;
import com.team29.speakingpartners.model.TopicGuideModel;
import com.team29.speakingpartners.model.UserModel;
import com.team29.speakingpartners.ui.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements TopicGuideListAdapter.TopicGuideItemClickListener {

    public static final String TAG = HomeFragment.class.getSimpleName();

    RecyclerView mElementaryLists, mIntermediateLists, mUpperIntermediateLists, mAdvanceLists;
    TopicGuideListAdapter mElementaryAdapter, mIntermediateAdapter, mUpperIntermediateAdapter, mAdvanceAdapter;

    FirebaseFirestore mFirestore;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        // Elementary
        mElementaryAdapter = new TopicGuideListAdapter(getContext());
        mElementaryAdapter.setItemLists(new ArrayList<TopicGuideModel>());
        mElementaryAdapter.setTopicGuideItemClickListener(this);
        mElementaryLists = root.findViewById(R.id.elementary_topic_list);
        mElementaryLists.setLayoutManager(new LinearLayoutManager(getContext()));
        mElementaryLists.setItemAnimator(new DefaultItemAnimator());
        mElementaryLists.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mElementaryLists.setAdapter(mElementaryAdapter);

        prepareElementaryData();

        // Intermediate
        mIntermediateAdapter = new TopicGuideListAdapter(getContext());
        mIntermediateAdapter.setItemLists(new ArrayList<TopicGuideModel>());
        mIntermediateAdapter.setTopicGuideItemClickListener(this);
        mIntermediateLists = root.findViewById(R.id.intermediate_topic_list);
        mIntermediateLists.setLayoutManager(new LinearLayoutManager(getContext()));
        mIntermediateLists.setItemAnimator(new DefaultItemAnimator());
        mIntermediateLists.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mIntermediateLists.setAdapter(mIntermediateAdapter);

        prepareIntermediateData();

        // Upper-intermediate
        mUpperIntermediateAdapter = new TopicGuideListAdapter(getContext());
        mUpperIntermediateAdapter.setItemLists(new ArrayList<TopicGuideModel>());
        mUpperIntermediateAdapter.setTopicGuideItemClickListener(this);
        mUpperIntermediateLists = root.findViewById(R.id.upper_intermediate_topic_list);
        mUpperIntermediateLists.setLayoutManager(new LinearLayoutManager(getContext()));
        mUpperIntermediateLists.setItemAnimator(new DefaultItemAnimator());
        mUpperIntermediateLists.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mUpperIntermediateLists.setAdapter(mUpperIntermediateAdapter);

        prepareUpperIntermediateData();

        // Advance
        mAdvanceAdapter = new TopicGuideListAdapter(getContext());
        mAdvanceAdapter.setItemLists(new ArrayList<TopicGuideModel>());
        mAdvanceAdapter.setTopicGuideItemClickListener(this);
        mAdvanceLists = root.findViewById(R.id.advance_topic_list);
        mAdvanceLists.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdvanceLists.setItemAnimator(new DefaultItemAnimator());
        mAdvanceLists.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mAdvanceLists.setAdapter(mAdvanceAdapter);

        prepareAdvanceData();

        return root;
    }

    // Elementary
    private void prepareElementaryData() {
         mFirestore.collection("skills")
                .document("1")
                .collection("topics")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {

                        List<TopicGuideModel> data = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : snapshots) {
                            TopicGuideModel model = snapshot.toObject(TopicGuideModel.class).withId(snapshot.getId());
                            data.add(model);
                        }
                        mElementaryAdapter.setItemLists(data);
                    }
                });

    }

    // Intermediate
    private void prepareIntermediateData() {
        mFirestore.collection("skills")
                .document("2")
                .collection("topics")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {

                        List<TopicGuideModel> data = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : snapshots) {
                            TopicGuideModel model = snapshot.toObject(TopicGuideModel.class).withId(snapshot.getId());
                            data.add(model);
                        }
                        mIntermediateAdapter.setItemLists(data);
                    }
                });
    }

    // Upper-intermediate
    private void prepareUpperIntermediateData() {
        mFirestore.collection("skills")
                .document("3")
                .collection("topics")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {

                        List<TopicGuideModel> data = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : snapshots) {
                            TopicGuideModel model = snapshot.toObject(TopicGuideModel.class).withId(snapshot.getId());
                            data.add(model);
                        }
                        mUpperIntermediateAdapter.setItemLists(data);
                    }
                });
    }

    // Advance
    private void prepareAdvanceData() {
        mFirestore.collection("skills")
                .document("4")
                .collection("topics")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {

                        List<TopicGuideModel> data = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : snapshots) {
                            TopicGuideModel model = snapshot.toObject(TopicGuideModel.class).withId(snapshot.getId());
                            data.add(model);
                        }
                        mAdvanceAdapter.setItemLists(data);
                    }
                });
    }

    @Override
    public void setOnItemClick(TopicGuideModel model) {
        Intent i = new Intent(getActivity(), TopicGuideDetailActivity.class);
        i.putExtra("TITLE", model.getTitle());
        i.putExtra("GUIDES", model.getGuides());
        startActivity(i);
    }

    @Override
    public void setOnButtonTopicDetailClick(TopicGuideModel model) {
        Intent i = new Intent(getActivity(), TopicGuideDetailActivity.class);
        i.putExtra("LEVEL", model.getLevel());
        i.putExtra("TITLE", model.getTitle());
        i.putExtra("GUIDES", model.getGuides());
        startActivity(i);
    }
}
