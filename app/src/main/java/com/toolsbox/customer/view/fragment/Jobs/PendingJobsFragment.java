package com.toolsbox.customer.view.fragment.Jobs;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.model.api.HistoryInfo;
import com.toolsbox.customer.view.activity.main.jobs.JobDetailActivity;
import com.toolsbox.customer.view.adapter.JobHistoryAdapter;
import com.toolsbox.customer.view.fragment.JobsFragment;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PendingJobsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, JobHistoryAdapter.OnItemClickListener {
    private static String TAG = "PendingJobsFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private JobHistoryAdapter  adapter;
    private LinearLayoutManager layoutManager;
    private RelativeLayout rlNodata, rlLoadingData;
    private TextView tvNoTitle;
    private RefreshJobReceiver refreshJobReceiver;
    private static final int REQUEST_CODE_JOB_DETAIL = 1;

    public PendingJobsFragment() {}

    public static PendingJobsFragment newInstance(String param1, String param2) {
        PendingJobsFragment fragment = new PendingJobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_jobs, container, false);
        initUI(view);
        fetchJobs();
        return view;
    }

    void initUI(View view){
        rlNodata = view.findViewById(R.id.rl_no_data);
        tvNoTitle = view.findViewById(R.id.tv_no_title);
        rlLoadingData = view.findViewById(R.id.rl_loading_data);
        tvNoTitle.setText(R.string.no_pending_job);
        swipeRefresh = view.findViewById(R.id.refresh_swipe);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView =  view.findViewById(R.id.recycler_jobs);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new JobHistoryAdapter(getActivity(), new ArrayList<>());
        adapter.addListener(this);
        recyclerView.setAdapter(adapter);
        refreshJobReceiver = new RefreshJobReceiver();
        if (getActivity() != null)
            getActivity().registerReceiver(refreshJobReceiver, new IntentFilter(Constant.ACTION_REFRESH_JOBS));
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        // call parent job fragment
        JobsFragment parentFrag = (JobsFragment) PendingJobsFragment.this.getParentFragment();
        parentFrag.refreshJobs();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (getActivity() != null)
                getActivity().unregisterReceiver(refreshJobReceiver);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void fetchJobs(){
        swipeRefresh.setRefreshing(false);
        List<HistoryInfo> arrJobs = new ArrayList<>();
        for (HistoryInfo item : JobsFragment.getArrHistory()){
            if (item.job.status == Constant.IN_BIDDING_PROCESS || item.job.status == Constant.PENDING_APPROVAL) {
                arrJobs.add(item);
            }
        }
        adapter.updateAll(arrJobs);
        updateBackground();
    }

    void updateBackground(){
        JobsFragment parentFrag = (JobsFragment) PendingJobsFragment.this.getParentFragment();
        if (adapter.getItemCount() == 0) {
            if (parentFrag.isLoading) {
                rlLoadingData.setVisibility(View.VISIBLE);
                rlNodata.setVisibility(View.GONE);
            } else {
                rlLoadingData.setVisibility(View.GONE);
                rlNodata.setVisibility(View.VISIBLE);
            }
        } else {
            rlLoadingData.setVisibility(View.GONE);
            rlNodata.setVisibility(View.GONE);
        }

    }

    @Override
    public void onItemClick(HistoryInfo item) {
        Intent intent = new Intent(getActivity(), JobDetailActivity.class);
        intent.putExtra("jobId", item.job.id);
        startActivityForResult(intent, REQUEST_CODE_JOB_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_JOB_DETAIL:
                    onRefresh();
                    break;

            }
        }
    }

    private class RefreshJobReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            fetchJobs();
        }
    }
}
