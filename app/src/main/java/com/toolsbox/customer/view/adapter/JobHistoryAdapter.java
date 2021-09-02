package com.toolsbox.customer.view.adapter;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.model.api.HistoryInfo;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.common.utils.TimeHelper;
import com.toolsbox.customer.view.activity.main.jobs.JobDetailActivity;
import com.toolsbox.customer.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class JobHistoryAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "JobHistoryAdapter";
    private ArrayList<HistoryInfo> dataSet;
    private Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private OnItemClickListener listener;

    public JobHistoryAdapter(Context context, ArrayList<HistoryInfo> data) {
        this.context = context;
        this.dataSet = data;
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(HistoryInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void updateAll(List<HistoryInfo> postItems) {
        for (HistoryInfo item : postItems) {
            if (!dataSet.contains(item)) {
                add(item);
            }
        }
    }

    public void addAll(List<HistoryInfo> postItems) {
        for (HistoryInfo response : postItems) {
            add(response);
        }
    }

    public void remove(HistoryInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = dataSet.size() - 1;
        HistoryInfo item = getItem(position);
        if (item != null) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoading() {
        isLoaderVisible = true;
        add(new HistoryInfo());
    }

    HistoryInfo getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        switch (viewType){
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_job_history, parent, false));
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible)
            return position == dataSet.size() - 1? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        else
            return VIEW_TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int listPosition) {
        holder.onBind(listPosition);
    }

    @Override
    public int getItemCount() {
        return dataSet == null? 0 : dataSet.size();
    }

    public class FooterHolder extends BaseViewHolder {
        ProgressBar mProgressBar;
        FooterHolder(View itemView) {
            super(itemView);
            this.mProgressBar = itemView.findViewById(R.id.progressbar);
        }

        @Override
        protected void clear() {
        }

    }



    // ViewHolders

    public  class ViewHolder extends BaseViewHolder {
        CardView cvItem;
        TextView tvJobName;
        TextView tvStatus;
        TextView tvIndustry;
        TextView tvPostedDate;
        TextView tvValue;
        TextView tvDateTitle;
        LinearLayout llValue, llIndustry;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvJobName =  itemView.findViewById(R.id.tv_job_name);
            this.tvStatus =  itemView.findViewById(R.id.tv_status);
            this.tvIndustry = itemView.findViewById(R.id.tv_industry);
            this.tvPostedDate =  itemView.findViewById(R.id.tv_posted_date);
            this.tvValue = itemView.findViewById(R.id.tv_value);
            this.tvDateTitle = itemView.findViewById(R.id.tv_date_title);
            this.cvItem = itemView.findViewById(R.id.cv_item);
            this.llValue = itemView.findViewById(R.id.ll_value);
            this.llIndustry = itemView.findViewById(R.id.ll_industry);
        }

        @Override
        protected void clear() {

        }
        public void onBind(int position) {
            super.onBind(position);
            HistoryInfo item = dataSet.get(position);
            tvJobName.setText(item.job.name);
            if (item.job.status == Constant.IN_BIDDING_PROCESS || item.job.status == Constant.PENDING_APPROVAL) {
                llValue.setVisibility(View.GONE);
                if (!StringHelper.isEmpty(item.job.quoted_contractors) &&  Arrays.asList(item.job.quoted_contractors.split(",")).size() > 0) {
                    tvStatus.setText(R.string.quotes_available);
                } else {
                    tvStatus.setText(R.string.pending_quotes);
                }
                String friendlyDates = TimeHelper.convertFrindlyTime(item.job.job_posted_date);
                tvPostedDate.setText(friendlyDates);
            } else if (item.job.status == Constant.SCHEDULED || item.job.status == Constant.EN_ROUTE) {
                llValue.setVisibility(View.GONE);
                if (item.job.status == Constant.SCHEDULED)
                    tvStatus.setText(R.string.on_schedule);
                else
                    tvStatus.setText(R.string.en_route);
                tvDateTitle.setText("Scheduled Date");
                String friendlyDates = TimeHelper.convertFrindlyTime(item.job.job_scheduled_date);
                tvPostedDate.setText(friendlyDates);
            } else if (item.job.status == Constant.IN_PROGRESS) {
                if (item.job.type == 0) {
                    tvValue.setText(String.format("%s%s", "$", item.job.accepted_budget));
                } else {
                    llValue.setVisibility(View.GONE);
                }
                tvStatus.setText(R.string.in_progress);
                tvDateTitle.setText("Start Date");
                String friendlyDates = TimeHelper.convertFrindlyTime(item.job.job_started_date);
                tvPostedDate.setText(friendlyDates);
            } else if (item.job.status == Constant.FINISH) {
                tvValue.setText(String.format("%s%s", "$", item.job.accepted_budget));
                tvStatus.setText(R.string.completed);
                tvDateTitle.setText("Completion Date");
                String friendlyDates = TimeHelper.convertFrindlyTime(item.job.job_completed_date);
                tvPostedDate.setText(friendlyDates);
            }

            tvIndustry.setText(Constant.gArrSpecialization[item.job.industry - 1]);
            cvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }


    public interface OnItemClickListener {
        void onItemClick(HistoryInfo item);
    }

}
