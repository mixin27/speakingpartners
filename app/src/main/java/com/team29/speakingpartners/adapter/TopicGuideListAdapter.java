package com.team29.speakingpartners.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.TopicGuideModel;

import java.util.List;

public class TopicGuideListAdapter extends RecyclerView.Adapter<TopicGuideListAdapter.TopicGuideViewHolder> {

    private List<TopicGuideModel> mLists;
    private TopicGuideItemClickListener mListener;
    Context mContext;

    public TopicGuideListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setItemLists(List<TopicGuideModel> lists) {
        this.mLists = lists;
        notifyDataSetChanged();
    }

    public void setTopicGuideItemClickListener(TopicGuideItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public TopicGuideViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.topic_list_row, viewGroup, false);
        return new TopicGuideViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicGuideViewHolder topicGuideViewHolder, int i) {
        TopicGuideModel model = mLists.get(i);
        topicGuideViewHolder.bindView(model);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public class TopicGuideViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TopicGuideModel topicGuideModel;
        AppCompatTextView topicName;
        AppCompatImageButton btnTopicDetail;

        public TopicGuideViewHolder(@NonNull View itemView) {
            super(itemView);

            topicName = itemView.findViewById(R.id.tv_topic_name);
            btnTopicDetail = itemView.findViewById(R.id.btn_topic_detail);
            itemView.setOnClickListener(this);
        }

        public void bindView(TopicGuideModel model) {
            this.topicGuideModel = model;

            topicName.setText(topicGuideModel.getTitle());

            btnTopicDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.setOnButtonTopicDetailClick(topicGuideModel);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.setOnItemClick(topicGuideModel);
        }
    }

    public interface TopicGuideItemClickListener {
        void setOnItemClick(TopicGuideModel model);

        void setOnButtonTopicDetailClick(TopicGuideModel model);
    }

}
