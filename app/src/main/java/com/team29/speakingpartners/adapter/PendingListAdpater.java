package com.team29.speakingpartners.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.PendingListModel;

public class PendingListAdpater extends RecyclerView.Adapter<PendingListAdpater.PendingListViewHolder> {

    public static final String TAG = PendingListAdpater.class.getSimpleName();

    Context mContext;
    private List<PendingListModel> mLists;

    public PendingListAdpater(Context mContext, List<PendingListModel> mLists) {
        this.mContext = mContext;
        this.mLists = mLists;
    }

    @NonNull
    @Override
    public PendingListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pending_list_row, viewGroup, false);
        return new PendingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingListViewHolder pendingListViewHolder, int i) {
        PendingListModel pendingListModel = mLists.get(i);
        pendingListViewHolder.bindView(pendingListModel);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public class PendingListViewHolder extends RecyclerView.ViewHolder {

        PendingListModel mPendingListModel;

        AppCompatTextView pendingUserName, pendingUserLevel, pendingAccept, pendingReject;

        private PendingListViewHolder(@NonNull View itemView) {
            super(itemView);

            pendingUserName = itemView.findViewById(R.id.pending_user_name);
            pendingUserLevel = itemView.findViewById(R.id.pending_user_level);

            pendingAccept = itemView.findViewById(R.id.pending_accept);
            pendingAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext.getApplicationContext(), "Accepted", Toast.LENGTH_SHORT).show();
                }
            });

            pendingReject = itemView.findViewById(R.id.pending_reject);
            pendingReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext.getApplicationContext(), "Rejected", Toast.LENGTH_SHORT).show();
                }
            });

        }

        private void bindView(PendingListModel model) {
            this.mPendingListModel = model;

            pendingUserName.setText(mPendingListModel.getUser_name());
            pendingUserLevel.setText(mPendingListModel.getLevel());

        }

    }

}
