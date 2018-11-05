package com.team29.speakingpartners.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.CallingRequestListModel;
import com.team29.speakingpartners.model.UserModel;
import com.team29.speakingpartners.utils.GlideApp;
import com.team29.speakingpartners.utils.GlideOptions;

import javax.annotation.Nullable;

public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.PendingListViewHolder> {

    private FirebaseFirestore mFirestore;

    public static final String TAG = PendingListAdapter.class.getSimpleName();

    private Context mContext;
    private List<CallingRequestListModel> mLists;

    private ButtonItemClickListener buttonItemClickListener;

    public PendingListAdapter(Context mContext) {
        this.mContext = mContext;
        mFirestore = FirebaseFirestore.getInstance();
    }

    public PendingListAdapter(Context mContext, List<CallingRequestListModel> mLists) {
        this.mContext = mContext;
        this.mLists = mLists;
    }

    public void setItemLists(List<CallingRequestListModel> mLists) {
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
        CallingRequestListModel pendingListModel = mLists.get(i);
        pendingListViewHolder.bindView(pendingListModel);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public class PendingListViewHolder extends RecyclerView.ViewHolder {

        CallingRequestListModel mPendingUserModel;

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

        private void bindView(final CallingRequestListModel model) {
            this.mPendingUserModel = model;

            Query userQuery = mFirestore.collection("users")
                    .whereEqualTo("email", mPendingUserModel.getFrom_email());
            userQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e(TAG, "Error Snapshot");
                        return;
                    }

                    for (QueryDocumentSnapshot snapshot : snapshots) {

                        UserModel userModel = snapshot.toObject(UserModel.class).withId(snapshot.getId());

                        pendingUserName.setText(userModel.getUser_name());

                        if (!userModel.getUrl_photo().equals("")) {
                            imgProfile.setBackgroundDrawable(null);
                            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
                            circularProgressDrawable.setStrokeWidth(5f);
                            circularProgressDrawable.setCenterRadius(30f);
                            circularProgressDrawable.start();
                            GlideApp.with(mContext)
                                    .load(userModel.getUrl_photo())
                                    .apply(GlideOptions.circleCropTransform())
                                    .placeholder(circularProgressDrawable)
                                    .into(imgProfile);
                        }

                        if (userModel.getActive_status() == 0) {
                            pendingUserActiveStatus.setText(mContext.getResources().getString(R.string.str_offline));
                            pendingUserActiveStatus.setTextColor(mContext.getResources().getColor(R.color.color_grey));
                        } else if (userModel.getActive_status() == 1) {
                            pendingUserActiveStatus.setText(mContext.getResources().getString(R.string.str_user_active_now));
                            pendingUserActiveStatus.setTextColor(mContext.getResources().getColor(R.color.color_green));
                        }
                    }
                }
            });

            imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext.getApplicationContext(), pendingUserName.getText(), Toast.LENGTH_SHORT).show();
                }
            });

            btnPendingAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonItemClickListener.setOnAcceptButtonClick(mPendingUserModel,
                            mPendingUserModel.id);
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
        void setOnAcceptButtonClick(CallingRequestListModel model, String docId);

        void setOnRejectButtonClick();
    }

}
