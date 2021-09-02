package com.toolsbox.customer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.toolsbox.customer.R;
import com.toolsbox.customer.common.model.AvailabilityDateInfo;
import com.toolsbox.customer.common.model.FromToDateInfo;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.toolsbox.customer.common.Constant.DATE_FORMAT_MMM_D_YYYY_dot;


public class AvailabilityAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "AvailabilityAdapter";
    private ArrayList<AvailabilityDateInfo> dataSet;
    private Context context;
    private boolean isEditable;

    private static final int VIEW_TYPE_ADD = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private OnItemClickListener listener;

    public AvailabilityAdapter(Context context, ArrayList<AvailabilityDateInfo> data, boolean isEditable) {
        this.context = context;
        this.dataSet = data;
        this.isEditable = isEditable;
    }



    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(AvailabilityDateInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<AvailabilityDateInfo> postItems) {
        for (AvailabilityDateInfo response : postItems) {
            add(response);
        }
    }

    public void remove(AvailabilityDateInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        int position = dataSet.size() - 1;
        AvailabilityDateInfo item = getItem(position);
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
        add(new AvailabilityDateInfo());
    }

    AvailabilityDateInfo getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        switch (viewType){
            case VIEW_TYPE_ADD:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_availability_add, parent, false));
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_availability_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == dataSet.size() ? VIEW_TYPE_ADD : VIEW_TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int listPosition) {
        holder.onBind(listPosition);
    }

    @Override
    public int getItemCount() {
        return dataSet == null? 1 : dataSet.size() + 1;
    }

    public class FooterHolder extends BaseViewHolder {
        TextView tvSupport;
        CardView cvAdd;
        FooterHolder(View itemView) {
            super(itemView);
            this.cvAdd = itemView.findViewById(R.id.cv_add_date);
            this.tvSupport = itemView.findViewById(R.id.tv_support);
            if (isEditable) {
                this.cvAdd.setVisibility(View.VISIBLE);
                this.tvSupport.setVisibility(View.GONE);
            } else {
                this.cvAdd.setVisibility(View.GONE);
                this.tvSupport.setVisibility(View.VISIBLE);
            }
            this.cvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemAddClick();
                }
            });
        }

        @Override
        protected void clear() {
        }

    }



    // ViewHolders

    public  class ViewHolder extends BaseViewHolder {
        TextView tvDate, tvTime;
        ImageButton btnRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvDate =  itemView.findViewById(R.id.tv_date);
            this.tvTime =  itemView.findViewById(R.id.tv_time);
            this.btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            AvailabilityDateInfo item = dataSet.get(position);
            String strDate = GlobalUtils.convertDateToString(item.timeStamp.get(0).fromDate, DATE_FORMAT_MMM_D_YYYY_dot);
            this.tvDate.setText(strDate);
            String strTimes = "";
            for (FromToDateInfo fItem : item.timeStamp) {
                String strFromTo = GlobalUtils.getStringStamp(fItem);
                strTimes = String.format("%s%s%s", strTimes, "\n", strFromTo);
            }
            if (strTimes.length() > 1) {
                strTimes = strTimes.substring(1);
            }
            this.tvTime.setText(strTimes);
            if (isEditable) {
                btnRemove.setImageDrawable(context.getDrawable(R.drawable.ic_close_gray));
                this.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onItemRemoveClick(item);
                        }
                    }
                });
            } else {
                btnRemove.setImageDrawable(null);
            }

        }
    }


    public interface OnItemClickListener {
        void onItemRemoveClick(AvailabilityDateInfo item);
        void onItemAddClick();
    }

}
