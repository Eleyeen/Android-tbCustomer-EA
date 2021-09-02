package com.toolsbox.customer.view.adapter;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.model.ContractorInfo;
import com.toolsbox.customer.common.model.api.HistoryInfo;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.view.activity.main.market.ReviewsActivity;
import com.toolsbox.customer.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ContractorAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "ContractorAdapter";
    private ArrayList<ContractorInfo> dataSet;
    private Context context;
    private OnItemClickListener listener;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public ContractorAdapter(Context context, ArrayList<ContractorInfo> data) {
        this.dataSet = data;
        this.context = context;
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(ContractorInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<ContractorInfo> postItems) {
        for (ContractorInfo response : postItems) {
            add(response);
        }
    }

    public void remove(ContractorInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = dataSet.size() - 1;
        ContractorInfo item = getItem(position);
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
        add(new ContractorInfo());
    }

    ContractorInfo getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        switch (viewType){
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_contractors, parent, false));
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

    public class ViewHolder extends BaseViewHolder {

        TextView tvName, tvIndustry, tvAbout, tvReview, tvHourlyRate;
        LinearLayout llReview;
        MaterialRatingBar rating;
        Button btnChat, btnHire;
        CircleImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = itemView.findViewById(R.id.iv_profile);
            this.tvName =  itemView.findViewById(R.id.tv_name);
            this.tvIndustry =  itemView.findViewById(R.id.tv_industry);
            this.tvAbout =  itemView.findViewById(R.id.tv_about);
            this.tvReview =  itemView.findViewById(R.id.tv_review);
            this.tvHourlyRate = itemView.findViewById(R.id.tv_hourly_rate);
            this.llReview =  itemView.findViewById(R.id.ll_review);
            this.rating =  itemView.findViewById(R.id.rating);
            this.btnChat = itemView.findViewById(R.id.btn_chat);
            this.btnHire = itemView.findViewById(R.id.btn_hire);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            ContractorInfo item = dataSet.get(position);
            tvName.setText(item.business_name);
            int industry = Integer.valueOf(Arrays.asList(item.industry.split(",")).get(0));
            tvIndustry.setText(Constant.gArrSpecialization[industry - 1]);
            tvAbout.setText(item.about);
            tvReview.setText("(" + item.review_count + ")");
            tvHourlyRate.setText("N/A");
            if (!StringHelper.isEmpty(item.hourly_rate)) {
                tvHourlyRate.setText("$" + item.hourly_rate);
            }
            rating.setRating(item.getReviewValue());
            rating.setIsIndicator(true);
            if (!StringHelper.isEmpty(item.image_url))
                Picasso.get().load(item.image_url).into(ivPhoto);
            if (PreferenceHelper.isLoginIn()) {
                llReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onTapReview(item);
                    }
                });
                btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onTapChat(item);
                    }
                });

                btnHire.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onTapHire(item);
                    }
                });
            }
        }
    }

    public interface OnItemClickListener {
        void onTapReview(ContractorInfo item);
        void onTapChat(ContractorInfo item);
        void onTapHire(ContractorInfo item);
    }

}
