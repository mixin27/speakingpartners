package com.team29.speakingpartners.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.UserModel;

public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.PendingListViewHolder> {

    public static final String TAG = PendingListAdapter.class.getSimpleName();

    Context mContext;
    private List<UserModel> mLists;

    public PendingListAdapter(Context mContext, List<UserModel> mLists) {
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
        UserModel pendingListModel = mLists.get(i);
        pendingListViewHolder.bindView(pendingListModel);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public class PendingListViewHolder extends RecyclerView.ViewHolder {

        UserModel mPendingListModel;

        AppCompatTextView pendingUserName, pendingUserActiveStatus;
        AppCompatButton btnReview;

        private PendingListViewHolder(@NonNull View itemView) {
            super(itemView);

            pendingUserName = itemView.findViewById(R.id.pending_user_name);
            pendingUserActiveStatus = itemView.findViewById(R.id.pending_user_active_status);

            btnReview = itemView.findViewById(R.id.btn_review);

        }

        private void bindView(UserModel model) {
            this.mPendingListModel = model;

            pendingUserName.setText(mPendingListModel.getUser_name());

            if (mPendingListModel.getActive_status() == 0) {
                pendingUserActiveStatus.setText(mContext.getResources().getString(R.string.str_offline));
            } else if (mPendingListModel.getActive_status() == 1) {
                pendingUserActiveStatus.setText(mContext.getResources().getString(R.string.str_online));
            }

            btnReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

    }

}
