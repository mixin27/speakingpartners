package com.team29.speakingpartners.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.UserModel;
import com.team29.speakingpartners.utils.GlideApp;
import com.team29.speakingpartners.utils.GlideOptions;

public class ActiveUserListAdapter extends RecyclerView.Adapter<ActiveUserListAdapter.ActiveUserViewHolder> {

    public static final String TAG = ActiveUserListAdapter.class.getSimpleName();

    private Context mContext;
    private List<UserModel> mLists;

    private ItemClickListener mItemClickListener;

    public class ActiveUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private UserModel mActiveUserModel;

        AppCompatTextView tvUserName, tvUserLevel;
        AppCompatImageView imgUserProfile, imgActiveStatus;

        private ActiveUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.active_user_name);
            tvUserLevel = itemView.findViewById(R.id.active_user_level);

            imgUserProfile = itemView.findViewById(R.id.active_user_img);
            imgActiveStatus = itemView.findViewById(R.id.active_user_status);

            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        private void bindView(UserModel model) {
            this.mActiveUserModel = model;

            if (mActiveUserModel.getUser_name() != null) {
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(mActiveUserModel.getEmail())) {
                    tvUserName.setText(mActiveUserModel.getUser_name() + mContext.getString(R.string.str_you));
                    imgActiveStatus.setVisibility(View.GONE);
                } else {
                    tvUserName.setText(mActiveUserModel.getUser_name());
                    imgActiveStatus.setVisibility(View.VISIBLE);
                }
            }

            if (mActiveUserModel.getLevel() != null) {
                tvUserLevel.setText(mActiveUserModel.getLevel());
            }

            if (mActiveUserModel.getActive_status() == 0) {
                imgActiveStatus.setColorFilter(mContext.getResources().getColor(R.color.color_grey));
            } else if (mActiveUserModel.getActive_status() == 1) {
                imgActiveStatus.setColorFilter(mContext.getResources().getColor(R.color.color_green));
            }

            if (!mActiveUserModel.getUrl_photo().equals("")) {
                imgUserProfile.setBackgroundDrawable(null);
                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f);
                circularProgressDrawable.start();
                GlideApp.with(mContext)
                        .load(mActiveUserModel.getUrl_photo())
                        .apply(GlideOptions.circleCropTransform())
                        .placeholder(circularProgressDrawable)
                        .into(imgUserProfile);
            }
            imgUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext.getApplicationContext(), tvUserName.getText(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onClick(View v) {
            mItemClickListener.setOnItemClick(mActiveUserModel);
        }
    }

    public ActiveUserListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setItemLists(List<UserModel> mLists) {
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
    public void onBindViewHolder(@NonNull final ActiveUserViewHolder activeUserViewHolder, int i) {
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

    public interface ItemClickListener {
        void setOnItemClick(UserModel userModel);
    }

}
