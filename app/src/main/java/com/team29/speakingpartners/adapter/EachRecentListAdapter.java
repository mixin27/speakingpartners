package com.team29.speakingpartners.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;
import com.team29.speakingpartners.model.RecentListModel;

import com.team29.speakingpartners.R;

public class EachRecentListAdapter extends RecyclerView.Adapter<EachRecentListAdapter.EachRecentListViewHolder> {

    public static final String TAG = EachRecentListAdapter.class.getSimpleName();

    Context mContext;
    private List<RecentListModel> mLists;

    public EachRecentListAdapter(Context mContext, List<RecentListModel> mLists) {
        this.mContext = mContext;
        this.mLists = mLists;
    }

    @NonNull
    @Override
    public EachRecentListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.each_recent_list_row, viewGroup, false);
        return new EachRecentListViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull EachRecentListViewHolder holder, int i) {
        RecentListModel model = mLists.get(i);
        holder.bindView(model);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public class EachRecentListViewHolder extends RecyclerView.ViewHolder {

        RecentListModel mRecentListModel;

        AppCompatTextView tvRecentReplayTitle, tvRecordDate, tvDuration, tvCallType;

        public EachRecentListViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRecentReplayTitle = itemView.findViewById(R.id.recent_replay_title);
            tvRecordDate = itemView.findViewById(R.id.recent_replay_date_time);
            tvCallType = itemView.findViewById(R.id.recent_calling_type);
            tvDuration = itemView.findViewById(R.id.recent_replay_duration);

        }

        private void bindView(RecentListModel model) {
            this.mRecentListModel = model;

            if (model.getRecord_title() != null) {
                tvRecentReplayTitle.setText(model.getRecord_title());
            }

            if (mRecentListModel.getRecord_date() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                String date = sdf.format(mRecentListModel.getRecord_date());
                Log.d(TAG, "RECORD DATE = " + date);
                tvRecordDate.setText(date);
            }

            if (mRecentListModel.getCall_type() != null) {
                tvCallType.setText(mRecentListModel.getCall_type());
            }

            if (mRecentListModel.getRecord_duration() != null) {
                tvDuration.setText(mRecentListModel.getRecord_duration());
            }
        }
    }

}
