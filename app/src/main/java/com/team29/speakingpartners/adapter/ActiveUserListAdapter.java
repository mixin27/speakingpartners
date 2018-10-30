package com.team29.speakingpartners.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.UserModel;

public class ActiveUserListAdapter extends RecyclerView.Adapter<ActiveUserListAdapter.ActiveUserViewHolder> {

    public static final String TAG = ActiveUserListAdapter.class.getSimpleName();

    Context mContext;
    private List<UserModel> mLists;

    public ActiveUserListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmLists(List<UserModel> mLists) {
        this.mLists = mLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActiveUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.active_user_list_row, viewGroup, false);
        return new ActiveUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveUserViewHolder activeUserViewHolder, int i) {
        UserModel model = mLists.get(i);
        activeUserViewHolder.bindView(model);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public void addItems(List<UserModel> list) {
        this.mLists = list;
        notifyDataSetChanged();
    }

    public class ActiveUserViewHolder extends RecyclerView.ViewHolder {

        private UserModel mActiveUserModel;

        AppCompatTextView tvUserName, tvUserLevel;
        AppCompatImageView imgUserProfile, imgActiveStatus;

        private ActiveUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.active_user_name);
            tvUserLevel = itemView.findViewById(R.id.active_user_level);

            imgUserProfile = itemView.findViewById(R.id.active_user_img);
            imgActiveStatus = itemView.findViewById(R.id.active_user_status);
        }

        private void bindView(UserModel model) {
            this.mActiveUserModel = model;

            if (mActiveUserModel.getUser_name() != null) {
                tvUserName.setText(mActiveUserModel.getUser_name());
            }

            if (mActiveUserModel.getLevel() != null) {
                tvUserLevel.setText(mActiveUserModel.getLevel());
            }

            if (mActiveUserModel.getActive_status() == 0) {
                imgActiveStatus.setColorFilter(mContext.getResources().getColor(R.color.color_grey));
            } else if (mActiveUserModel.getActive_status() == 1) {
                imgActiveStatus.setColorFilter(mContext.getResources().getColor(R.color.color_green));
            }

            imgUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext.getApplicationContext(), "Profile of " + tvUserName.getText(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }


}
