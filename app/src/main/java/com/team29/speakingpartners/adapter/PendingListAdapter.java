package com.team29.speakingpartners.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.UserModel;

public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.PendingListViewHolder> {

    public static final String TAG = PendingListAdapter.class.getSimpleName();

    private Context mContext;
    private List<UserModel> mLists;

    private ButtonItemClickListener buttonItemClickListener;

    public PendingListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public PendingListAdapter(Context mContext, List<UserModel> mLists) {
        this.mContext = mContext;
        this.mLists = mLists;
    }

    public void setItemLists(List<UserModel> mLists) {
        this.mLists = mLists;
        notifyDataSetChanged();
    }

    public void setButtonClickListener(ButtonItemClickListener listener) {
        this.buttonItemClickListener = listener;
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

        UserModel mPendingUserModel;

        AppCompatTextView pendingUserName, pendingUserActiveStatus;
        AppCompatButton btnPendingReject, btnPendingAccept;
        AppCompatImageView imgProfile;

        private PendingListViewHolder(@NonNull View itemView) {
            super(itemView);

            pendingUserName = itemView.findViewById(R.id.pending_user_name);
            pendingUserActiveStatus = itemView.findViewById(R.id.pending_user_active_status);

            imgProfile = itemView.findViewById(R.id.pending_user_img);

            btnPendingReject = itemView.findViewById(R.id.btn_pending_reject);
            btnPendingAccept = itemView.findViewById(R.id.btn_pending_accept);

        }

        private void bindView(UserModel model) {
            this.mPendingUserModel = model;

            pendingUserName.setText(mPendingUserModel.getUser_name());

            if (!mPendingUserModel.getUrl_photo().equals("")) {
                imgProfile.setBackgroundDrawable(null);
                Glide.with(mContext)
                        .load(mPendingUserModel.getUrl_photo())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgProfile);
            }
            imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext.getApplicationContext(), pendingUserName.getText(), Toast.LENGTH_SHORT).show();
                }
            });

            if (mPendingUserModel.getActive_status() == 0) {
                pendingUserActiveStatus.setText(mContext.getResources().getString(R.string.str_offline));
                pendingUserActiveStatus.setTextColor(mContext.getResources().getColor(R.color.color_grey));
            } else if (mPendingUserModel.getActive_status() == 1) {
                pendingUserActiveStatus.setText(mContext.getResources().getString(R.string.str_user_active_now));
                pendingUserActiveStatus.setTextColor(mContext.getResources().getColor(R.color.color_green));
            }

            btnPendingAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonItemClickListener.setOnAcceptButtonClick(mPendingUserModel);
                }
            });

            btnPendingReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonItemClickListener.setOnRejectButtonClick();
                }
            });
        }

    }

    public interface ButtonItemClickListener {
        void setOnAcceptButtonClick(UserModel userModel);

        void setOnRejectButtonClick();
    }

}
