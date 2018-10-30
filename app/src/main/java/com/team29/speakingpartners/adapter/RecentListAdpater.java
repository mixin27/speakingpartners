package com.team29.speakingpartners.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.RecentListModel;

public class RecentListAdpater extends RecyclerView.Adapter<RecentListAdpater.RecentListViewHolder> {

    public static final String TAG = RecentListAdpater.class.getSimpleName();

    Context mContext;
    private List<RecentListModel> mLists;

    public RecentListAdpater(Context mContext, List<RecentListModel> mLists) {
        this.mContext = mContext;
        this.mLists = mLists;
    }

    @NonNull
    @Override
    public RecentListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_list_row, viewGroup, false);
        return new RecentListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentListViewHolder recentListViewHolder, int i) {
        RecentListModel recentListModel = mLists.get(i);
        recentListViewHolder.bindView(recentListModel);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public class RecentListViewHolder extends RecyclerView.ViewHolder {

        RecentListModel mRecentListModel;

        AppCompatTextView recentUsername, recentUserLevel, recentUserActiveStatus;
        AppCompatImageView imgRecentUserProfile, imgRecentUserGender;

        public RecentListViewHolder(@NonNull View itemView) {
            super(itemView);
            recentUsername = itemView.findViewById(R.id.recent_user_name);
            recentUserLevel = itemView.findViewById(R.id.recent_user_level);
            recentUserActiveStatus = itemView.findViewById(R.id.recent_user_active_status);

            imgRecentUserGender = itemView.findViewById(R.id.recent_user_gender_img);
            imgRecentUserProfile = itemView.findViewById(R.id.recent_user_img);
        }

        private void bindView(RecentListModel model) {
            this.mRecentListModel = model;
            recentUsername.setText(model.getUser_name());
            recentUserLevel.setText("Native");
        }
    }

}
