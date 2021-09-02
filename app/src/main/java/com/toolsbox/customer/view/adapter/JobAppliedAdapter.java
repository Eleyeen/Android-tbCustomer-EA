package com.toolsbox.customer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.model.AttachFileInfo;
import com.toolsbox.customer.common.model.ProposalInfo;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class JobAppliedAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "JobAppliedAdapter";
    private ArrayList<ProposalInfo> dataSet;
    private Context context;
    private OnItemClickListener listener;


    public JobAppliedAdapter(Context context, ArrayList<ProposalInfo> data) {
        this.dataSet = data;
        this.context = context;
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(ProposalInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<ProposalInfo> postItems) {
        for (ProposalInfo response : postItems) {
            add(response);
        }
    }

    public void remove(ProposalInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        int position = dataSet.size() - 1;
        ProposalInfo item = getItem(position);
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
        add(new ProposalInfo());
    }

    ProposalInfo getItem(int position) {
        return dataSet.get(position);
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_job_applied, parent, false));
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int listPosition) {
        holder.onBind(listPosition);
    }

    @Override
    public int getItemCount() {
        return dataSet == null? 0 : dataSet.size();
    }


    public class ViewHolder extends BaseViewHolder {

        CircleImageView ivPhoto;
        TextView tvValue, tvValueTitle, tvContractorName, tvScheduledDate, tvReviewsNumber, tvDetails;
        MaterialRatingBar overallRating;
        Button btnChat, btnReview, btnAccept;
        LinearLayout llButtons;
        RecyclerView recyclerView;
        JobAttachAdapter adapter;
        ArrayList<AttachFileInfo> dataAttach = new ArrayList<>();

        public ViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = itemView.findViewById(R.id.iv_photo);
            this.tvValue = itemView.findViewById(R.id.tv_value);
            this.tvValueTitle = itemView.findViewById(R.id.tv_value_title);
            this.tvContractorName = itemView.findViewById(R.id.tv_name);
            this.tvScheduledDate = itemView.findViewById(R.id.tv_scheduled_date);
            this.tvReviewsNumber = itemView.findViewById(R.id.tv_review_count);
            this.tvDetails = itemView.findViewById(R.id.tv_description);
            this.overallRating =  itemView.findViewById(R.id.overall_rating);
            this.overallRating.setIsIndicator(true);
            this.btnAccept = itemView.findViewById(R.id.btn_accept);
            this.btnChat = itemView.findViewById(R.id.btn_chat);
            this.btnReview = itemView.findViewById(R.id.btn_review);
            this.llButtons = itemView.findViewById(R.id.ll_buttons);
            this.recyclerView = itemView.findViewById(R.id.recycler);
            this.recyclerView.setHasFixedSize(false);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            this.adapter = new JobAttachAdapter(context, dataAttach);
            this.adapter.addListener(new JobAttachAdapter.OnItemClickListener() {
                @Override
                public void onItemOpen(AttachFileInfo item) {
                    if (listener != null) {
                        listener.onTapFileView(item.url);
                    }
                }
            });
            recyclerView.setAdapter(adapter);

        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            ProposalInfo item = dataSet.get(position);
            if (!StringHelper.isEmpty(item.contractor.image_url))
                Picasso.get().load(item.contractor.image_url).into(ivPhoto);
            if (item.type == 0) {
                tvValueTitle.setText(R.string.quotation_value);
                tvValue.setText("$" + item.budget);
            } else {
                tvValueTitle.setText(R.string.hourly_rate);
                tvValue.setText("$" + item.hourly_rate);
            }

            tvContractorName.setText(item.contractor.business_name);
            tvDetails.setText(item.description);
            tvReviewsNumber.setText(String.valueOf(item.contractor.review_count));
            tvScheduledDate.setText(GlobalUtils.convertScheduleRange(item.availability_start_dates));

            if (!StringHelper.isEmpty(item.attachment)) {
                for (String fItem : Arrays.asList(item.attachment.split(","))) {
                    dataAttach.add(new AttachFileInfo(GlobalUtils.fetchDownloadFileName(fItem), fItem, "", false));
                }
                adapter.notifyDataSetChanged();
            }

            if (item.status == Constant.QUOTE_PENDING || item.status == Constant.QUOTE_AWARDED){
                btnAccept.setEnabled(true);
            }else {
                btnAccept.setEnabled(false);
            }
            if (item.job.status >= Constant.SCHEDULED) {
                llButtons.setVisibility(View.GONE);
            }

            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTapChat(item);
                }
            });
            btnReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTapReview(item);
                }
            });

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTapAccept(item);
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onTapReview(ProposalInfo item);
        void onTapChat(ProposalInfo item);
        void onTapAccept(ProposalInfo item);
        void onTapDecline(ProposalInfo item);
        void onTapFileView(String url);
    }
}
